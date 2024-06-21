package mod.adrenix.nostalgic.network.packet.backup;

import com.google.common.collect.Sets;
import dev.architectury.networking.NetworkManager;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.packet.ModPacket;
import mod.adrenix.nostalgic.util.common.io.PathUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ClientboundBackupObjects implements ModPacket
{
    /* Type */

    public static final Type<ClientboundBackupObjects> TYPE = ModPacket.createType(ClientboundBackupObjects.class);

    /* Fields */

    final Set<BackupObject> backups;
    final boolean success;

    /* Constructors */

    /**
     * Create a new "list of backup objects" packet instance. If an exception occurred from I/O, then a message will be
     * sent to the operator that an error had occurred.
     */
    public ClientboundBackupObjects()
    {
        this.backups = new LinkedHashSet<>();
        boolean hasError = false;

        try
        {
            List<Path> files = PathUtil.getNewestModified(PathUtil.getBackupPath(), PathUtil::isJsonFile);

            for (Path path : files)
                this.backups.add(BackupObject.create(path));
        }
        catch (IOException exception)
        {
            hasError = true;

            NostalgicTweaks.LOGGER.error("An error occurred when preparing the server's backup files\n%s", exception);
        }

        this.success = !hasError;
    }

    /**
     * Decode a packet received over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     */
    public ClientboundBackupObjects(FriendlyByteBuf buffer)
    {
        this.success = buffer.readBoolean();
        this.backups = buffer.readCollection(Sets::newLinkedHashSetWithExpectedSize, BackupObject::decode);
    }

    /* Methods */

    @Override
    public void encoder(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(this.success);
        buffer.writeCollection(this.backups, BackupObject::encode);
    }

    @Override
    public void receiver(NetworkManager.PacketContext context)
    {
        if (this.isServerHandling(context))
            return;

        ExecuteOnClient.handleBackupObjects(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
