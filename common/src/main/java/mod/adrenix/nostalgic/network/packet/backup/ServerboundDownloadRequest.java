package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.nio.file.Path;

/**
 * This packet makes a request to the server to download a mod backup config file. The server will only acknowledge a
 * request from a server operator.
 *
 * @param backup       The {@link BackupObject} instance.
 * @param downloadType The {@link DownloadType} value.
 */
public record ServerboundDownloadRequest(BackupObject backup, DownloadType downloadType) implements ModPacket
{
    /* Type */

    public static final Type<ServerboundDownloadRequest> TYPE = ModPacket.createType(ServerboundDownloadRequest.class);

    /* Decoder */

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ServerboundDownloadRequest(final FriendlyByteBuf buffer)
    {
        this(BackupObject.decode(buffer), buffer.readEnum(DownloadType.class));
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
        BackupObject.encode(buffer, this.backup);
        buffer.writeEnum(this.downloadType);
    }

    @Override
    public void receiver(NetworkManager.PacketContext context)
    {
        if (this.isNotFromOperator(context))
            return;

        Path path = this.backup.getPath();
        String filename = path.getFileName().toString();

        this.log("Player (%s) requested and was sent (%s)", this.getPlayerName(context), filename);

        PacketUtil.sendToPlayer(this.getServerPlayer(context), new ClientboundBackupDownload(this.backup, this.downloadType));
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
