package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.util.common.io.PathUtil;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.network.FriendlyByteBuf;

import java.io.IOException;
import java.nio.file.Path;

public class ServerboundDeleteBackup implements ModPacket
{
    /* Fields */

    private final BackupObject backup;

    /* Constructors */

    /**
     * This packet makes a request to the server to delete a mod config backup file. The server will only acknowledge a
     * request from a server operator.
     *
     * @param backup The {@link BackupObject} to delete.
     */
    public ServerboundDeleteBackup(BackupObject backup)
    {
        this.backup = backup;
    }

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ServerboundDeleteBackup(FriendlyByteBuf buffer)
    {
        this.backup = BackupObject.decode(buffer);
    }

    /* Methods */

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        BackupObject.encode(buffer, this.backup);
    }

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        if (this.isNotFromOperator(context))
            return;

        Path path = this.backup.getPath();
        String filename = path.getFileName().toString();

        NostalgicTweaks.LOGGER.info("Player (%s) deleted backup (%s)", this.getPlayerName(context), filename);

        try
        {
            PathUtil.deleteWithoutCatch(path);
            PacketUtil.sendToPlayer(this.getServerPlayer(context), new ClientboundBackupDeleted(true));
        }
        catch (IOException exception)
        {
            PacketUtil.sendToPlayer(this.getServerPlayer(context), new ClientboundBackupDeleted(false));
        }
    }
}
