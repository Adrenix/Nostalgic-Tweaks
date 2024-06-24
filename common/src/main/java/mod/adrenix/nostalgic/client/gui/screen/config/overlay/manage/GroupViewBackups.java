package mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage;

import com.google.common.base.Splitter;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.AfterConfigSave;
import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageOverlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageType;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconTemplate;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.config.ClientConfig;
import mod.adrenix.nostalgic.config.factory.ConfigBuilder;
import mod.adrenix.nostalgic.config.factory.ConfigHandler;
import mod.adrenix.nostalgic.network.packet.backup.*;
import mod.adrenix.nostalgic.util.client.animate.Animate;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.Gradient;
import mod.adrenix.nostalgic.util.common.data.Holder;
import mod.adrenix.nostalgic.util.common.data.NullableAction;
import mod.adrenix.nostalgic.util.common.io.PathUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GroupViewBackups extends ManageGroup
{
    /**
     * This enumeration defines which logical-side the overlay is viewing. The server-side view will send packets to the
     * server to control the server's backups. All operations will be checked server-side for operator privileges before
     * performing backup operations.
     */
    private enum Viewing
    {
        CLIENT,
        SERVER
    }

    /* Fields */

    private Viewing viewing = Viewing.CLIENT;
    private ButtonWidget client;
    private ButtonWidget server;
    private RowList clientList;
    private RowList serverList;
    private final ArrayList<BackupObject> backupsForClient = new ArrayList<>();
    private final ArrayList<BackupObject> backupsForServer = new ArrayList<>();
    private final int padding = 2;

    /* Methods */

    @Override
    void define(ManageOverlay manager)
    {
        this.clear();

        this.client = ButtonWidget.create(Lang.Tag.CLIENT)
            .icon(Icons.CLIENT)
            .onPress(this::setClientView)
            .backgroundRenderer(this::renderHeader)
            .build();

        this.server = ButtonWidget.create(Lang.Tag.SERVER)
            .icon(Icons.SERVER)
            .disableIf(NetUtil::isNotConnectedOrOperator)
            .disabledTooltip(Lang.Manage.VIEW_BACKUPS_DISCONNECTED, 45, 1L, TimeUnit.SECONDS)
            .onPress(this::setServerView)
            .backgroundRenderer(this::renderHeader)
            .build();

        Grid header = Grid.create(manager.overlay, 2)
            .forceRelativeY()
            .columnSpacing(0)
            .extendWidthToScreenEnd(0)
            .rightOf(manager.separator, manager.padding)
            .addCells(this.client, this.server)
            .build(this::register);

        Grid footer = Grid.create(manager.overlay, 3)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .fromScreenEndY(0)
            .columnSpacing(1)
            .build(this::register);

        SeparatorWidget separator = SeparatorWidget.create(Color.WHITE)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .above(footer, 1)
            .height(1)
            .build(this::register);

        this.clientList = RowList.create()
            .below(header, 0)
            .extendHeightTo(separator, 0)
            .extendWidthToScreenEnd(0)
            .rightOf(manager.separator, manager.padding)
            .emptyMessage(Lang.Manage.VIEW_BACKUPS_EMPTY)
            .highlight(0.15D, Animate.linear(150L, TimeUnit.MILLISECONDS))
            .invisibleIf(CollectionUtil.areAnyTrue(this::isViewingServer, this::isGroupInvisible))
            .backgroundRenderer(this::renderBackground)
            .heightOverflowMargin(this.padding)
            .showSelectionBorder()
            .useSeparators()
            .build(this::register);

        this.serverList = RowList.create()
            .below(header, 0)
            .extendHeightTo(separator, 0)
            .extendWidthToScreenEnd(0)
            .rightOf(manager.separator, manager.padding)
            .emptyMessage(Lang.Manage.VIEW_BACKUPS_EMPTY)
            .highlight(0.15D, Animate.linear(150L, TimeUnit.MILLISECONDS))
            .invisibleIf(CollectionUtil.areAnyTrue(this::isViewingClient, this::isGroupInvisible))
            .backgroundRenderer(this::renderBackground)
            .heightOverflowMargin(this.padding)
            .showSelectionBorder()
            .useSeparators()
            .build(this::register);

        ButtonTemplate.openFolder(PathUtil.getBackupPath())
            .tooltip(Lang.Button.OPEN_FOLDER, 40, 700L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.OPEN_BACKUP_FOLDER, 40)
            .disableIf(this::isViewingServer)
            .build(footer::addCell);

        ButtonWidget.create(Lang.Button.REFRESH)
            .icon(Icons.BOOK_CLOSED)
            .tooltip(Lang.Button.REFRESH, 40, 700L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.REFRESH_VIEW, 40)
            .onPress(this::refreshAll)
            .build(footer::addCell);

        ButtonWidget.create(Lang.Button.DELETE_ALL)
            .icon(Icons.RED_TRASH_CAN)
            .tooltip(Lang.Button.DELETE_ALL, 40, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.DELETE_ALL_BACKUPS, 40)
            .holdFor(2L, TimeUnit.SECONDS)
            .onPress(this::deleteAll)
            .build(footer::addCell);

        this.setClientView();
    }

    /**
     * Clears the view backups section for a new session.
     */
    private void clear()
    {
        this.viewing = Viewing.CLIENT;

        this.backupsForClient.clear();
        this.backupsForServer.clear();

        NullableAction.attempt(this.clientList, RowList::clear);
        NullableAction.attempt(this.serverList, RowList::clear);
    }

    /**
     * Sets all widgets invisible, clears both sided backup views, and resets the view back to client-side.
     */
    @Override
    public void setInvisible()
    {
        super.setInvisible();

        this.clear();
        this.setClientView();
    }

    /**
     * @return Whether the overlay is viewing client-side backup files.
     */
    private boolean isViewingClient()
    {
        return this.viewing == Viewing.CLIENT;
    }

    /**
     * @return Whether the overlay is viewing server-side backup files.
     */
    private boolean isViewingServer()
    {
        return this.viewing == Viewing.SERVER;
    }

    /**
     * Common row widgets between client and server views.
     *
     * @param row A {@link Row} instance.
     * @return The bottom-most widget built for the row.
     */
    private DynamicWidget<?, ?> getBackupRowInformation(Row row, BackupObject backup)
    {
        IconWidget book = IconTemplate.text(Icons.BOOK_OPEN).pos(this.padding, this.padding).build(row::addWidget);

        TextWidget filename = TextWidget.create(Lang.Manage.VIEW_BACKUPS_FILENAME)
            .color(Color.fromFormatting(ChatFormatting.GRAY))
            .extendWidthToEnd(row, this.padding)
            .rightOf(book, this.padding * 2)
            .build(row::addWidget);

        TextWidget json = TextWidget.create(backup.getFilename())
            .extendWidthToEnd(row, this.padding)
            .alignFlushTo(filename)
            .below(filename, this.padding)
            .build(row::addWidget);

        IconWidget floppy = IconTemplate.text(Icons.SAVE_FLOPPY)
            .alignFlushTo(book)
            .below(json, this.padding * 2)
            .build(row::addWidget);

        TextWidget created = TextWidget.create(Lang.Manage.VIEW_BACKUPS_LAST_MODIFIED)
            .color(Color.fromFormatting(ChatFormatting.GRAY))
            .extendWidthToEnd(row, this.padding)
            .rightOf(floppy, this.padding * 2)
            .below(json, this.padding * 2)
            .build(row::addWidget);

        return TextWidget.create(PathUtil.parseEpochTime(backup.getTimestamp()))
            .extendWidthToEnd(row, this.padding)
            .alignFlushTo(created)
            .below(created, this.padding)
            .build(row::addWidget);
    }

    /**
     * Set up the view backups overlay for client-side viewing.
     */
    private void setClientView()
    {
        this.viewing = Viewing.CLIENT;
        this.backupsForClient.clear();
        this.clientList.clear();

        try
        {
            List<Path> files = PathUtil.getNewestModified(PathUtil.getBackupPath(), PathUtil::isJsonFile);

            for (Path path : files)
                this.backupsForClient.add(BackupObject.create(path));
        }
        catch (IOException exception)
        {
            MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.IO_TITLE, Lang.Error.VIEW_CLIENT_BACKUPS)
                .addButton(ButtonTemplate.openFolder(PathUtil.getLogsPath()))
                .setResizePercentage(0.65D)
                .build()
                .open();

            NostalgicTweaks.LOGGER.error("[I/O Error] Could not read files within backup directory\n%s", exception);
        }

        for (BackupObject backup : this.backupsForClient)
        {
            Row row = Row.create(this.clientList).build();
            DynamicWidget<?, ?> bottom = this.getBackupRowInformation(row, backup);

            Grid grid = Grid.create(row, 2)
                .below(bottom, this.padding)
                .alignFlushTo(bottom)
                .extendWidthToEnd(row, this.padding)
                .columnSpacing(1)
                .build(row::addWidget);

            ButtonWidget.create(Lang.Button.INSPECT)
                .icon(Icons.SEARCH)
                .tooltip(Lang.Button.INSPECT, 40, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.INSPECT_BACKUP, 40)
                .holdFor(500L, TimeUnit.MILLISECONDS)
                .onPress(() -> this.inspectBackup(backup))
                .build(grid::addCell);

            ButtonWidget.create(Lang.Button.EDIT)
                .icon(Icons.PENCIL)
                .tooltip(Lang.Button.EDIT, 40, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.EDIT_BACKUP, 40)
                .holdFor(500L, TimeUnit.MILLISECONDS)
                .onPress(() -> Util.getPlatform().openFile(backup.getPath().toFile()))
                .build(grid::addCell);

            ButtonWidget.create(Lang.Button.APPLY)
                .icon(Icons.GREEN_CHECK)
                .tooltip(Lang.Button.APPLY, 40, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.APPLY_CLIENT_BACKUP, 40)
                .holdFor(1L, TimeUnit.SECONDS)
                .onPress(() -> this.applyClientBackup(backup))
                .build(grid::addCell);

            ButtonWidget.create(Lang.Button.DELETE)
                .icon(Icons.TRASH_CAN)
                .tooltip(Lang.Button.DELETE, 40, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.DELETE_BACKUP, 40)
                .holdFor(1L, TimeUnit.SECONDS)
                .onPress(() -> this.deleteClientBackup(backup))
                .build(grid::addCell);

            this.clientList.addBottomRow(row);
        }
    }

    /**
     * Clears the entries in the server list and sets the empty message to "waiting...".
     */
    private void waitForServer()
    {
        this.backupsForServer.clear();
        this.serverList.clear();
        this.serverList.getBuilder().emptyMessage(Lang.Manage.VIEW_BACKUPS_WAITING);
    }

    /**
     * Set up the view backups overlay for server-side viewing.
     */
    private void setServerView()
    {
        this.viewing = Viewing.SERVER;

        this.waitForServer();
        PacketUtil.sendToServer(new ServerboundRequestBackups());
    }

    /**
     * Display an error message that the server did not respond to our retrieval request.
     */
    void setReceiveFailed()
    {
        this.serverList.getBuilder().emptyMessage(Lang.Manage.VIEW_BACKUPS_EMPTY);

        MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.SERVER_TITLE, Lang.Error.SERVER_BACKUPS)
            .setResizePercentage(0.65D)
            .build()
            .open();
    }

    /**
     * Set up the server row list with the given set backup objects.
     *
     * @param backups A {@link Set} of {@link BackupObject}.
     */
    void setServerBackups(Set<BackupObject> backups)
    {
        this.serverList.getBuilder().emptyMessage(Lang.Manage.VIEW_BACKUPS_EMPTY);
        this.backupsForServer.addAll(backups);

        for (BackupObject backup : this.backupsForServer)
        {
            Row row = Row.create(this.serverList).build();
            DynamicWidget<?, ?> bottom = this.getBackupRowInformation(row, backup);

            Grid grid = Grid.create(row, 2)
                .below(bottom, this.padding)
                .alignFlushTo(bottom)
                .extendWidthToEnd(row, this.padding)
                .columnSpacing(1)
                .build(row::addWidget);

            ButtonWidget.create(Lang.Button.INSPECT)
                .icon(Icons.SEARCH)
                .tooltip(Lang.Button.INSPECT, 40, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.INSPECT_BACKUP, 40)
                .holdFor(500L, TimeUnit.MILLISECONDS)
                .onPress(() -> PacketUtil.sendToServer(new ServerboundDownloadRequest(backup, DownloadType.VIEW)))
                .build(grid::addCell);

            ButtonWidget.create(Lang.Button.DOWNLOAD)
                .icon(Icons.EXPORT_FLOPPY)
                .tooltip(Lang.Button.DOWNLOAD, 40, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.DOWNLOAD_BACKUP, 40)
                .holdFor(500L, TimeUnit.MILLISECONDS)
                .onPress(() -> PacketUtil.sendToServer(new ServerboundDownloadRequest(backup, DownloadType.SAVE)))
                .build(grid::addCell);

            ButtonWidget.create(Lang.Button.APPLY)
                .icon(Icons.GREEN_CHECK)
                .tooltip(Lang.Button.APPLY, 40, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.APPLY_SERVER_BACKUP, 40)
                .holdFor(1L, TimeUnit.SECONDS)
                .onPress(() -> this.applyServerBackup(backup))
                .build(grid::addCell);

            ButtonWidget.create(Lang.Button.DELETE)
                .icon(Icons.TRASH_CAN)
                .tooltip(Lang.Button.DELETE, 40, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.DELETE_BACKUP, 40)
                .holdFor(1L, TimeUnit.SECONDS)
                .onPress(() -> this.deleteServerBackup(backup))
                .build(grid::addCell);

            this.serverList.addBottomRow(row);
        }
    }

    /**
     * Refreshes the selected view's backup files collection.
     */
    private void refreshAll()
    {
        switch (this.viewing)
        {
            case CLIENT -> this.setClientView();
            case SERVER -> this.setServerView();
        }
    }

    /**
     * Apply the given config backup to the client's runtime config, and locally save the changes.
     *
     * @param backup The {@link BackupObject} instance to apply.
     */
    private void applyClientBackup(BackupObject backup)
    {
        ConfigHandler<ClientConfig> imported = ConfigBuilder.temp(ClientConfig.class, backup.getPath());

        if (imported.load())
        {
            ConfigHandler<ClientConfig> handler = ConfigBuilder.getHandler();

            ConfigBuilder.getHandler().backup();
            handler.setLoaded(imported.getLoaded());
            handler.save();

            AfterConfigSave.reloadAndRun();
            NostalgicTweaks.LOGGER.info("[Config Import] Imported a new client config using backup (%s)", backup.getFilename());

            this.setClientView();
        }
        else
        {
            MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.APPLY_TITLE, Lang.Error.APPLY_MESSAGE)
                .setResizePercentage(0.65D)
                .build()
                .open();
        }
    }

    /**
     * Apply the given config backup to the server's runtime config and save the changes.
     *
     * @param backup The {@link BackupObject} instance to apply.
     */
    private void applyServerBackup(BackupObject backup)
    {
        this.waitForServer();
        PacketUtil.sendToServer(new ServerboundApplyBackup(backup));
    }

    /**
     * Delete a config backup file on the client.
     *
     * @param backup The {@link BackupObject} instance to delete.
     */
    private void deleteClientBackup(BackupObject backup)
    {
        try
        {
            PathUtil.deleteWithoutCatch(backup.getPath());
        }
        catch (IOException exception)
        {
            MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.IO_TITLE, Lang.Error.DELETE_CLIENT_BACKUP)
                .addButton(ButtonTemplate.openFolder(PathUtil.getLogsPath()))
                .setResizePercentage(0.65D)
                .build()
                .open();

            NostalgicTweaks.LOGGER.error("[I/O Exception] Could not delete config backup file\n%s", exception);
        }

        this.setClientView();
    }

    /**
     * Delete a config backup file on the server.
     *
     * @param backup The {@link BackupObject} instance to delete.
     */
    private void deleteServerBackup(BackupObject backup)
    {
        this.waitForServer();
        PacketUtil.sendToServer(new ServerboundDeleteBackup(backup));
    }

    /**
     * Remove all the backup files from the backup folder. If server-side viewing, then will send a packet to the server
     * to remove all backup files.
     */
    private void deleteAll()
    {
        switch (this.viewing)
        {
            case CLIENT ->
            {
                try
                {
                    List<Path> files = PathUtil.getNewestFiles(PathUtil.getBackupPath(), PathUtil::isJsonFile);

                    for (Path path : files)
                        PathUtil.deleteWithoutCatch(path);
                }
                catch (IOException exception)
                {
                    MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.IO_TITLE, Lang.Error.DELETE_ALL_BACKUPS)
                        .addButton(ButtonTemplate.openFolder(PathUtil.getLogsPath()))
                        .setResizePercentage(0.65D)
                        .build()
                        .open();
                }

                this.setClientView();
            }
            case SERVER ->
            {
                this.waitForServer();
                PacketUtil.sendToServer(new ServerboundDeleteAllBackups());
            }
        }
    }

    /**
     * View the contents of a client backup file.
     *
     * @param backup The {@link BackupObject} to inspect.
     */
    private void inspectBackup(BackupObject backup)
    {
        Holder<String> content = Holder.create("");

        if (Files.exists(backup.getPath()))
        {
            try
            {
                content.set(new String(Files.readAllBytes(backup.getPath())));
            }
            catch (IOException exception)
            {
                NostalgicTweaks.LOGGER.error("[I/O Error] Could not read backup file (%s)\n%s", backup.getFilename(), exception);

                MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.IO_TITLE, Lang.Error.INSPECT_BACKUP)
                    .addButton(ButtonTemplate.openFolder(PathUtil.getLogsPath()))
                    .setResizePercentage(0.65D)
                    .build()
                    .open();
            }
        }
        else
        {
            NostalgicTweaks.LOGGER.error("[I/O Error] File does not exist (%s)\n%s", backup.getFilename());

            MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.IO_TITLE, Lang.Error.BACKUP_NONEXISTENT)
                .addButton(ButtonTemplate.openFolder(PathUtil.getLogsPath()))
                .setResizePercentage(0.65D)
                .build()
                .open();
        }

        Overlay overlay = Overlay.create(Lang.literal(backup.getFilename()))
            .resizeHeightUsingPercentage(0.95D)
            .resizeWidthUsingPercentage(0.75D)
            .build();

        Grid grid = Grid.create(overlay, 2)
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

        for (String line : Splitter.onPattern("\n").splitToList(TextUtil.colorJson(content.get())))
        {
            Row row = Row.create(rowList).build();

            TextWidget.create(line).extendWidthToEnd(row, 0).build(row::addWidget);
            rowList.addBottomRow(row);
        }

        ButtonWidget.create(Lang.Button.COPY)
            .icon(Icons.COPY)
            .tooltip(Lang.Button.COPY, 40, 700L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.COPY, 40)
            .onPress(() -> Minecraft.getInstance().keyboardHandler.setClipboard(content.get()))
            .below(separator, 1)
            .build(grid::addCell);

        ButtonWidget.create(Lang.Vanilla.GUI_DONE)
            .icon(Icons.GREEN_CHECK)
            .onPress(() -> GuiUtil.getScreenAs(Overlay.class).ifPresent(Overlay::close))
            .build(grid::addCell);

        overlay.open();
    }

    /**
     * Handler for rendering the row lists background.
     *
     * @param rowList     The {@link RowList} instance being rendered.
     * @param graphics    The current pose stack.
     * @param mouseX      The x-position of the mouse.
     * @param mouseY      The y-position of the mouse.
     * @param partialTick The normalized progress made between two ticks [0.0F, 1.0F].
     */
    private void renderBackground(RowList rowList, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        RenderUtil.fill(graphics, rowList.getX(), rowList.getY(), rowList.getEndX(), rowList.getEndY(), Color.SONIC_SILVER.fromAlpha(0.2F));
    }

    /**
     * Handler for rendering header buttons.
     *
     * @param button      The {@link ButtonWidget} instance being rendered.
     * @param graphics    The current {@link GuiGraphics}.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress made between two ticks [0.0F, 1.0F].
     */
    private void renderHeader(ButtonWidget button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        boolean isActive = false;

        if (this.client.equals(button))
            isActive = this.isViewingClient();
        else if (this.server.equals(button))
            isActive = this.isViewingServer();

        Color barColor = isActive ? Color.fromFormatting(ChatFormatting.GOLD) : Color.AZURE_WHITE;
        Color fillColor = barColor.fromAlpha(0.2F);

        if (button.isHoveredOrFocused() && isActive)
        {
            barColor = Color.RIPE_MANGO;
            fillColor = Color.RIPE_MANGO.fromAlpha(0.2F);
        }

        float startX = button.getX();
        float endX = button.getEndX();
        float barStartY = button.getEndY() - 1;
        float barEndY = button.getEndY();
        float fillStartY = button.getY();

        Color top = barColor.fromAlpha(0.0F);
        Color bottom = barColor.fromAlpha(0.3F);

        if (button.isInactive() && button.isHoveredOrFocused())
            RenderUtil.gradient(Gradient.vertical(top, bottom), graphics, startX, fillStartY, endX, barStartY);
        else if (isActive || button.isHoveredOrFocused())
            RenderUtil.fill(graphics, startX, fillStartY, endX, barStartY, fillColor.get());

        RenderUtil.fill(graphics, startX, barStartY, endX, barEndY, barColor.get());
    }
}
