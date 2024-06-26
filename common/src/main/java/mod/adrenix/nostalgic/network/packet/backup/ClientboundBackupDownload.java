package mod.adrenix.nostalgic.network.packet.backup;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public class ClientboundBackupDownload implements ModPacket
{
    /* Type */

    public static final Type<ClientboundBackupDownload> TYPE = ModPacket.createType(ClientboundBackupDownload.class);

    /* Fields */

    final DownloadType downloadType;
    final Set<String> chunks;
    final String filename;
    final boolean success;

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
        boolean hasError = false;

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
                hasError = true;
                NostalgicTweaks.LOGGER.error("[I/O Error] Could not read backup file (%s)\n%s", this.filename, exception);
            }
        }
        else
        {
            hasError = true;
            NostalgicTweaks.LOGGER.error("[I/O Error] Could not found backup file (%s)", this.filename);
        }

        this.success = !hasError;
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
        this.success = buffer.readBoolean();
        this.chunks = buffer.readCollection(Sets::newLinkedHashSetWithExpectedSize, FriendlyByteBuf::readUtf);
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
        buffer.writeEnum(this.downloadType);
        buffer.writeUtf(this.filename);
        buffer.writeBoolean(this.success);
        buffer.writeCollection(this.chunks, FriendlyByteBuf::writeUtf);
    }

    @Override
    public void receiver(NetworkManager.PacketContext context)
    {
        if (this.isServerHandling(context))
            return;

        ExecuteOnClient.handleBackupDownload(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
