package mod.adrenix.nostalgic.common.config.v2.network.packet;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.gui.toast.ToastNotification;
import mod.adrenix.nostalgic.common.config.reflect.TweakStatus;
import mod.adrenix.nostalgic.common.config.v2.cache.ConfigCache;
import mod.adrenix.nostalgic.common.config.v2.network.TweakSerializer;
import mod.adrenix.nostalgic.common.config.v2.tweak.Tweak;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import mod.adrenix.nostalgic.util.common.log.LogColor;
import net.fabricmc.api.EnvType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

/**
 * This packet sends a tweak update to the client.
 * The client will process the packet and update the tweak's sent server value field.
 */

public class v2PacketS2CTweakUpdate
{
    /**
     * Register this packet to the mod's network channel.
     * Channel registration is handled by Architectury.
     */
    public static void register()
    {
        NostalgicTweaks.NETWORK.register
        (
            v2PacketS2CTweakUpdate.class,
            v2PacketS2CTweakUpdate::encode,
            v2PacketS2CTweakUpdate::new,
            v2PacketS2CTweakUpdate::handle
        );
    }

    /* Fields */

    private final String json;

    /* Constructors */

    /**
     * Create a new tweak update packet with a tweak instance.
     * This creates a packet using a serialized JSON string.
     *
     * @param tweak A tweak instance.
     */
    public v2PacketS2CTweakUpdate(Tweak<?> tweak)
    {
        this.json = new TweakSerializer<>(tweak).serialize();
    }

    /**
     * Create a new tweak update packet with a buffer.
     * This decodes a packet into a JSON string.
     *
     * @param buffer A friendly byte buffer instance.
     */
    public v2PacketS2CTweakUpdate(FriendlyByteBuf buffer)
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

        // Retrieve client tweak data
        TweakSerializer<?> serializer = TweakSerializer.deserialize(this.json);
        Tweak<?> tweak = Tweak.get(serializer.getCacheKey());

        // Debug information
        String information = String.format
        (
            """
                Received tweak update from server:
                NEW -> [tweak={cacheId:%s, newValue:%s, status:%s}]
                OLD -> [tweak={cacheId:%s, newValue:%s, status:%s}]
            """,

            LogColor.apply(LogColor.LIGHT_PURPLE, serializer.getCacheKey()),
            LogColor.apply(LogColor.BLUE, serializer.getSendingValue().toString()),
            TweakStatus.toStringWithColor(serializer.getStatus()),

            LogColor.apply(LogColor.LIGHT_PURPLE, tweak.getCacheKey()),
            LogColor.apply(LogColor.BLUE, tweak.getValue().toString()),
            TweakStatus.toStringWithColor(tweak.getStatus())
        );

        NostalgicTweaks.LOGGER.debug(information);

        // Update client server sent value
        tweak.setSentValue(serializer.getSendingValue());

        // Notify client that it was sent a tweak update
        if (NostalgicTweaks.isNetworkVerified())
            ToastNotification.gotChanges();

        // Update the client's config if this is a LAN session
        if (NetUtil.isLocalHost())
            ConfigCache.saveClient();
    }
}
