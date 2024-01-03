package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.impl;

import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.DeletableSetOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.ItemListingOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.ManageItemOverlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRow;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.listing.ItemSet;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import org.jetbrains.annotations.Nullable;

public class ItemSetOverlay extends ItemListingOverlay<String, ItemSet> implements DeletableSetOverlay<String, ItemSet>
{
    /* Fields */

    private final ItemSet set;

    /* Constructor */

    public ItemSetOverlay(TweakListing<String, ItemSet> tweak)
    {
        super(tweak);

        this.set = tweak.fromCache();
        this.createListRows();
    }

    /* Methods */

    @Override
    public ItemSet getSet()
    {
        return this.set;
    }

    @Override
    public @Nullable AbstractRow<?, ?> getRow(String key, String value)
    {
        if (this.set.isWildcard(key))
            return null;

        Row row = this.createRow(key, value);

        IconWidget icon = this.createIcon(row, key, value);
        ButtonWidget undo = this.createUndo(row, value);

        ButtonWidget manage = ButtonWidget.create(Lang.Button.MANAGE)
            .leftOf(undo, 2)
            .icon(Icons.MECHANICAL_TOOLS)
            .onPress(() -> new ManageItemOverlay(this.set, key).open())
            .disableIf(() -> this.set.isDeleted(value))
            .useTextWidth()
            .build(row::addWidget);

        ButtonWidget delete = this.createDelete(row, value, manage);
        TextWidget title = this.createTitle(row, key, value, icon, delete);

        IconWidget warning = IconWidget.create(Icons.WARNING)
            .belowAll(2, title, delete)
            .alignFlushTo(title)
            .size(GuiUtil.textHeight() - 1)
            .visibleIf(() -> this.set.containsWildcard(key))
            .build(row::addWidget);

        TextWidget.create(Lang.Listing.WILDCARD_ALERT)
            .rightOf(warning, 4)
            .extendWidthToEnd(row, 2)
            .visibleIf(() -> this.set.containsWildcard(key))
            .build(row::addWidget);

        return row;
    }
}
