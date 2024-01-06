package mod.adrenix.nostalgic.network.packet;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.toast.ToastNotification;
import mod.adrenix.nostalgic.network.ModConnection;
import mod.adrenix.nostalgic.util.common.log.LogColor;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundHandshake implements ModPacket
{
    /* Fields */

    protected final String json;

    /* Constructors */

    /**
     * Prepare a handshake to be sent over the network.
     *
     * @param loader   The server mod loader.
     * @param version  The mod version on the server.
     * @param protocol The mod protocol on the server.
     */
    public ClientboundHandshake(String loader, String version, String protocol)
    {
        this.json = new ModConnection(loader, version, protocol).serialize();
    }

    /**
     * Decode a buffer received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ClientboundHandshake(FriendlyByteBuf buffer)
    {
        this.json = buffer.readUtf();
    }

    /* Methods */

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.json);
    }

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        if (this.isServerHandling(context))
            return;

        ModConnection.deserialize(this.json);
        ModConnection connection = NostalgicTweaks.getConnection().orElseGet(ModConnection::disconnected);

        // Debug connection
        String output = "Connected to server running Nostalgic Tweaks [modLoader=%s, version=%s, protocol=%s]";
        String loader = LogColor.apply(LogColor.AQUA, connection.getLoader());
        String version = LogColor.apply(LogColor.GREEN, connection.getVersion());
        String protocol = LogColor.apply(LogColor.LIGHT_PURPLE, connection.getProtocol());

        NostalgicTweaks.LOGGER.debug(output, loader, version, protocol);

        // Inform client
        if (connection.getProtocol().equals(NostalgicTweaks.PROTOCOL))
        {
            NostalgicTweaks.setNetworkVerification(true);
            ToastNotification.handshake();
        }
        else
        {
            NostalgicTweaks.setNetworkVerification(false);
            NostalgicTweaks.LOGGER.warn("Connected to a server with Nostalgic Tweaks but received an incorrect protocol");

            // Debug protocol
            String info = "Protocol: [server=%s, client=%s]";
            String sent = LogColor.apply(LogColor.RED, connection.getProtocol());
            String using = LogColor.apply(LogColor.GREEN, NostalgicTweaks.PROTOCOL);

            NostalgicTweaks.LOGGER.warn(info, sent, using);
            NostalgicTweaks.LOGGER.warn("This shouldn't happen! Continuing to play on this server is at your own risk!");
        }
    }
}
