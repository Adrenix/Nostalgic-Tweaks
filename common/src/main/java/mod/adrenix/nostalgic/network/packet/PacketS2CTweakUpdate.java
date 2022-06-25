package mod.adrenix.nostalgic.network.packet;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.gui.ToastNotification;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.common.config.tweak.TweakSerializer;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import net.fabricmc.api.EnvType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

/**
 * This packet sends a tweak state to the client.
 * The client will process the packet and update its server config cache.
 */

public class PacketS2CTweakUpdate
{
    public static void register()
    {
        NostalgicTweaks.NETWORK.register(
            PacketS2CTweakUpdate.class,
            PacketS2CTweakUpdate::encode,
            PacketS2CTweakUpdate::new,
            PacketS2CTweakUpdate::handle
        );
    }

    private final String json;

    public PacketS2CTweakUpdate(TweakServerCache<?> tweak)
    {
        // Packet Creation

        this.json = new TweakSerializer(tweak).serialize();
    }

    public PacketS2CTweakUpdate(FriendlyByteBuf buffer)
    {
        // Decode Packet into Data

        this.json = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        // Encode Data into Packet

        buffer.writeUtf(this.json);
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier)
    {
        // Client Received Packet Data
        /*
            WARNING:

            Although the client is handling the received packet data, no client classes can be used here since the
            server will be class loading this packet.
         */

        NetworkManager.PacketContext context = supplier.get();
        context.queue(() -> {
            if (context.getEnv() == EnvType.SERVER)
            {
                PacketUtil.warn(EnvType.SERVER, this.getClass());
                return;
            }

            TweakSerializer serializer = TweakSerializer.deserialize(this.json);
            TweakServerCache<?> serverCache = TweakServerCache.get(serializer.getGroup(), serializer.getKey());

            if (serverCache != null && serverCache.getValue().getClass().equals(serializer.getValue().getClass()))
            {
                boolean isValueChanged = !serverCache.getValue().equals(serializer.getValue());

                serverCache.setValue(serializer.getValue());
                serverCache.setStatus(serializer.getStatus());
                serverCache.setServerCache(serializer.getValue());

                if (isValueChanged)
                    ToastNotification.addTweakUpdate();

                String information = String.format(
                    "Updated client's server cache in group (%s) and key (%s) with value (%s) and status (%s)",
                    serializer.getGroup(),
                    serializer.getKey(),
                    serializer.getValue(),
                    serializer.getStatus()
                );

                NostalgicTweaks.LOGGER.info(information);
            }
            else if (serverCache == null)
            {
                String warning = String.format(
                    "Client's deserialized data with group (%s) and key (%s) could not be found in tweak server cache",
                    serializer.getGroup(),
                    serializer.getKey()
                );

                NostalgicTweaks.LOGGER.warn(warning);
            }
            else
            {
                String warning = String.format(
                    "Client's server cache (%s) didn't match deserialized (%s)",
                    serverCache.getValue().getClass(),
                    serializer.getValue().getClass()
                );

                NostalgicTweaks.LOGGER.warn(warning);
            }
        });
    }
}
