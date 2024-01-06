package mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage;

import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.controller.BooleanController;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.group.Group;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.config.cache.ConfigCache;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;

import java.util.concurrent.TimeUnit;

public class GroupServerOps extends ManageGroup
{
    @Override
    void define(ManageOverlay manager)
    {
        /* Header */

        Group header = Group.create(manager.overlay)
            .forceRelativeY()
            .icon(Icons.WARNING)
            .title(Lang.Manage.OPERATIONS_WIP)
            .border(Color.SCHOOL_BUS)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget.create(Lang.Manage.OPERATIONS_WIP_MESSAGE).width(header::getInsideWidth).build(header::addWidget);

        /* Restricted LAN */

        Group lan = Group.create(manager.overlay)
            .icon(Icons.CLIENT)
            .title(Lang.Manage.OPERATIONS_LAN)
            .border(Color.MAYA_BLUE)
            .below(header, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget lanInformation = TextWidget.create(Lang.Manage.OPERATIONS_LAN_MESSAGE)
            .width(lan::getInsideWidth)
            .build(lan::addWidget);

        ButtonWidget setRestriction = new BooleanController(ModTweak.RESTRICTED_LAN).getBuilder()
            .centerInWidgetX(lan, manager.padding + 20)
            .below(lanInformation, manager.padding * 2)
            .build(lan::addWidget);

        ButtonWidget.create()
            .icon(Icons.SAVE_FLOPPY)
            .tooltip(Lang.Button.SAVE, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.SAVE_LAN, 35)
            .rightOf(setRestriction, manager.padding)
            .enableIf(ModTweak.RESTRICTED_LAN::isLocalSavable)
            .onPress(CollectionUtil.runAll(ModTweak.RESTRICTED_LAN::applyCacheAndSend, ConfigCache::save))
            .build(lan::addWidget);

        /* SSO Mode */

        Group sso = Group.create(manager.overlay)
            .icon(Icons.SERVER)
            .title(Lang.Manage.OPERATIONS_SSO)
            .border(Color.SHADOW_BLUE)
            .below(lan, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget ssoInformation = TextWidget.create(Lang.Manage.OPERATIONS_SSO_MESSAGE)
            .width(sso::getInsideWidth)
            .build(sso::addWidget);

        ButtonWidget setSSO = new BooleanController(ModTweak.SERVER_SIDE_ONLY).getBuilder()
            .disabledTooltip(Lang.Tooltip.NOT_CONNECTED_OR_OPERATOR, 35, 500, TimeUnit.MILLISECONDS)
            .disableIf(NetUtil::isNotConnectedOrOperator)
            .centerInWidgetX(sso, manager.padding + 20)
            .below(ssoInformation, manager.padding * 2)
            .build(sso::addWidget);

        ButtonWidget.create()
            .icon(Icons.SAVE_FLOPPY)
            .tooltip(Lang.Button.SAVE, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.SAVE_SSO, 35)
            .rightOf(setSSO, manager.padding)
            .onPress(ModTweak.SERVER_SIDE_ONLY::applyCacheAndSend)
            .enableIf(CollectionUtil.areAllTrue(NetUtil::isConnectedAndOperator, ModTweak.SERVER_SIDE_ONLY::isNetworkSavable))
            .build(sso::addWidget);

        /* Logging */

        Group logging = Group.create(manager.overlay)
            .icon(Icons.CLIPBOARD)
            .title(Lang.Manage.OPERATIONS_LOGGING)
            .border(Color.DEER_BROWN)
            .below(sso, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget loggingInformation = TextWidget.create(Lang.Manage.OPERATIONS_LOGGING_MESSAGE)
            .width(logging::getInsideWidth)
            .build(logging::addWidget);

        ButtonWidget setLogging = new BooleanController(ModTweak.SERVER_LOGGING).getBuilder()
            .disabledTooltip(Lang.Tooltip.NOT_CONNECTED_OR_OPERATOR, 35, 500, TimeUnit.MILLISECONDS)
            .disableIf(NetUtil::isNotConnectedOrOperator)
            .centerInWidgetX(logging, manager.padding + 20)
            .below(loggingInformation, manager.padding * 2)
            .build(logging::addWidget);

        ButtonWidget.create()
            .icon(Icons.SAVE_FLOPPY)
            .tooltip(Lang.Button.SAVE, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.SAVE_TWEAK_NETWORK, 35)
            .rightOf(setLogging, manager.padding)
            .onPress(ModTweak.SERVER_LOGGING::applyCacheAndSend)
            .enableIf(CollectionUtil.areAllTrue(NetUtil::isConnectedAndOperator, ModTweak.SERVER_LOGGING::isNetworkSavable))
            .build(logging::addWidget);

        /* Debug Mode */

        Group debug = Group.create(manager.overlay)
            .icon(Icons.BUG)
            .title(Lang.Manage.OPERATIONS_DEBUG)
            .border(Color.ALERT_RED)
            .below(logging, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget debugInformation = TextWidget.create(Lang.Manage.OPERATIONS_DEBUG_MESSAGE)
            .width(debug::getInsideWidth)
            .build(debug::addWidget);

        ButtonWidget setDebug = new BooleanController(ModTweak.SERVER_DEBUG).getBuilder()
            .disabledTooltip(Lang.Tooltip.NOT_CONNECTED_OR_OPERATOR, 35, 500, TimeUnit.MILLISECONDS)
            .disableIf(NetUtil::isNotConnectedOrOperator)
            .centerInWidgetX(debug, manager.padding + 20)
            .below(debugInformation, manager.padding * 2)
            .build(debug::addWidget);

        ButtonWidget.create()
            .icon(Icons.SAVE_FLOPPY)
            .tooltip(Lang.Button.SAVE, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.SAVE_TWEAK_NETWORK, 35)
            .rightOf(setDebug, manager.padding)
            .onPress(ModTweak.SERVER_DEBUG::applyCacheAndSend)
            .enableIf(CollectionUtil.areAllTrue(NetUtil::isConnectedAndOperator, ModTweak.SERVER_DEBUG::isNetworkSavable))
            .build(debug::addWidget);
    }
}
