package mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage;

import mod.adrenix.nostalgic.client.gui.overlay.types.state.SwitchGroup;
import mod.adrenix.nostalgic.client.gui.widget.group.Group;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.factory.TweakFlag;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.world.item.Items;

public class GroupMenuOptions extends ManageGroup
{
    /* Fields */

    private final TweakFlag tweak = ModTweak.PERSISTENT_CONFIG_SCREEN;

    /* Methods */

    @Override
    void define(ManageOverlay manager)
    {
        /* Toggle View */

        Group view = Group.create(manager.overlay)
            .forceRelativeY()
            .title(Lang.Manage.TOGGLE_VIEW_SCREEN)
            .icon(TextureIcon.fromItem(Items.SPYGLASS))
            .border(Color.SUMMER_YELLOW)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget.create(Lang.Manage.TOGGLE_VIEW_INFO).width(view::getInsideWidth).build(view::addWidget);

        /* Persistence */

        Group persistence = Group.create(manager.overlay)
            .icon(Icons.SAVE_FLOPPY)
            .title(Lang.Manage.PERSISTENCE)
            .border(Color.IRIS_BLUE)
            .below(view, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        Translation persistHead = Lang.Manage.PERSISTENT_SCREEN;
        Translation persistInfo = Lang.Manage.PERSISTENT_INFO;

        SwitchGroup.Widgets persistSwitch = SwitchGroup.create(manager.overlay, persistHead, persistInfo, this.tweak::get, this.tweak::setDiskAndSave)
            .getWidgets();

        persistSwitch.header().getBuilder().extendWidthToEnd(persistence, persistence.getInsidePaddingX());
        persistSwitch.description().getBuilder().extendWidthToEnd(persistence, persistence.getInsidePaddingX());
        persistSwitch.subscribeTo(persistence);
    }
}
