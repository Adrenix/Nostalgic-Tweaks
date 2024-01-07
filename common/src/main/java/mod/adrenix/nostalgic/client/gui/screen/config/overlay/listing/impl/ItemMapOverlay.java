package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.impl;

import mod.adrenix.nostalgic.client.gui.overlay.types.state.SwitchGroup;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.DeletableMapOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.ItemListingOverlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.ActiveBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconTemplate;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRow;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.listing.ItemMap;
import mod.adrenix.nostalgic.util.common.data.Holder;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import org.jetbrains.annotations.Nullable;

public class ItemMapOverlay<V> extends ItemListingOverlay<V, ItemMap<V>> implements DeletableMapOverlay<V, ItemMap<V>>
{
    /* Fields */

    private final TweakListing<V, ItemMap<V>> tweak;

    /* Constructor */

    public ItemMapOverlay(TweakListing<V, ItemMap<V>> tweak)
    {
        super(tweak);

        this.tweak = tweak;
        this.createListRows();
    }

    /* Methods */

    @Override
    public ItemMap<V> getMap()
    {
        return this.tweak.fromCache();
    }

    @Override
    public @Nullable AbstractRow<?, ?> getRow(String key, V value)
    {
        if (this.getMap().isWildcard(key))
            return null;

        Row row = this.createRow(key);
        Holder<V> cache = Holder.create(this.getMap().getOrDeleted(key));
        IntegerHolder tabOrder = IntegerHolder.create(0);

        IconWidget icon = this.getIconFactory(key, this.getRowIcon(key)).build(row::addWidget);
        TextWidget title = this.getTitleBuilder(key, icon, cache, () -> this.isWildcardChanged(key))
            .build(row::addWidget);

        ButtonWidget delete = this.getDeleteBuilder(key).posY(2).fromWidgetEndX(row, 2).build(row::addWidget);
        SwitchGroup.Widgets wildcard = this.getWildcardWidgets(row, key);

        wildcard.toggle().getBuilder().belowAll(2, title, delete).disableIf(delete::isInactive).alignFlushTo(title);
        wildcard.header().getBuilder().useTextWidth();
        wildcard.description().getBuilder().extendWidthToEnd(row, 2);
        wildcard.subscribeTo(row);

        IconWidget help = IconTemplate.help()
            .onPress(this.wildcardHelp::open)
            .alignVerticalTo(wildcard.toggle(), -1)
            .rightOf(wildcard.header(), 4)
            .build(row::addWidget);

        Grid controls = Grid.create(row, 2)
            .posX(2)
            .columnSpacing(1)
            .below(wildcard.description(), 4)
            .width(() -> (int) (row.getWidth() * 0.5F))
            .build(row::addWidget);

        ButtonWidget undo = this.getUndoBuilder(key, cache).build(controls::addCell);
        ButtonWidget reset = this.getResetBuilder(key, this.getResetValue(key)).build(controls::addCell);

        DynamicWidget<?, ?> controller = this.getController(key, row, controls);

        if (controller.getBuilder() instanceof ActiveBuilder<?, ?> builder)
            builder.disableIf(() -> this.getMap().isDeleted(key));

        delete.setTabOrderGroup(tabOrder.getAndIncrement());
        help.setTabOrderGroup(tabOrder.getAndIncrement());
        undo.setTabOrderGroup(tabOrder.getAndIncrement());
        reset.setTabOrderGroup(tabOrder.getAndIncrement());
        controller.setTabOrderGroup(tabOrder.getAndIncrement());

        return row;
    }
}
