package mod.adrenix.nostalgic.client.gui.screen.config.widget.list;

import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

class TweakDescription
{
    /* Static */

    /**
     * Setup tweak row descriptions using the given layout.
     *
     * @param layout A {@link TweakRowLayout} instance.
     */
    static void init(TweakRowLayout layout)
    {
        new TweakDescription(layout).create(Lang.TweakRow.DESCRIPTION, layout.tweak.getDescription(), TextureIcon.fromItem(Items.BOOK), Color.fromFormatting(ChatFormatting.GRAY), BooleanSupplier.ALWAYS);
        new TweakDescription(layout).create(Lang.Tag.ALERT, layout.tweak.getAlertMessage(), Icons.ALERT, Color.fromFormatting(ChatFormatting.RED), layout.tweak::isAlertTag);
        new TweakDescription(layout).create(Lang.Tag.WARNING, layout.tweak.getWarningMessage(), Icons.WARNING, Color.fromFormatting(ChatFormatting.YELLOW), layout.tweak::isWarningTag);
        new TweakDescription(layout).create(Lang.Tag.CONFLICT, layout.tweak.getConflictMessage(), Icons.WARNING, Color.fromFormatting(ChatFormatting.RED), layout.tweak::isModConflict);
        new TweakDescription(layout).create(Lang.Tag.NO_SSO_HEADER, layout.tweak.getNoSSOMessage(), Icons.WARNING, Color.fromFormatting(ChatFormatting.YELLOW), layout.tweak::isNotSSO);

        layout.tweak.getModIssues()
            .forEach(issue -> new TweakDescription(layout).create(issue.getTitle(), issue.getDescription(layout.tweak), issue.getIcon(), issue.getColor(), issue.isActive()));
    }

    /* Fields */

    private final TweakRowLayout layout;

    /* Constructor */

    private TweakDescription(TweakRowLayout layout)
    {
        this.layout = layout;
    }

    /* Methods */

    private void create(Translation header, Component description, TextureIcon icon, Color color, BooleanSupplier visibleIf)
    {
        TextWidget title = TextWidget.create(header)
            .icon(icon)
            .color(color)
            .visibleIf(visibleIf)
            .alignFlushTo(this.layout.breadcrumbs)
            .extendWidthToEnd(this.layout.row, this.layout.padding)
            .belowAll(this.layout.descriptions, this.layout.padding * 2)
            .below(this.layout.title, this.layout.padding * 2)
            .build(this.layout.row::addWidget);

        TextWidget body = TextWidget.create(description)
            .visibleIf(visibleIf)
            .below(title, 3)
            .alignFlushTo(this.layout.breadcrumbs)
            .extendWidthToEnd(this.layout.row, this.layout.padding)
            .build(this.layout.row::addWidget);

        this.layout.descriptions.add(body);
    }
}
