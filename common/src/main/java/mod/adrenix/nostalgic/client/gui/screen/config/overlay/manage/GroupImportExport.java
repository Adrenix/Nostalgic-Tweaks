package mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.group.Group;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.config.ClientConfig;
import mod.adrenix.nostalgic.config.ServerConfig;
import mod.adrenix.nostalgic.config.cache.ConfigCache;
import mod.adrenix.nostalgic.config.factory.ConfigBuilder;
import mod.adrenix.nostalgic.config.factory.ConfigHandler;
import mod.adrenix.nostalgic.network.packet.backup.ServerboundReloadConfig;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import mod.adrenix.nostalgic.util.client.RunUtil;
import mod.adrenix.nostalgic.util.client.dialog.DialogType;
import mod.adrenix.nostalgic.util.client.dialog.FileDialog;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.io.PathUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GroupImportExport extends ManageGroup
{
    @Override
    void define(ManageOverlay manager)
    {
        /* Header */

        Group header = Group.create(manager.overlay)
            .forceRelativeY()
            .icon(Icons.SAVE_FLOPPY)
            .title(Lang.Button.IMPORT_AND_EXPORT)
            .border(Color.IRIS_BLUE)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget.create(Lang.Manage.IMPORT_EXPORT_HEADER).width(header::getInsideWidth).build(header::addWidget);

        /* Hot Swap */

        Group swap = Group.create(manager.overlay)
            .icon(Icons.RED_UNDO)
            .title(Lang.Manage.HOT_SWAP)
            .border(Color.GOLDEN_GATE_BRIDGE)
            .below(header, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget swapInformation = TextWidget.create(Lang.Manage.HOT_SWAP_INFO)
            .width(swap::getInsideWidth)
            .build(swap::addWidget);

        Grid swapGrid = Grid.create(manager.overlay, 2)
            .columnSpacing(1)
            .extendWidthToEnd(swap, swap.getInsidePaddingX())
            .below(swapInformation, manager.padding * 2)
            .build(swap::addWidget);

        ButtonWidget.create(Lang.Button.CLIENT_RELOAD)
            .icon(Icons.CLIENT)
            .holdFor(1L, TimeUnit.SECONDS)
            .onPress(GroupImportExport::clientReload)
            .build(swapGrid::addCell);

        ButtonWidget.create(Lang.Button.SERVER_RELOAD)
            .disabledTooltip(Lang.Tooltip.NOT_CONNECTED_OR_OPERATOR, 35, 500, TimeUnit.MILLISECONDS)
            .disableIf(NetUtil::isNotConnectedOrOperator)
            .onPress(GroupImportExport::serverReload)
            .icon(Icons.SERVER)
            .holdFor(1L, TimeUnit.SECONDS)
            .cooldown(2L, TimeUnit.SECONDS)
            .build(swapGrid::addCell);

        /* Advisory */

        Group advisement = Group.create(manager.overlay)
            .icon(Icons.WARNING)
            .title(Lang.Manage.IMPORT_ADVISORY_TITLE)
            .border(Color.SCHOOL_BUS)
            .below(swap, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget advisoryInformation = TextWidget.create(Lang.Manage.IMPORT_ADVISORY_MESSAGE)
            .width(advisement::getInsideWidth)
            .build(advisement::addWidget);

        ButtonTemplate.openFolder(PathUtil.getLogsPath())
            .useTextWidth()
            .centerInWidgetX(advisement)
            .below(advisoryInformation, manager.padding * 2)
            .build(advisement::addWidget);

        /* Importing */

        Group importing = Group.create(manager.overlay)
            .icon(Icons.IMPORT_FLOPPY)
            .title(Lang.Button.IMPORT)
            .border(Color.VIVID_LIME)
            .below(advisement, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget clientTextImport = TextWidget.create(Lang.Manage.CLIENT_IMPORT)
            .width(importing::getInsideWidth)
            .build(importing::addWidget);

        ButtonWidget clientImport = ButtonWidget.create(Lang.Button.CLIENT_IMPORT)
            .icon(Icons.CLIENT)
            .useTextWidth()
            .centerInWidgetX(importing)
            .onPress(GroupImportExport::clientImport)
            .below(clientTextImport, manager.padding * 2)
            .build(importing::addWidget);

        SeparatorWidget separatorImport = SeparatorWidget.create(importing.getColor())
            .height(1)
            .below(clientImport, manager.padding * 2)
            .width(importing::getInsideWidth)
            .build(importing::addWidget);

        TextWidget serverTextImport = TextWidget.create(Lang.Manage.SERVER_IMPORT)
            .below(separatorImport, manager.padding * 2)
            .width(importing::getInsideWidth)
            .build(importing::addWidget);

        ButtonWidget.create(Lang.Button.SERVER_IMPORT)
            .icon(Icons.SERVER)
            .useTextWidth()
            .centerInWidgetX(importing)
            .enableIf(NetUtil::isConnectedAndOperator)
            .onPress(GroupImportExport::serverImport)
            .below(serverTextImport, manager.padding * 2)
            .build(importing::addWidget);

        /* Exporting */

        Group exporting = Group.create(manager.overlay)
            .icon(Icons.EXPORT_FLOPPY)
            .title(Lang.Button.EXPORT)
            .border(Color.ATOMIC_TANGERINE)
            .below(importing, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget clientTextExport = TextWidget.create(Lang.Manage.CLIENT_EXPORT)
            .width(exporting::getInsideWidth)
            .build(exporting::addWidget);

        ButtonWidget clientExport = ButtonWidget.create(Lang.Button.CLIENT_EXPORT)
            .icon(Icons.CLIENT)
            .useTextWidth()
            .centerInWidgetX(exporting)
            .onPress(GroupImportExport::clientExport)
            .below(clientTextExport, manager.padding * 2)
            .build(exporting::addWidget);

        SeparatorWidget separatorExport = SeparatorWidget.create(exporting.getColor())
            .height(1)
            .below(clientExport, manager.padding * 2)
            .width(exporting::getInsideWidth)
            .build(exporting::addWidget);

        TextWidget serverTextExport = TextWidget.create(Lang.Manage.SERVER_EXPORT)
            .below(separatorExport, manager.padding * 2)
            .width(exporting::getInsideWidth)
            .build(exporting::addWidget);

        ButtonWidget.create(Lang.Button.SERVER_EXPORT)
            .icon(Icons.SERVER)
            .useTextWidth()
            .centerInWidgetX(exporting)
            .onPress(GroupImportExport::serverExport)
            .below(serverTextExport, manager.padding * 2)
            .build(exporting::addWidget);
    }

    /* Static */

    private static void clientReload()
    {
        Path reloadLocation = ConfigBuilder.getHandler().getPath();
        ConfigHandler<ClientConfig> hotSwap = ConfigBuilder.temp(ClientConfig.class, reloadLocation);

        if (hotSwap.load())
        {
            ConfigHandler<ClientConfig> handler = ConfigBuilder.getHandler();

            handler.setLoaded(hotSwap.getLoaded());
            handler.save();

            TweakPool.values().forEach(Tweak::sync);
            RunUtil.reload();
        }
        else
            ManageThreadMessage.IMPORT_ERROR.open();
    }

    private static void serverReload()
    {
        PacketUtil.sendToServer(new ServerboundReloadConfig());
    }

    private static void clientImport()
    {
        CompletableFuture.runAsync(() -> {
            Path defaultFile = ConfigBuilder.getHandler().getPath();
            String readLocation = FileDialog.getJsonLocation("Client Config Import", defaultFile, DialogType.OPEN_FILE);

            if (readLocation != null)
            {
                ConfigHandler<ClientConfig> handler = ConfigBuilder.temp(ClientConfig.class, Path.of(readLocation));

                if (handler.load())
                {
                    ManageThreadMessage.IMPORT_CLIENT_SUCCESS.open(handler);
                    NostalgicTweaks.LOGGER.info("[Config Import] Imported a new client config using the file at %s", readLocation);
                }
                else
                    ManageThreadMessage.IMPORT_ERROR.open();
            }
        }).exceptionally(throwable -> {
            ManageThreadMessage.JAVA_ERROR.open();
            NostalgicTweaks.LOGGER.error("[Config Import] An error occurred while importing client config file\n%s", throwable);

            return null;
        });
    }

    private static void clientExport()
    {
        CompletableFuture.runAsync(() -> {
            try
            {
                Path defaultFile = ConfigBuilder.getHandler().getPath();
                String writeLocation = FileDialog.getJsonLocation("Client Config Export", defaultFile, DialogType.SAVE_FILE);

                if (writeLocation != null)
                {
                    Path savePath = Path.of(writeLocation);
                    Files.copy(ConfigCache.path(), savePath, StandardCopyOption.REPLACE_EXISTING);

                    ManageThreadMessage.EXPORT_CLIENT_SUCCESS.open(savePath.getParent());
                    NostalgicTweaks.LOGGER.info("[Config Export] Created a new client export file at %s", writeLocation);
                }
            }
            catch (IOException exception)
            {
                ManageThreadMessage.JAVA_ERROR.open();
                NostalgicTweaks.LOGGER.error("[Config Export] (I/O Error) Could not export client config file\n%s", exception);
            }
        }).exceptionally(throwable -> {
            ManageThreadMessage.JAVA_ERROR.open();
            NostalgicTweaks.LOGGER.error("[Config Export] An error occurred while exporting client config file\n%s", throwable);

            return null;
        });
    }

    private static void serverImport()
    {
        CompletableFuture.runAsync(() -> {
            String loadLocation = FileDialog.getJsonLocation("Server Config Import", null, DialogType.OPEN_FILE);

            if (loadLocation != null)
            {
                ConfigHandler<ServerConfig> importHandler = ConfigBuilder.temp(ServerConfig.class, Path.of(loadLocation));

                if (importHandler.load())
                {
                    ManageThreadMessage.IMPORT_SERVER_SUCCESS.open(importHandler);
                    NostalgicTweaks.LOGGER.info("[Config Import] Imported a new server config using the file at %s", loadLocation);
                }
                else
                {
                    ManageThreadMessage.IMPORT_ERROR.open();
                    NostalgicTweaks.LOGGER.info("[Config Import] An error occurred while importing server config file\n%s", loadLocation);
                }
            }
        }).exceptionally(throwable -> {
            ManageThreadMessage.JAVA_ERROR.open();
            NostalgicTweaks.LOGGER.error("[Config Import] An error occurred while importing server config file\n%s", throwable);

            return null;
        });
    }

    private static void serverExport()
    {
        CompletableFuture.runAsync(() -> {
            Path defaultFile = Path.of("server-export.json");
            String saveLocation = FileDialog.getJsonLocation("Server Config Export", defaultFile, DialogType.SAVE_FILE);

            if (saveLocation != null)
            {
                ManageThreadMessage.EXPORT_SERVER_SUCCESS.open(Path.of(saveLocation));
                NostalgicTweaks.LOGGER.info("[Config Export] Created a new server export file at %s", saveLocation);
            }
        }).exceptionally(throwable -> {
            ManageThreadMessage.JAVA_ERROR.open();
            NostalgicTweaks.LOGGER.error("[Config Export] An error occurred while exporting server config file\n%s", throwable);

            return null;
        });
    }
}
