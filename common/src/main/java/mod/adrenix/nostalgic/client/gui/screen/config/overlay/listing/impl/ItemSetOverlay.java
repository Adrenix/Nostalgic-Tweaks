package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.impl;

import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.DeletableSetOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.ItemListingOverlay;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.listing.ItemSet;

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
}
