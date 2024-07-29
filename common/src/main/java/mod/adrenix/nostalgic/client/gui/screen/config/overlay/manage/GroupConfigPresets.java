package mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.AfterConfigSave;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageOverlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageType;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconTemplate;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.input.GenericInput;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.config.ClientConfig;
import mod.adrenix.nostalgic.config.cache.ConfigCache;
import mod.adrenix.nostalgic.config.cache.ConfigReflect;
import mod.adrenix.nostalgic.config.factory.ConfigBuilder;
import mod.adrenix.nostalgic.config.factory.ConfigHandler;
import mod.adrenix.nostalgic.network.packet.backup.ServerboundCreateBackup;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakMeta;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import mod.adrenix.nostalgic.util.client.animate.Animate;
import mod.adrenix.nostalgic.util.client.dialog.DialogType;
import mod.adrenix.nostalgic.util.client.dialog.FileDialog;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import mod.adrenix.nostalgic.util.client.search.GenericDatabase;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.io.PathUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.ChatFormatting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GroupConfigPresets extends ManageGroup
{
    /* Fields */

    private RowList rowList;
    private GenericInput search;
    private final FlagHolder refresh = FlagHolder.off();
    private final ArrayList<PresetObject> presets = new ArrayList<>();
    private final GenericDatabase<PresetObject> database = new GenericDatabase<>();
    private final int padding = 2;

    /* Methods */

    @Override
    void define(ManageOverlay manager)
    {
        this.clear();

        this.search = GenericInput.create()
            .icon(Icons.SEARCH)
            .whenEmpty(Lang.Input.SEARCH)
            .background(Color.OLIVE_BLACK, Color.OLIVE_BLACK)
            .border(Color.ASPHALT_GRAY, Color.WHITE)
            .maxLength(100)
            .searchShortcut()
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .onInput(this::fromSearch)
            .build(this::register);

        SeparatorWidget topLine = SeparatorWidget.create(Color.WHITE)
            .rightOf(manager.separator, manager.padding)
            .below(this.search, manager.padding)
            .extendWidthToScreenEnd(0)
            .height(1)
            .build(this::register);

        Grid footer = Grid.create(manager.overlay, 2)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .fromScreenEndY(0)
            .columnSpacing(1)
            .build(this::register);

        SeparatorWidget bottomLine = SeparatorWidget.create(Color.WHITE)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .above(footer, 1)
            .height(1)
            .build(this::register);

        this.rowList = RowList.create()
            .below(topLine, 0)
            .extendHeightTo(bottomLine, 0)
            .extendWidthToScreenEnd(0)
            .rightOf(manager.separator, manager.padding)
            .emptyMessage(Lang.Manage.CONFIG_PRESETS_EMPTY)
            .highlight(0.15D, Animate.linear(150L, TimeUnit.MILLISECONDS))
            .heightOverflowMargin(this.padding)
            .showSelectionBorder()
            .useSeparators()
            .build(this::register);

        ButtonTemplate.openFolder(PathUtil.getPresetsPath())
            .tooltip(Lang.Button.OPEN_FOLDER, 40, 700L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.OPEN_PRESETS_FOLDER, 40)
            .build(footer::addCell);

        ButtonWidget.create(Lang.Button.REFRESH)
            .icon(Icons.BOOK_CLOSED)
            .tooltip(Lang.Button.REFRESH, 40, 700L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.REFRESH_VIEW, 40)
            .onPress(this::getPresetsAndMakeRows)
            .build(footer::addCell);

        ButtonWidget.create(Lang.Button.CREATE_PRESET)
            .icon(Icons.ADD)
            .tooltip(Lang.Button.CREATE_PRESET, 40, 700L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.CREATE_PRESET, 40)
            .onPress(this::create)
            .build(footer::addCell);

        ButtonWidget.create(Lang.Button.DELETE_ALL)
            .icon(Icons.RED_TRASH_CAN)
            .tooltip(Lang.Button.DELETE_ALL, 40, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.DELETE_ALL_PRESETS, 40)
            .holdFor(2L, TimeUnit.SECONDS)
            .onPress(this::deleteAll)
            .build(footer::addCell);

        manager.overlay.runOnTick(() -> {
            if (this.refresh.ifEnabledThenDisable())
                this.getPresetsAndMakeRows();
        });

        this.getPresetsAndMakeRows();
    }

    /**
     * Clears the presets section for a new session.
     */
    private void clear()
    {
        this.presets.clear();
        this.database.clear();

        if (this.rowList != null)
            this.rowList.clear();
    }

    /**
     * Sets all widgets invisible and clears the presets view.
     */
    @Override
    public void setInvisible()
    {
        super.setInvisible();

        this.clear();
        this.getPresetsAndMakeRows();
    }

    /**
     * Get preset files stored on disk and make a row list from the files.
     */
    private void getPresetsAndMakeRows()
    {
        this.readDirectory();
        this.makeRows();

        this.search.setInput("");
    }

    /**
     * Read all preset files and store them in the preset cache and search database.
     */
    private void readDirectory()
    {
        this.presets.clear();
        this.database.clear();

        try
        {
            List<Path> files = PathUtil.getNewestModified(PathUtil.getPresetsPath(), PathUtil::isJsonFile);

            for (Path path : files)
                this.presets.add(PresetObject.create(path));
        }
        catch (IOException exception)
        {
            MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.IO_TITLE, Lang.Error.VIEW_CONFIG_PRESETS)
                .addButton(ButtonTemplate.openFolder(PathUtil.getLogsPath()))
                .setResizePercentage(0.65D)
                .build()
                .open();

            NostalgicTweaks.LOGGER.error("[I/O Error] Could not read files within presets directory\n%s", exception);
        }

        this.presets.forEach(preset -> this.database.put(preset.getFilename(), preset));
    }

    /**
     * Create rows based on the current preset list.
     */
    private void makeRows()
    {
        this.rowList.clear();

        for (PresetObject preset : this.presets)
        {
            Row row = Row.create(this.rowList).build();
            DynamicWidget<?, ?> bottom = this.getPresetRowInformation(row, preset);

            Grid grid = Grid.create(row, 2)
                .below(bottom, this.padding)
                .alignFlushTo(bottom)
                .extendWidthToEnd(row, this.padding)
                .columnSpacing(1)
                .build(row::addWidget);

            ButtonWidget.create(Lang.Button.INSPECT)
                .icon(Icons.SEARCH)
                .tooltip(Lang.Button.INSPECT, 40, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.INSPECT_PRESET, 40)
                .holdFor(500L, TimeUnit.MILLISECONDS)
                .onPress(() -> this.inspect(preset))
                .build(grid::addCell);

            ButtonWidget.create(Lang.Button.SEND_TO_SERVER)
                .icon(Icons.SERVER)
                .tooltip(Lang.Button.SEND_TO_SERVER, 40, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.SEND_PRESET_TO_SERVER, 40)
                .holdFor(1L, TimeUnit.SECONDS)
                .cooldown(3L, TimeUnit.SECONDS)
                .disableIf(NetUtil::isNotConnectedOrOperator)
                .onPress(() -> this.sendToServer(preset))
                .build(grid::addCell);

            ButtonWidget.create(Lang.Button.APPLY)
                .icon(Icons.GREEN_CHECK)
                .tooltip(Lang.Button.APPLY, 40, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.APPLY_PRESET, 40)
                .holdFor(1L, TimeUnit.SECONDS)
                .onPress(() -> this.apply(preset))
                .build(grid::addCell);

            ButtonWidget.create(Lang.Button.DELETE)
                .icon(Icons.TRASH_CAN)
                .tooltip(Lang.Button.DELETE, 40, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.DELETE_PRESET, 40)
                .holdFor(1L, TimeUnit.SECONDS)
                .onPress(() -> this.delete(preset))
                .build(grid::addCell);

            this.rowList.addBottomRow(row);
        }
    }

    /**
     * Row widgets that display information about a preset object.
     *
     * @param row    A {@link Row} instance.
     * @param preset The {@link PresetObject} instance.
     * @return The bottom-most widget built for the row.
     */
    private DynamicWidget<?, ?> getPresetRowInformation(Row row, PresetObject preset)
    {
        IconWidget book = IconTemplate.text(Icons.BOOK_OPEN).pos(this.padding, this.padding).build(row::addWidget);

        TextWidget filename = TextWidget.create(Lang.Manage.CONFIG_PRESETS_FILENAME)
            .color(Color.fromFormatting(ChatFormatting.GRAY))
            .extendWidthToEnd(row, this.padding)
            .rightOf(book, this.padding * 2)
            .build(row::addWidget);

        TextWidget json = TextWidget.create(preset.getFilename())
            .extendWidthToEnd(row, this.padding)
            .alignFlushTo(filename)
            .below(filename, this.padding)
            .build(row::addWidget);

        IconWidget floppy = IconTemplate.text(Icons.SAVE_FLOPPY)
            .alignFlushTo(book)
            .below(json, this.padding * 2)
            .build(row::addWidget);

        TextWidget created = TextWidget.create(Lang.Manage.CONFIG_PRESETS_LAST_MODIFIED)
            .color(Color.fromFormatting(ChatFormatting.GRAY))
            .extendWidthToEnd(row, this.padding)
            .rightOf(floppy, this.padding * 2)
            .below(json, this.padding * 2)
            .build(row::addWidget);

        return TextWidget.create(PathUtil.parseEpochTime(preset.getTimestamp()))
            .extendWidthToEnd(row, this.padding)
            .alignFlushTo(created)
            .below(created, this.padding)
            .build(row::addWidget);
    }

    /**
     * Reorganize the row list to show results from search input.
     *
     * @param query The current search input.
     */
    private void fromSearch(String query)
    {
        if (query.isEmpty() || query.isBlank())
            this.readDirectory();
        else
        {
            this.presets.clear();
            this.presets.addAll(this.database.findValues(query));
        }

        this.makeRows();
    }

    /**
     * Apply the given preset to the client's runtime config, and locally save the changes.
     *
     * @param preset The {@link PresetObject} instance to apply.
     */
    private void apply(PresetObject preset)
    {
        ConfigHandler<ClientConfig> imported = ConfigBuilder.temp(ClientConfig.class, preset.getPath());

        if (imported.load())
        {
            ConfigHandler<ClientConfig> handler = ConfigBuilder.getHandler();

            ConfigBuilder.getHandler().backup();
            handler.setLoaded(imported.getLoaded());
            handler.save();

            TweakPool.values().forEach(Tweak::sync);
            AfterConfigSave.reloadAndRun();

            NostalgicTweaks.LOGGER.info("[Config Import] Imported a new client config using preset (%s)", preset.getFilename());

            this.getPresetsAndMakeRows();
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
     * Delete a preset file.
     *
     * @param preset The {@link PresetObject} instance to delete.
     */
    private void delete(PresetObject preset)
    {
        try
        {
            PathUtil.deleteWithoutCatch(preset.getPath());
        }
        catch (IOException exception)
        {
            MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.IO_TITLE, Lang.Error.DELETE_CONFIG_PRESET)
                .addButton(ButtonTemplate.openFolder(PathUtil.getLogsPath()))
                .setResizePercentage(0.65D)
                .build()
                .open();

            NostalgicTweaks.LOGGER.error("[I/O Exception] Could not delete config preset file\n%s", exception);
        }

        this.getPresetsAndMakeRows();
    }

    /**
     * Remove all the preset files from the presets folder.
     */
    private void deleteAll()
    {
        try
        {
            List<Path> files = PathUtil.getNewestFiles(PathUtil.getPresetsPath(), PathUtil::isJsonFile);

            for (Path path : files)
                PathUtil.deleteWithoutCatch(path);
        }
        catch (IOException exception)
        {
            MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.IO_TITLE, Lang.Error.DELETE_ALL_PRESETS)
                .addButton(ButtonTemplate.openFolder(PathUtil.getLogsPath()))
                .setResizePercentage(0.65D)
                .build()
                .open();
        }

        this.getPresetsAndMakeRows();
    }

    /**
     * View the contents of a preset file.
     *
     * @param preset The {@link PresetObject} to inspect.
     */
    private void inspect(PresetObject preset)
    {
        FileInspector.open(preset::getPath, preset::getFilename);
    }

    /**
     * Start the process of creating a new preset file.
     */
    private void create()
    {
        CompletableFuture.runAsync(() -> {
            try
            {
                Path defaultFile = PathUtil.getPresetsPath().resolve("preset");
                String writeLocation = FileDialog.getJsonLocation("Config Preset", defaultFile, DialogType.SAVE_FILE);

                if (writeLocation != null)
                {
                    Path savePath = Path.of(writeLocation);
                    Files.copy(ConfigCache.path(), savePath, StandardCopyOption.REPLACE_EXISTING);

                    ManageThreadMessage.CREATE_PRESET_SUCCESS.open(savePath.getParent());
                    NostalgicTweaks.LOGGER.info("[Config Preset] Created a new preset file at %s", writeLocation);
                }
            }
            catch (IOException exception)
            {
                ManageThreadMessage.JAVA_ERROR.open();
                NostalgicTweaks.LOGGER.error("[Config Preset] (I/O Error) Could not export preset file\n%s", exception);
            }

            this.refresh.enable();
        }).exceptionally(throwable -> {
            ManageThreadMessage.JAVA_ERROR.open();
            NostalgicTweaks.LOGGER.error("[Config Preset] An error occurred while exporting preset file\n%s", throwable);

            return null;
        });
    }

    /**
     * Send the multiplayer-like tweak values in the preset file to the server.
     *
     * @param preset The {@link PresetObject} to send.
     */
    private void sendToServer(PresetObject preset)
    {
        ConfigHandler<ClientConfig> imported = ConfigBuilder.temp(ClientConfig.class, preset.getPath());

        if (imported.load())
        {
            PacketUtil.sendToServer(new ServerboundCreateBackup(false));

            ClientConfig config = imported.getLoaded();

            TweakPool.filter(Tweak::isMultiplayerLike).map(TweakMeta::wildcard).forEach(tweak -> {
                Object value = ConfigReflect.getFieldValue(tweak, ClientConfig.class, config);
                String name = tweak.getGenericType().getSimpleName();

                if (value != null)
                {
                    if (tweak.applySafely(value, tweak::setReceived))
                        tweak.sendToServer();
                    else
                        NostalgicTweaks.LOGGER.warn("[Server Preset] %s did not match class type (%s)", tweak, name);
                }
                else
                    NostalgicTweaks.LOGGER.warn("[Server Preset] %s is not a known server tweak", tweak);
            });

            MessageOverlay.create(MessageType.SUCCESS, Lang.Info.SENT_PRESET_TITLE, Lang.Info.SENT_PRESET_MESSAGE)
                .setResizePercentage(0.65D)
                .build()
                .open();
        }
        else
        {
            MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.APPLY_TITLE, Lang.Error.APPLY_MESSAGE)
                .setResizePercentage(0.65D)
                .build()
                .open();
        }
    }
}
