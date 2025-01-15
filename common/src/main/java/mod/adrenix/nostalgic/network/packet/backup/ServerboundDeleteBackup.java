package mod.adrenix.nostalgic.network.packet.backup;

import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.util.common.io.PathUtil;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This packet makes a request to the server to delete a mod config backup file. The server will only acknowledge a
 * request from a server operator.
 *
 * @param backup The {@link BackupObject} to delete.
 */
public record ServerboundDeleteBackup(BackupObject backup) implements ModPacket
{
    /* Type */

    public static final Type<ServerboundDeleteBackup> TYPE = ModPacket.createType(ServerboundDeleteBackup.class);

    /* Decoder */

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ServerboundDeleteBackup(final FriendlyByteBuf buffer)
    {
        this(BackupObject.decode(buffer));
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
        BackupObject.encode(buffer, this.backup);
    }

    @Override
    public void receiver(NetworkManager.PacketContext context)
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

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
