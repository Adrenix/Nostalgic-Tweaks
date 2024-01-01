package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.impl;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.DeletableSetOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.InvalidTypeOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.ListingWidgets;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.add.StringOverlay;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.listing.StringSet;
import mod.adrenix.nostalgic.util.common.data.Pair;

import java.util.Collection;
import java.util.HashMap;

public class StringSetOverlay implements DeletableSetOverlay<String, StringSet>
{
    /* Fields */

    private final ListingWidgets<String, StringSet> widgets;
    private final TweakListing<String, StringSet> tweak;
    private final StringSet set;
    private final Overlay overlay;

    /* Constructor */

    public StringSetOverlay(TweakListing<String, StringSet> tweak)
    {
        this.tweak = tweak;
        this.set = tweak.fromCache();
        this.overlay = this.getDefaultOverlay().build();
        this.widgets = new ListingWidgets<>(this);
        this.createListRows();
    }

    /* Methods */

    @Override
    public StringSet getSet()
    {
        return this.set;
    }

    @Override
    public TweakListing<String, StringSet> getTweak()
    {
        return this.tweak;
    }

    @Override
    public ListingWidgets<String, StringSet> getWidgets()
    {
        return this.widgets;
    }

    @Override
    public Overlay getOverlay()
    {
        return this.overlay;
    }

    @Override
    public void onAdd()
    {
        if (this.getListing().genericType().equals(String.class))
            new StringOverlay<>(this.getListing(), this::createListRows, this::addString).open();
        else
            new InvalidTypeOverlay().open();
    }

    @Override
    public String getLocalizedKey(String listKey)
    {
        return listKey;
    }

    @Override
    public HashMap<Pair<String, String>, String> getLocalizedEntries(Collection<Pair<String, String>> collection)
    {
        HashMap<Pair<String, String>, String> localized = new HashMap<>();
        collection.forEach(pair -> localized.put(pair, pair.left()));

        return localized;
    }

    /**
     * Adds the given string to the listing and updates the row list.
     *
     * @param string A string.
     */
    private void addString(String string)
    {
        this.onRowAdd(string);

        this.createListRows();
        this.widgets.findAndHighlight(string);
    }
}
