package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.impl;

import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.DeletableMapOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.ItemListingOverlay;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.listing.ItemMap;

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
}
