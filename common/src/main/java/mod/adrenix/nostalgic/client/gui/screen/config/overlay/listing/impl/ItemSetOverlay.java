package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.impl;

import mod.adrenix.nostalgic.client.gui.overlay.types.state.SwitchGroup;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.DeletableSetOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.ItemListingOverlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconTemplate;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRow;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.listing.ItemSet;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import org.jetbrains.annotations.Nullable;

public class ItemSetOverlay extends ItemListingOverlay<String, ItemSet> implements DeletableSetOverlay<String, ItemSet>
{
    /* Fields */

    private final TweakListing<String, ItemSet> tweak;

    /* Constructor */

    public ItemSetOverlay(TweakListing<String, ItemSet> tweak)
    {
        super(tweak);

        this.tweak = tweak;
        this.createListRows();
    }

    /* Methods */

    @Override
    public ItemSet getSet()
    {
        return this.tweak.fromCache();
    }

    @Override
    public @Nullable AbstractRow<?, ?> getRow(String key, String value)
    {
        if (this.getSet().isWildcard(key))
            return null;

        Row row = this.createRow(key, value);
        IntegerHolder tabOrder = IntegerHolder.create(0);

        IconWidget icon = this.getIconFactory(key, value).build(row::addWidget);
        ButtonWidget undo = this.getUndoBuilder(value).fromWidgetEndX(row, 2).build(row::addWidget);
        ButtonWidget delete = this.getDeleteBuilder(value).leftOf(undo, 1).build(row::addWidget);
        TextWidget title = this.getTitleBuilder(key, value, icon, () -> this.isWildcardChanged(key))
            .onPress(() -> new ItemInfoOverlay(key).open(), Color.LEMON_YELLOW)
            .extendWidthTo(delete, 2)
            .build(row::addWidget);

        SwitchGroup.Widgets wildcard = this.getWildcardWidgets(row, key);

        wildcard.toggle().getBuilder().disableIf(delete::isInactive).belowAll(2, title, delete).alignFlushTo(title);
        wildcard.header().getBuilder().useTextWidth();
        wildcard.description().getBuilder().extendWidthToEnd(row, 2);
        wildcard.subscribeTo(row);

        IconWidget help = IconTemplate.help()
            .onPress(this.wildcardHelp::open)
            .alignVerticalTo(wildcard.toggle(), -1)
            .rightOf(wildcard.header(), 4)
            .build(row::addWidget);

        title.setTabOrderGroup(tabOrder.getAndIncrement());
        delete.setTabOrderGroup(tabOrder.getAndIncrement());
        undo.setTabOrderGroup(tabOrder.getAndIncrement());
        wildcard.toggle().setTabOrderGroup(tabOrder.getAndIncrement());
        help.setTabOrderGroup(tabOrder.getAndIncrement());

        return row;
    }
}
