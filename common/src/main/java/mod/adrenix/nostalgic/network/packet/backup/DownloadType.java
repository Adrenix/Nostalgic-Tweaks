package mod.adrenix.nostalgic.network.packet.backup;

/**
 * Used by the download request packets. Each type results in a different response sent to the client. The view type
 * will simply open a new overlay on the client that can view the config file. The save type will let the client save
 * the contents of the backup file. Only operators will be capable of viewing and downloading config backup files.
 */
public enum DownloadType
{
    VIEW,
    SAVE
}
