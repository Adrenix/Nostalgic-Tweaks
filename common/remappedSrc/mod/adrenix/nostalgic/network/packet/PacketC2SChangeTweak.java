package mod.adrenix.nostalgic.network.packet;

import dev.architectury.networking.NetworkManager;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.common.config.tweak.TweakSerializer;
import mod.adrenix.nostalgic.server.config.ServerConfig;
import mod.adrenix.nostalgic.server.config.reflect.ServerReflect;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import net.fabricmc.api.EnvType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.List;
import java.util.function.Supplier;

/**
 * This packet notifies the server that an operator has updated a tweak value.
 * The server must verify the player is indeed an operator before making changes and syncing clients.
 */

public class PacketC2SChangeTweak
{
    public static void register()
    {
        NostalgicTweaks.NETWORK.register
        (
            PacketC2SChangeTweak.class,
            PacketC2SChangeTweak::encode,
            PacketC2SChangeTweak::new,
            PacketC2SChangeTweak::handle
        );
    }

    private final String json;

    public PacketC2SChangeTweak(TweakServerCache<?> tweak)
    {
        // Packet creation
        this.json = new TweakSerializer(tweak).serialize();
    }

    public PacketC2SChangeTweak(PacketByteBuf buffer)
    {
        // Decode packet into data
        this.json = buffer.readString();
    }

    public void encode(PacketByteBuf buffer)
    {
        // Encode data into packet
        buffer.writeString(this.json);
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier)
    {
        // Server received packet data

        NetworkManager.PacketContext context = supplier.get();
        context.queue(() -> {
            if (context.getEnv() == EnvType.CLIENT)
            {
                PacketUtil.warn(EnvType.CLIENT, this.getClass());
                return;
            }

            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
            boolean isOperator = PacketUtil.isPlayerOp(player);

            if (!isOperator)
            {
                NostalgicTweaks.LOGGER.warn(String.format("Player (%s) tried changing a tweak without permission", player.getDisplayName().getString()));
                return;
            }

            TweakSerializer serializer = TweakSerializer.deserialize(this.json);
            TweakServerCache<?> cache = TweakServerCache.get(serializer.getGroup(), serializer.getKey());

            // Check if value received from client matches server cached value
            if (cache != null && cache.getValue().getClass().equals(serializer.getValue().getClass()))
            {
                // Even though this packet is handled by the server, we don't want singleplayer worlds to access the server config.
                if (NostalgicTweaks.isServer())
                {
                    // Save to server config
                    ServerReflect.setConfig(serializer.getGroup(), serializer.getKey(), serializer.getValue());
                    AutoConfig.getConfigHolder(ServerConfig.class).save();
                }

                // Update tweak cache before sending it over in a packet
                cache.setStatus(serializer.getStatus());
                cache.setValue(serializer.getValue());

                // Send tweak update to all connected players
                List<ServerPlayerEntity> players = player.server.getPlayerManager().getPlayerList();
                PacketUtil.sendToAll(players, new PacketS2CTweakUpdate(cache));

                String information = String.format(
                    "Updated server config entry in group (%s) with key (%s) with new value (%s)",
                    serializer.getGroup(),
                    serializer.getKey(),
                    serializer.getValue()
                );

                NostalgicTweaks.LOGGER.debug(information);
            }
            else if (cache == null)
            {
                String warning = String.format(
                    "Server's deserialized data in group (%s) with key (%s) could not be found in tweak server cache",
                    serializer.getGroup(),
                    serializer.getKey()
                );

                NostalgicTweaks.LOGGER.warn(warning);
            }
            else
            {
                String warning = String.format(
                    "Server's tweak cache (%s) didn't match client's sent deserialized (%s)",
                    cache.getValue().getClass(),
                    serializer.getValue().getClass()
                );

                NostalgicTweaks.LOGGER.warn(warning);
            }
        });
    }
}
