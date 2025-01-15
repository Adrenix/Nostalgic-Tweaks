package mod.adrenix.nostalgic.network.packet.backup;

import mod.adrenix.nostalgic.util.common.io.PathUtil;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.file.Path;

public class BackupObject
{
    /* Static */

    /**
     * Create a new backup file object.
     *
     * @param path The {@link Path} that points to a config backup file.
     * @return A new {@link BackupObject} instance.
     */
    public static BackupObject create(Path path)
    {
        return new BackupObject(path.getFileName().toString(), PathUtil.getCreationTime(path));
    }

    /* Fields */

    private final String filename;
    private final long timestamp;

    /* Constructor */

    private BackupObject(String filename, long timestamp)
    {
        this.filename = filename;
        this.timestamp = timestamp;
    }

    /* Network */

    /**
     * Encode a backup object to be sent over the network.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     * @param backup A {@link BackupObject} instance.
     */
    public static void encode(FriendlyByteBuf buffer, BackupObject backup)
    {
        buffer.writeUtf(backup.filename);
        buffer.writeLong(backup.timestamp);
    }

    /**
     * Decode a new backup object from a network buffer.
     *
     * @param buffer A {@link FriendlyByteBuf} instance.
     * @return A new {@link BackupObject} instance.
     */
    public static BackupObject decode(FriendlyByteBuf buffer)
    {
        return new BackupObject(buffer.readUtf(), buffer.readLong());
    }

    /* Methods */

    /**
     * @return The backup config filename.
     */
    public String getFilename()
    {
        return this.filename;
    }

    /**
     * @return The timestamp the backup file was created in epoch milliseconds.
     */
    public long getTimestamp()
    {
        return this.timestamp;
    }

    /**
     * @return A {@link Path} instance within the mod's backup path resolved to this object's filename.
     */
    public Path getPath()
    {
        return PathUtil.getBackupPath().resolve(this.filename);
    }
}
