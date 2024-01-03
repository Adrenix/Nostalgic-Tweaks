package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.impl;

import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.DeletableMapOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.ItemListingOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.ManageItemOverlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.ActiveBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRow;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.listing.ItemMap;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.data.Holder;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import org.jetbrains.annotations.Nullable;

public class ItemMapOverlay<V> extends ItemListingOverlay<V, ItemMap<V>> implements DeletableMapOverlay<V, ItemMap<V>>
{
    /* Fields */

    private final ItemMap<V> map;

    /* Constructor */

    public ItemMapOverlay(TweakListing<V, ItemMap<V>> tweak)
    {
        super(tweak);

        this.map = tweak.fromCache();
        this.createListRows();
    }

    /* Methods */

    @Override
    public ItemMap<V> getMap()
    {
        return this.map;
    }

    @Override
    public @Nullable AbstractRow<?, ?> getRow(String key, V value)
    {
        if (this.map.isWildcard(key))
            return null;

        Row row = this.createRow(key);
        Holder<V> cache = Holder.create(this.getMap().getOrDeleted(key));
        IntegerHolder tabOrder = IntegerHolder.create(0);

        IconWidget icon = this.createIcon(row, key, this.getRowIcon(key));
        TextWidget title = this.createTitle(row, key, cache, icon);

        IconWidget warning = IconWidget.create(Icons.WARNING)
            .below(title, 4)
            .alignFlushTo(title)
            .size(GuiUtil.textHeight() - 1)
            .visibleIf(() -> this.map.containsWildcard(key))
            .build(row::addWidget);

        TextWidget wildcard = TextWidget.create(Lang.Listing.WILDCARD_ALERT)
            .rightOf(warning, 4)
            .extendWidthToEnd(row, 2)
            .visibleIf(() -> this.map.containsWildcard(key))
            .build(row::addWidget);

        Grid controls = Grid.create(row, 2)
            .posX(2)
            .columnSpacing(1)
            .belowAll(4, icon, title, wildcard)
            .width(() -> (int) (row.getWidth() * 0.5F))
            .build(row::addWidget);

        Grid extras = Grid.create(row, 2)
            .posX(2)
            .columnSpacing(1)
            .below(controls, 1)
            .width(() -> (int) (row.getWidth() * 0.5F))
            .build(row::addWidget);

        ButtonWidget delete = this.createDelete(row, key);

        ButtonWidget manage = ButtonWidget.create(Lang.Button.MANAGE)
            .icon(Icons.MECHANICAL_TOOLS)
            .onPress(() -> new ManageItemOverlay(this.map, key).open())
            .disableIf(() -> this.map.isDeleted(key))
            .build();

        ButtonWidget undo = this.createUndo(row, key, cache, manage);
        ButtonWidget reset = this.createReset(row, key, this.getResetValue(key), undo);

        controls.addCells(undo, reset);
        extras.addCells(delete, manage);

        DynamicWidget<?, ?> controller = this.getController(key, row, controls);

        if (controller.getBuilder() instanceof ActiveBuilder<?, ?> builder)
            builder.disableIf(() -> this.map.isDeleted(key));

        undo.setTabOrderGroup(tabOrder.getAndIncrement());
        reset.setTabOrderGroup(tabOrder.getAndIncrement());
        controller.setTabOrderGroup(tabOrder.getAndIncrement());
        delete.setTabOrderGroup(tabOrder.getAndIncrement());
        manage.setTabOrderGroup(tabOrder.getAndIncrement());

        return row;
    }
}
