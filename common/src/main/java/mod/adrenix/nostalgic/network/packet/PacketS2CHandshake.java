package mod.adrenix.nostalgic.network.packet;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.gui.ToastNotification;
import mod.adrenix.nostalgic.util.common.log.LogColor;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import net.fabricmc.api.EnvType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

/**
 * This packet is sent to the client with details about the mod's current network state.
 *
 * If the client never receives this packet after joining the world, then the mod must assume the server is not
 * running an instance of the mod.
 */

public class PacketS2CHandshake
{
    /**
     * Register this packet to the mod's network channel.
     * Channel registration is handled by Architectury.
     */
    public static void register()
    {
        NostalgicTweaks.NETWORK.register
        (
            PacketS2CHandshake.class,
            PacketS2CHandshake::encode,
            PacketS2CHandshake::new,
            PacketS2CHandshake::handle
        );
    }

    /* Fields */

    private final String protocol;

    /* Constructors */

    /**
     * Create a new handshake packet.
     * This creates a packet using the mod's current network protocol version.
     */
    public PacketS2CHandshake() { this.protocol = NostalgicTweaks.PROTOCOL; }

    /**
     * Create a new handshake packet with a buffer.
     * This decodes a packet into a protocol string.
     *
     * @param buffer A friendly byte buffer instance.
     */
    public PacketS2CHandshake(FriendlyByteBuf buffer) { this.protocol = buffer.readUtf(); }

    /* Methods */

    /**
     * Encode data into the packet.
     * @param buffer A friendly byte buffer instance.
     */
    public void encode(FriendlyByteBuf buffer) { buffer.writeUtf(this.protocol); }

    /**
     * Handle packet data.
     * @param supplier A packet context supplier.
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

        if (this.protocol.equals(NostalgicTweaks.PROTOCOL))
        {
            NostalgicTweaks.setNetworkVerification(true);
            ToastNotification.gotServerHandshake();

            String info = String.format
            (
                "Successfully connected to a world with Nostalgic Tweaks with protocol (%s).",
                LogColor.apply(LogColor.GREEN, NostalgicTweaks.PROTOCOL)
            );

            NostalgicTweaks.LOGGER.debug(info);
        }
        else
        {
            NostalgicTweaks.setNetworkVerification(false);
            NostalgicTweaks.LOGGER.warn("Connected to a server with Nostalgic Tweaks but received an incorrect protocol");

            String info = String.format
            (
                "The server sent (%s) but the client has (%s)",
                LogColor.apply(LogColor.RED, this.protocol),
                LogColor.apply(LogColor.GREEN, NostalgicTweaks.PROTOCOL)
            );

            NostalgicTweaks.LOGGER.warn(info);
            NostalgicTweaks.LOGGER.warn("This shouldn't happen! Continuing to play on this server is at your own risk!");
        }
    }
}
