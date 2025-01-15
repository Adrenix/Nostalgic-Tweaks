package mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage;

import mod.adrenix.nostalgic.network.packet.backup.BackupObject;
import mod.adrenix.nostalgic.util.common.io.PathUtil;

import java.nio.file.Path;

class PresetObject
{
    /* Static */

    /**
     * Create a new backup file object.
     *
     * @param path The {@link Path} that points to a config backup file.
     * @return A new {@link BackupObject} instance.
     */
    public static PresetObject create(Path path)
    {
        return new PresetObject(path.getFileName().toString(), PathUtil.getCreationTime(path));
    }

    /* Fields */

    private final String filename;
    private final long timestamp;

    /* Constructor */

    private PresetObject(String filename, long timestamp)
    {
        this.filename = filename;
        this.timestamp = timestamp;
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
        return PathUtil.getPresetsPath().resolve(this.filename);
    }
}
