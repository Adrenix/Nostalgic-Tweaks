package mod.adrenix.nostalgic.network.packet;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.auto.AutoConfig;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.common.config.tweak.TweakSerializer;
import mod.adrenix.nostalgic.server.config.ServerConfig;
import mod.adrenix.nostalgic.server.config.reflect.ServerReflect;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import net.fabricmc.api.EnvType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.function.Supplier;

/**
 * This packet notifies the server that an operator has updated a tweak value.
 * The server must verify the player is indeed an operator before making changes and syncing clients.
 */

public class PacketC2SChangeTweak
{
    /**
     * Register this packet to the mod's network channel.
     * Channel registration is handled by Architectury.
     */
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

    /* Fields */

    private final String json;

    /* Constructors */

    /**
     * Create a new change tweak packet with a tweak server cache instance.
     * This creates a packet using a serialized JSON string.
     *
     * @param tweak A tweak server cache instance.
     */
    public PacketC2SChangeTweak(TweakServerCache<?> tweak)
    {
        this.json = new TweakSerializer(tweak).serialize();
    }

    /**
     * Create a new change tweak packet with a buffer.
     * This decodes a packet into a JSON string.
     *
     * @param buffer A friendly byte buffer instance.
     */
    public PacketC2SChangeTweak(FriendlyByteBuf buffer)
    {
        this.json = buffer.readUtf();
    }

    /* Methods */

    /**
     * Encode data into the packet.
     * @param buffer A friendly byte buffer instance.
     */
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.json);
    }

    /**
     * Handle packet data.
     * @param supplier A packet context supplier.
     */
    public void handle(Supplier<NetworkManager.PacketContext> supplier)
    {
        // Server received packet data
        supplier.get().queue(() -> this.process(supplier.get()));
    }

    /**
     * Process packet data.
     * @param context Network manager packet context.
     */
    private void process(NetworkManager.PacketContext context)
    {
        if (context.getEnv() == EnvType.CLIENT)
        {
            PacketUtil.warn(EnvType.CLIENT, this.getClass());
            return;
        }

        ServerPlayer player = (ServerPlayer) context.getPlayer();
        boolean isOperator = PacketUtil.isPlayerOp(player);

        if (!isOperator)
        {
            String warn = String.format("Player (%s) tried changing a tweak without permission", player.getDisplayName().getString());
            NostalgicTweaks.LOGGER.warn(warn);

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
            List<ServerPlayer> players = player.server.getPlayerList().getPlayers();
            PacketUtil.sendToAll(players, new PacketS2CTweakUpdate(cache));

            String information = String.format
            (
                "%s updated server config entry in group (%s) with key (%s) with new value (%s)",
                player.getDisplayName().getString(),
                serializer.getGroup(),
                serializer.getKey(),
                serializer.getValue()
            );

            if (NostalgicTweaks.isServer())
                NostalgicTweaks.LOGGER.info(information);
            else
                NostalgicTweaks.LOGGER.debug(information);
        }
        else if (cache == null)
        {
            String warning = String.format
            (
                "Server's deserialized data in group (%s) with key (%s) could not be found in tweak server cache",
                serializer.getGroup(),
                serializer.getKey()
            );

            NostalgicTweaks.LOGGER.warn(warning);
        }
        else
        {
            String warning = String.format
            (
                "Server's tweak cache (%s) didn't match client's sent deserialized (%s)",
                cache.getValue().getClass(),
                serializer.getValue().getClass()
            );

            NostalgicTweaks.LOGGER.warn(warning);
        }
    }
}
