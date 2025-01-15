package mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage;

import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageOverlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageType;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.SliderTweak;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.group.Group;
import mod.adrenix.nostalgic.client.gui.widget.slider.SliderWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.config.cache.ConfigCache;
import mod.adrenix.nostalgic.config.factory.ConfigBuilder;
import mod.adrenix.nostalgic.network.packet.backup.ServerboundCreateBackup;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.factory.TweakNumber;
import mod.adrenix.nostalgic.tweak.gui.TweakSlider;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.io.PathUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;

import java.util.concurrent.TimeUnit;

public class GroupCreateBackup extends ManageGroup
{
    /* Static */

    private static final int SLIDER_WIDTH = 124;

    /* Fields */

    private final TweakNumber<Integer> tweak = ModTweak.NUMBER_OF_BACKUPS;
    private final TweakSlider slider = this.tweak.getSlider();

    /* Methods */

    @Override
    void define(ManageOverlay manager)
    {
        /* Header */

        Group header = Group.create(manager.overlay)
            .forceRelativeY()
            .icon(Icons.ADD)
            .title(Lang.Button.CREATE_BACKUP)
            .border(Color.GREEN_APPLE)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget.create(Lang.Manage.CREATE_BACKUP_HELP).width(header::getInsideWidth).build(header::addWidget);

        /* Folder */

        Group folder = Group.create(manager.overlay)
            .icon(Icons.FOLDER)
            .title(Lang.Button.VIEW_BACKUPS)
            .border(Color.SUMMER_YELLOW)
            .below(header, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget viewInformation = TextWidget.create(Lang.Manage.CREATE_BACKUP_VIEW)
            .width(folder::getInsideWidth)
            .build(folder::addWidget);

        ButtonTemplate.openFolder(PathUtil.getBackupPath())
            .useTextWidth()
            .centerInWidgetX(folder)
            .below(viewInformation, manager.padding * 2)
            .build(folder::addWidget);

        /* Client */

        Group client = Group.create(manager.overlay)
            .icon(Icons.CLIENT)
            .title(Lang.Button.CLIENT_BACKUP)
            .border(Color.MAYA_BLUE)
            .below(folder, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget clientInformation = TextWidget.create(Lang.Manage.CREATE_BACKUP_CLIENT)
            .width(client::getInsideWidth)
            .build(client::addWidget);

        SliderWidget clientMaxSlider = SliderTweak.create(this.slider, this.tweak::setLocal, this.tweak::fromLocal)
            .width(SLIDER_WIDTH)
            .centerInWidgetX(client, manager.padding + 20)
            .below(clientInformation, manager.padding * 2)
            .build(client::addWidget);

        ButtonWidget clientSaveMax = ButtonWidget.create()
            .icon(Icons.SAVE_FLOPPY)
            .tooltip(Lang.Button.SAVE, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.SAVE_MAX_BACKUP, 35)
            .rightOf(clientMaxSlider, manager.padding)
            .enableIf(this.tweak::isLocalSavable)
            .onPress(this::saveClientMax)
            .build(client::addWidget);

        ButtonWidget.create(Lang.Button.CREATE_BACKUP)
            .icon(Icons.ADD)
            .cooldown(1L, TimeUnit.SECONDS)
            .extendWidthTo(clientSaveMax, 1)
            .below(clientMaxSlider, manager.padding * 2)
            .posX(clientMaxSlider::getX)
            .onPress(this::clientBackup)
            .build(client::addWidget);

        /* Server */

        Group server = Group.create(manager.overlay)
            .icon(Icons.SERVER)
            .title(Lang.Button.SERVER_BACKUP)
            .border(Color.SHADOW_BLUE)
            .below(client, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget serverInformation = TextWidget.create(Lang.Manage.CREATE_BACKUP_SERVER)
            .width(server::getInsideWidth)
            .build(server::addWidget);

        SliderWidget serverMaxSlider = SliderTweak.create(this.slider, this.tweak::setNetwork, this.tweak::fromNetwork)
            .width(SLIDER_WIDTH)
            .centerInWidgetX(server, manager.padding + 20)
            .below(serverInformation, manager.padding * 2)
            .disableIf(NetUtil::isNotConnectedOrOperator)
            .build(server::addWidget);

        ButtonWidget serverSaveMax = ButtonWidget.create()
            .icon(Icons.SAVE_FLOPPY)
            .tooltip(Lang.Button.SAVE, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.SAVE_MAX_BACKUP, 35)
            .rightOf(serverMaxSlider, manager.padding)
            .onPress(this.tweak::applyCacheAndSend)
            .enableIf(CollectionUtil.areAllTrue(NetUtil::isConnectedAndOperator, this.tweak::isNetworkSavable))
            .build(server::addWidget);

        ButtonWidget.create(Lang.Button.CREATE_BACKUP)
            .icon(Icons.ADD)
            .cooldown(3L, TimeUnit.SECONDS)
            .below(serverMaxSlider, manager.padding * 2)
            .posX(serverMaxSlider::getX)
            .extendWidthTo(serverSaveMax, 1)
            .disableIf(NetUtil::isNotConnectedOrOperator)
            .onPress(this::serverBackup)
            .build(server::addWidget);
    }

    /**
     * Save the maximum allowed backup files within the client's filesystem.
     */
    private void saveClientMax()
    {
        this.tweak.applyCacheAndSend();
        ConfigCache.save();
    }

    /**
     * Create a new backup file on the client.
     */
    private void clientBackup()
    {
        if (ConfigBuilder.getHandler().backup())
            return;

        MessageOverlay.create(MessageType.RED_WARNING, Lang.Error.CREATE_BACKUP_TITLE, Lang.Error.CREATE_BACKUP_MESSAGE)
            .build()
            .open();
    }

    /**
     * Send a request to the server to make a backup file.
     */
    private void serverBackup()
    {
        PacketUtil.sendToServer(new ServerboundCreateBackup(true));
    }
}
