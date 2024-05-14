package mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage;

import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.group.Group;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.client.gui.LinkUtil;
import mod.adrenix.nostalgic.util.client.search.SearchTag;
import mod.adrenix.nostalgic.util.common.LinkLocation;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.function.ForEachWithPrevious;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Items;

import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;

public class GroupHelp extends ManageGroup
{
    @Override
    void define(ManageOverlay manager)
    {
        /* Header */

        Group heading = Group.create(manager.overlay)
            .icon(Icons.TOOLTIP_HOVER)
            .title(Lang.Button.HELP)
            .border(Color.LEMON_YELLOW)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .forceRelativeY()
            .build(this::register);

        TextWidget sectionInformation = TextWidget.create(Lang.Help.HEADER)
            .width(heading::getInsideWidth)
            .build(heading::addWidget);

        ButtonWidget.create(Lang.Home.DISCORD)
            .icon(Icons.DISCORD)
            .tooltip(Lang.Home.DISCORD, 35, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.OPEN_DISCORD, 35)
            .onPress(LinkUtil.onPress(LinkLocation.DISCORD))
            .below(sectionInformation, manager.padding * 2)
            .centerInWidgetX(heading)
            .useTextWidth()
            .build(heading::addWidget);

        /* Tweak Tags */

        Group tweakTags = Group.create(manager.overlay)
            .icon(TextureIcon.fromItem(Items.NAME_TAG))
            .title(Lang.Help.TWEAK_TAGS_TITLE)
            .border(Color.LATTE_GOLD)
            .below(heading, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget.create(Lang.Help.TWEAK_TAGS_MESSAGE).width(tweakTags::getInsideWidth).build(tweakTags::addWidget);

        /* Search Tags */

        LinkedHashSet<TextWidget> searchTextWidgets = new LinkedHashSet<>();

        Group searchTags = Group.create(manager.overlay)
            .icon(Icons.SEARCH)
            .title(Lang.Help.SEARCH_TAGS_TITLE)
            .border(Color.CORNFLOWER_BLUE)
            .below(tweakTags, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget searchTagsMessage = TextWidget.create(Lang.Help.SEARCH_TAGS_MESSAGE)
            .width(searchTags::getInsideWidth)
            .build(searchTags::addWidget);

        SearchTag.stream().forEach(tag -> {
            TextWidget text = TextWidget.create(tag.getColor() + "#" + tag + ChatFormatting.RESET + ": " + tag.toDescription())
                .width(searchTags::getInsideWidth)
                .build();

            searchTextWidgets.add(text);
            searchTags.addWidget(text);
        });

        ForEachWithPrevious.create(searchTextWidgets)
            .applyToFirst((first) -> first.getBuilder().below(searchTagsMessage, manager.padding * 3))
            .forEach((last, next) -> next.getBuilder().below(last, manager.padding * 3))
            .run();

        /* Keyboard Shortcuts */

        Group shortcut = Group.create(manager.overlay)
            .icon(Icons.BUTTON)
            .title(Lang.Help.SHORTCUT_TITLE)
            .border(Color.SONIC_SILVER)
            .below(searchTags, manager.padding)
            .rightOf(manager.separator, manager.padding)
            .extendWidthToScreenEnd(0)
            .build(this::register);

        TextWidget shortcutMessage = TextWidget.create(Lang.Help.SHORTCUT_MESSAGE)
            .width(shortcut::getInsideWidth)
            .build(shortcut::addWidget);

        TextWidget shortcutSearch = TextWidget.create(Lang.Help.SHORTCUT_SEARCH)
            .below(shortcutMessage, manager.padding * 3)
            .width(shortcut::getInsideWidth)
            .build(shortcut::addWidget);

        TextWidget shortcutSave = TextWidget.create(Lang.Help.SHORTCUT_SAVE)
            .below(shortcutSearch, manager.padding * 3)
            .width(shortcut::getInsideWidth)
            .build(shortcut::addWidget);

        TextWidget shortcutExit = TextWidget.create(Lang.Help.SHORTCUT_EXIT)
            .below(shortcutSave, manager.padding * 3)
            .width(shortcut::getInsideWidth)
            .build(shortcut::addWidget);

        TextWidget shortcutJump = TextWidget.create(Lang.Help.SHORTCUT_JUMP)
            .below(shortcutExit, manager.padding * 3)
            .width(shortcut::getInsideWidth)
            .build(shortcut::addWidget);

        TextWidget shortcutAll = TextWidget.create(Lang.Help.SHORTCUT_ALL)
            .below(shortcutJump, manager.padding * 3)
            .width(shortcut::getInsideWidth)
            .build(shortcut::addWidget);

        TextWidget.create(Lang.Help.SHORTCUT_CATEGORY)
            .below(shortcutAll, manager.padding * 3)
            .width(shortcut::getInsideWidth)
            .build(shortcut::addWidget);
    }
}
