package mod.adrenix.nostalgic.network.packet.backup;

interface ExecuteOnClient
{
    /**
     * Open different overlay types based on the success of making a backup file on the server.
     */
    static void handleMadeBackup(ClientboundMadeBackup packet)
    {
        // TODO
    }

    /**
     * Applies server sent backup objects to the client's management overlay, or open an error message if an I/O error
     * occurred on the server.
     */
    static void handleBackupObjects(ClientboundBackupObjects packet)
    {
        // TODO
    }

    /**
     * Displays a message if the deletion of the backup file failed or refreshes the server list with new data.
     */
    static void handleBackupDeleted(ClientboundBackupDeleted packet)
    {
        // TODO
    }

    /**
     * Displays a message if the deletion of all backup files failed or refreshes the server list with empty data.
     */
    static void handleDeletedAll(ClientboundDeletedAllBackups packet)
    {
        // TODO
    }

    /**
     * Displays a message on the success or failure of applying the selected config backup on the server. The server
     * list will be repopulated with new data.
     */
    static void handleAppliedBackup(ClientboundAppliedBackup packet)
    {
        // TODO
    }

    /**
     * Puts the chunks of a backup download back together and lets the client choose where to save the downloaded config
     * backup file.
     */
    static void handleBackupDownload(ClientboundBackupDownload packet)
    {
        // TODO
    }
}
