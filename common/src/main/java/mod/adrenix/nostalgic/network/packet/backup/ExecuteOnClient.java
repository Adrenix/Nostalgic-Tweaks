package mod.adrenix.nostalgic.network.packet.backup;

import com.google.common.base.Splitter;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageOverlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageType;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage.ManageSection;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage.ManageThreadMessage;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.client.dialog.DialogType;
import mod.adrenix.nostalgic.util.client.dialog.FileDialog;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.io.PathUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import net.minecraft.client.Minecraft;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

interface ExecuteOnClient
{
    /**
     * Open different overlay types based on the success of making a backup file on the server.
     */
    static void handleMadeBackup(ClientboundMadeBackup packet)
    {
        if (packet.success())
        {
            MessageOverlay.create(MessageType.SUCCESS, Lang.Info.CREATE_BACKUP_TITLE, Lang.Info.CREATE_BACKUP_MESSAGE)
                .setResizePercentage(0.65D)
                .build()
                .open();
        }
        else
        {
            MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.CREATE_BACKUP_TITLE, Lang.Error.CREATE_BACKUP_MESSAGE)
                .setResizePercentage(0.65D)
                .build()
                .open();
        }
    }

    /**
     * Applies server sent backup objects to the client's management overlay, or open an error message if an I/O error
     * occurred on the server.
     */
    static void handleBackupObjects(ClientboundBackupObjects packet)
    {
        if (!packet.success)
            ManageSection.setReceiveFailed();
        else
            ManageSection.setServerBackups(packet.backups);
    }

    /**
     * Displays a message if the deletion of the backup file failed or refreshes the server list with new data.
     */
    static void handleBackupDeleted(ClientboundBackupDeleted packet)
    {
        PacketUtil.sendToServer(new ServerboundRequestBackups());

        if (!packet.success())
        {
            MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.SERVER_TITLE, Lang.Error.DELETE_SERVER_BACKUP)
                .setResizePercentage(0.65D)
                .build()
                .open();
        }
    }

    /**
     * Displays a message if the deletion of all backup files failed or refreshes the server list with empty data.
     */
    static void handleDeletedAll(ClientboundDeletedAllBackups packet)
    {
        PacketUtil.sendToServer(new ServerboundRequestBackups());

        if (!packet.success())
        {
            MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.SERVER_TITLE, Lang.Error.DELETE_SERVER_BACKUP)
                .setResizePercentage(0.65D)
                .build()
                .open();
        }
    }

    /**
     * Displays a message on the success or failure of applying the selected config backup on the server. The server
     * list will be repopulated with new data.
     */
    static void handleAppliedBackup(ClientboundAppliedBackup packet)
    {
        PacketUtil.sendToServer(new ServerboundRequestBackups());

        if (!packet.success())
        {
            MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.SERVER_TITLE, Lang.Error.SERVER_APPLY)
                .setResizePercentage(0.65D)
                .build()
                .open();
        }
    }

    /**
     * Puts the chunks of a backup download back together and lets the client choose where to save the downloaded config
     * backup file.
     */
    static void handleBackupDownload(ClientboundBackupDownload packet)
    {
        if (!packet.success)
        {
            MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.SERVER_TITLE, Lang.Error.SERVER_BACKUPS)
                .setResizePercentage(0.65D)
                .build()
                .open();

            return;
        }

        StringBuilder builder = new StringBuilder();

        for (String chunk : packet.chunks)
            builder.append(chunk);

        String content = builder.toString();
        String filename = packet.filename;

        Runnable saveFile = () -> {
            Path defaultFile = Path.of(filename);
            String writeLocation = FileDialog.getJsonLocation("Save Backup", defaultFile, DialogType.SAVE_FILE);

            if (writeLocation == null)
                return;

            try
            {
                BufferedWriter writer = Files.newBufferedWriter(Path.of(writeLocation));

                writer.write(content);
                writer.close();
            }
            catch (IOException exception)
            {
                ManageThreadMessage.DOWNLOAD_ERROR.open(PathUtil.getLogsPath());
                NostalgicTweaks.LOGGER.error("[I/O Error] Could not write file to (%s)\n%s", writeLocation, exception);
            }
        };

        if (packet.downloadType == DownloadType.SAVE)
        {
            CompletableFuture.runAsync(saveFile);
            return;
        }

        Overlay overlay = Overlay.create(Lang.literal(filename))
            .resizeHeightUsingPercentage(0.95D)
            .resizeWidthUsingPercentage(0.75D)
            .build();

        Grid grid = Grid.create(overlay, 3)
            .extendWidthToScreenEnd(1)
            .fromScreenEndY(1)
            .columnSpacing(1)
            .build(overlay::addWidget);

        SeparatorWidget separator = SeparatorWidget.create(Color.WHITE)
            .extendWidthToScreenEnd(1)
            .above(grid, 1)
            .height(1)
            .build(overlay::addWidget);

        RowList rowList = RowList.create()
            .defaultRowHeight(GuiUtil.textHeight())
            .extendHeightTo(separator, 0)
            .extendWidthToScreenEnd(0)
            .verticalMargin(2)
            .build(overlay::addWidget);

        for (String line : Splitter.onPattern("\n").splitToList(TextUtil.colorJson(content)))
        {
            Row row = Row.create(rowList).build();

            TextWidget.create(line).extendWidthToEnd(row, 0).build(row::addWidget);
            rowList.addBottomRow(row);
        }

        ButtonWidget.create(Lang.Button.SAVE)
            .icon(Icons.SAVE_FLOPPY)
            .tooltip(Lang.Button.SAVE, 40, 700L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.SAVE_BACKUP, 40)
            .onPress(() -> CompletableFuture.runAsync(saveFile))
            .build(grid::addCell);

        ButtonWidget.create(Lang.Button.COPY)
            .icon(Icons.COPY)
            .tooltip(Lang.Button.COPY, 40, 700L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.COPY, 40)
            .onPress(() -> Minecraft.getInstance().keyboardHandler.setClipboard(content))
            .build(grid::addCell);

        ButtonWidget.create(Lang.Vanilla.GUI_DONE)
            .icon(Icons.GREEN_CHECK)
            .onPress(() -> GuiUtil.getScreenAs(Overlay.class).ifPresent(Overlay::close))
            .build(grid::addCell);

        overlay.open();
    }
}
