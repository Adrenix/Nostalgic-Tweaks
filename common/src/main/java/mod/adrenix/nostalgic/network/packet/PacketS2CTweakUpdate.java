package mod.adrenix.nostalgic.network.packet;

import dev.architectury.networking.NetworkManager;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.gui.ToastNotification;
import mod.adrenix.nostalgic.client.config.reflect.ClientReflect;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.common.config.tweak.TweakSerializer;
import mod.adrenix.nostalgic.util.common.log.LogColor;
import mod.adrenix.nostalgic.util.client.NetUtil;
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
        NostalgicTweaks.NETWORK.register
        (
            PacketS2CTweakUpdate.class,
            PacketS2CTweakUpdate::encode,
            PacketS2CTweakUpdate::new,
            PacketS2CTweakUpdate::handle
        );
    }

    private final String json;

    public PacketS2CTweakUpdate(TweakServerCache<?> tweak)
    {
        // Packet creation
        this.json = new TweakSerializer(tweak).serialize();
    }

    public PacketS2CTweakUpdate(FriendlyByteBuf buffer)
    {
        // Decode packet into data
        this.json = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        // Encode data into packet
        buffer.writeUtf(this.json);
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier)
    {
        // Client received packet data
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

            // Ensure cache is available and that the class value received over the wire matches what is cached.
            if (serverCache != null && serverCache.getValue().getClass().equals(serializer.getValue().getClass()))
            {
                boolean isValueChanged = !serverCache.getServerCache().equals(serializer.getValue());

                serverCache.setValue(serializer.getValue());
                serverCache.setStatus(serializer.getStatus());
                serverCache.setServerCache(serializer.getValue());

                // Notify client that a tweak was updated
                if (NostalgicTweaks.isNetworkVerified() && isValueChanged)
                    ToastNotification.addTweakUpdate();

                // Update the client's config if this is a LAN session
                if (NetUtil.isLocalHost() && isValueChanged)
                {
                    TweakClientCache.get(serializer.getGroup(), serializer.getKey()).setCurrent(serializer.getValue(), true);
                    ClientReflect.setConfig(serializer.getGroup(), serializer.getKey(), serializer.getValue());
                    AutoConfig.getConfigHolder(ClientConfig.class).save();
                }

                // Add debug information to console
                String information = String.format(
                    "Updated client's server cache in group (%s) and key (%s) with value (%s) and status (%s)",
                    LogColor.apply(LogColor.LIGHT_PURPLE, serializer.getGroup().toString()),
                    LogColor.apply(LogColor.GREEN, serializer.getKey()),
                    LogColor.apply(LogColor.BLUE, serializer.getValue().toString()),
                    StatusType.toStringWithColor(serializer.getStatus())
                );

                NostalgicTweaks.LOGGER.debug(information);
            }
            else if (serverCache == null)
            {
                String warning = String.format(
                    "Client's deserialized data with group (%s) and key (%s) could not be found in tweak server cache",
                    LogColor.apply(LogColor.LIGHT_PURPLE, serializer.getGroup().toString()),
                    LogColor.apply(LogColor.GREEN, serializer.getKey())
                );

                NostalgicTweaks.LOGGER.warn(warning);
            }
            else
            {
                String warning = String.format(
                    "Client's server cache (%s) didn't match deserialized (%s)",
                    LogColor.apply(LogColor.GREEN, serverCache.getValue().getClass().toString()),
                    LogColor.apply(LogColor.RED, serializer.getValue().getClass().toString())
                );

                NostalgicTweaks.LOGGER.warn(warning);
            }
        });
    }
}
