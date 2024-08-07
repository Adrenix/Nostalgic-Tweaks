package mod.adrenix.nostalgic.network.packet.sync;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.toast.ToastNotification;
import mod.adrenix.nostalgic.network.ModConnection;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.util.common.log.LogColor;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientboundHandshake implements ModPacket
{
    /* Type */

    public static final Type<ClientboundHandshake> TYPE = ModPacket.createType(ClientboundHandshake.class);

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
    public void encoder(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.json);
    }

    @Override
    public void receiver(NetworkManager.PacketContext context)
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

        // Notify client
        if (connection.getProtocol().equals(NostalgicTweaks.PROTOCOL))
        {
            NostalgicTweaks.setNetworkVerification(true);
            ToastNotification.handshake();
            PacketUtil.sendToServer(new ServerboundSyncAll());
        }
        else
        {
            NostalgicTweaks.setNetworkVerification(false);
            NostalgicTweaks.LOGGER.warn("Connected to a server with Nostalgic Tweaks but received mismatched protocol");

            String info = "Protocol: [server=%s, client=%s]";
            String server = LogColor.apply(LogColor.RED, connection.getProtocol());
            String client = LogColor.apply(LogColor.GREEN, NostalgicTweaks.PROTOCOL);

            NostalgicTweaks.LOGGER.warn(info, server, client);
            NostalgicTweaks.LOGGER.warn("Client should disconnect due to an incorrect mod network state");
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
