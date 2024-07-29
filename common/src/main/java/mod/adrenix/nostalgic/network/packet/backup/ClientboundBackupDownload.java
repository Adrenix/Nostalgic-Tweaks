package mod.adrenix.nostalgic.network.packet.backup;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import net.minecraft.network.FriendlyByteBuf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public class ClientboundBackupDownload implements ModPacket
{
    /* Fields */

    final DownloadType downloadType;
    final Set<String> chunks;
    final String filename;
    final boolean isError;

    /* Constructors */

    /**
     * Creates a new clientbound download packet. The client will be responsible for decoding the string chunks into a
     * file and then saving those chunks to the operator's local machine.
     */
    public ClientboundBackupDownload(BackupObject backup, DownloadType downloadType)
    {
        this.chunks = new LinkedHashSet<>();
        this.filename = backup.getFilename();
        this.downloadType = downloadType;

        Path path = backup.getPath();
        boolean isError = false;

        if (Files.exists(path))
        {
            try
            {
                String content = new String(Files.readAllBytes(path));
                Splitter splitter = Splitter.fixedLength(FriendlyByteBuf.MAX_STRING_LENGTH);

                this.chunks.addAll(splitter.splitToList(content));
            }
            catch (IOException exception)
            {
                isError = true;
                NostalgicTweaks.LOGGER.error("[I/O Error] Could not read backup file (%s)\n%s", this.filename, exception);
            }
        }
        else
        {
            isError = true;
            NostalgicTweaks.LOGGER.error("[I/O Error] Could not found backup file (%s)", this.filename);
        }

        this.isError = isError;
    }

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ClientboundBackupDownload(FriendlyByteBuf buffer)
    {
        this.downloadType = buffer.readEnum(DownloadType.class);
        this.filename = buffer.readUtf();
        this.isError = buffer.readBoolean();
        this.chunks = buffer.readCollection(Sets::newLinkedHashSetWithExpectedSize, FriendlyByteBuf::readUtf);
    }

    /* Methods */

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeEnum(this.downloadType);
        buffer.writeUtf(this.filename);
        buffer.writeBoolean(this.isError);
        buffer.writeCollection(this.chunks, FriendlyByteBuf::writeUtf);
    }

    @Override
    public void apply(NetworkManager.PacketContext context)
    {
        if (this.isServerHandling(context))
            return;

        ExecuteOnClient.handleBackupDownload(this);
    }
}
