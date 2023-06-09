package mod.adrenix.nostalgic.network.packet;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.gui.toast.ToastNotification;
import mod.adrenix.nostalgic.client.config.reflect.ClientReflect;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.auto.AutoConfig;
import mod.adrenix.nostalgic.common.config.list.ConfigList;
import mod.adrenix.nostalgic.common.config.list.ListMap;
import mod.adrenix.nostalgic.common.config.list.ListSet;
import mod.adrenix.nostalgic.common.config.reflect.TweakStatus;
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
    /**
     * Register this packet to the mod's network channel.
     * Channel registration is handled by Architectury.
     */
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

    /* Fields */

    private final String json;

    /* Constructors */

    /**
     * Create a new tweak update packet with a tweak server cache instance.
     * This creates a packet using a serialized JSON string.
     *
     * @param tweak A tweak server cache instance.
     */
    public PacketS2CTweakUpdate(TweakServerCache<?> tweak) { this.json = new TweakSerializer(tweak).serialize(); }

    /**
     * Create a new tweak update packet with a buffer.
     * This decodes a packet into a JSON string.
     *
     * @param buffer A friendly byte buffer instance.
     */
    public PacketS2CTweakUpdate(FriendlyByteBuf buffer) { this.json = buffer.readUtf(); }

    /* Methods */

    /**
     * Encode data into the packet.
     * @param buffer A friendly byte buffer instance.
     */
    public void encode(FriendlyByteBuf buffer) { buffer.writeUtf(this.json); }

    /**
     * Handle any received tweak update packet data.
     * @param supplier A supplier that provides network packet context.
     */
    public void handle(Supplier<NetworkManager.PacketContext> supplier)
    {
        // Client received packet data

        /*
            WARNING:

            Although the client is handling the received packet data, no client classes can be used here since the
            server will be class loading this packet.
         */

        supplier.get().queue(() -> this.process(supplier.get()));
    }

    /**
     * Process packet data.
     * @param context Network manager packet context.
     */
    private void process(NetworkManager.PacketContext context)
    {
        if (context.getEnv() == EnvType.SERVER)
        {
            PacketUtil.warn(EnvType.SERVER, this.getClass());
            return;
        }

        TweakSerializer serializer = TweakSerializer.deserialize(this.json);
        TweakServerCache<?> cache = TweakServerCache.get(serializer.getGroup(), serializer.getKey());

        boolean isCached = cache != null;
        boolean isValueMatched = isCached && cache.getValue().getClass().equals(serializer.getValue().getClass());
        boolean isListMatched = isCached && cache.getList() != null && cache.getList().id() == serializer.getListId();

        // Check if sent value is a list, and if so, ensure list ids received over the wire matches what is cached
        if (isListMatched)
        {
            ListSet listSet = ConfigList.getSetFromId(serializer.getListId());
            ListMap<?> listMap = ConfigList.getMapFromId(serializer.getListId());

            if (listSet != null)
            {
                listSet.getConfigSet().clear();
                listSet.getConfigSet().addAll(serializer.getValue());

                listSet.getDisabledDefaults().clear();
                listSet.getDisabledDefaults().addAll(serializer.getDisabledDefaults());
            }
            else if (listMap != null)
            {
                listMap.getConfigMap().clear();
                listMap.getConfigMap().putAll(serializer.getValue());

                listMap.getDisabledDefaults().clear();
                listMap.getDisabledDefaults().addAll(serializer.getDisabledDefaults());
            }

            // Notify the client that a tweak list was updated
            if (NostalgicTweaks.isNetworkVerified())
                ToastNotification.gotChanges();

            // Update the client's config if this is a LAN session
            if (NetUtil.isLocalHost())
                AutoConfig.getConfigHolder(ClientConfig.class).save();

            // Add debug information to console
            String information = String.format
            (
                "Updated client's server list cache in group (%s) and key (%s)",
                LogColor.apply(LogColor.LIGHT_PURPLE, serializer.getGroup().toString()),
                LogColor.apply(LogColor.GREEN, serializer.getKey())
            );

            NostalgicTweaks.LOGGER.debug(information);

            return;
        }

        // Ensure cache is available and that the class value received over the wire matches what is cached
        if (isValueMatched)
        {
            boolean isValueChanged = !cache.getServerCache().equals(serializer.getValue());

            cache.setValue(serializer.getValue());
            cache.setStatus(serializer.getStatus());
            cache.setServerCache(serializer.getValue());

            // Notify client that a tweak was updated
            if (NostalgicTweaks.isNetworkVerified() && isValueChanged)
                ToastNotification.gotChanges();

            // Update the client's config if this is a LAN session
            if (NetUtil.isLocalHost() && isValueChanged)
            {
                TweakClientCache.get(serializer.getGroup(), serializer.getKey()).setValue(serializer.getValue(), true);
                ClientReflect.setConfig(serializer.getGroup(), serializer.getKey(), serializer.getValue());
                AutoConfig.getConfigHolder(ClientConfig.class).save();
            }

            // Add debug information to console
            String information = String.format
            (
                "Updated client's server cache in group (%s) and key (%s) with value (%s) and status (%s)",
                LogColor.apply(LogColor.LIGHT_PURPLE, serializer.getGroup().toString()),
                LogColor.apply(LogColor.GREEN, serializer.getKey()),
                LogColor.apply(LogColor.BLUE, serializer.getValue().toString()),
                TweakStatus.toStringWithColor(serializer.getStatus())
            );

            NostalgicTweaks.LOGGER.debug(information);
        }
        else if (cache == null)
        {
            String warning = String.format
            (
                "Client's deserialized data with group (%s) and key (%s) could not be found in tweak server cache",
                LogColor.apply(LogColor.LIGHT_PURPLE, serializer.getGroup().toString()),
                LogColor.apply(LogColor.GREEN, serializer.getKey())
            );

            NostalgicTweaks.LOGGER.warn(warning);
        }
        else
        {
            String warning = String.format
            (
                "Client's server cache (%s) didn't match deserialized (%s)",
                LogColor.apply(LogColor.GREEN, cache.getValue().getClass().toString()),
                LogColor.apply(LogColor.RED, serializer.getValue().getClass().toString())
            );

            NostalgicTweaks.LOGGER.warn(warning);
        }
    }
}
