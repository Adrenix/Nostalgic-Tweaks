package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.file.Path;

public class ServerboundDownloadRequest implements ModPacket
{
    /* Fields */

    private final BackupObject backup;
    private final DownloadType downloadType;

    /* Constructors */

    /**
     * This packet makes a request to the server to download a mod backup config file. The server will only acknowledge
     * a request from a server operator.
     */
    public ServerboundDownloadRequest(BackupObject backup, DownloadType downloadType)
    {
        this.backup = backup;
        this.downloadType = downloadType;
    }

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ServerboundDownloadRequest(FriendlyByteBuf buffer)
    {
        this.backup = BackupObject.decode(buffer);
        this.downloadType = buffer.readEnum(DownloadType.class);
    }

    /* Methods */

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        BackupObject.encode(buffer, this.backup);
        buffer.writeEnum(this.downloadType);
    }

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        if (this.isNotFromOperator(context))
            return;

        Path path = this.backup.getPath();
        String filename = path.getFileName().toString();

        this.log("Player (%s) requested and was sent (%s)", this.getPlayerName(context), filename);

        PacketUtil.sendToPlayer(this.getServerPlayer(context), new ClientboundBackupDownload(this.backup, this.downloadType));
    }
}
