package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.impl;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.DeletableSetOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.InvalidTypeOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.ListingWidgets;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.add.StringOverlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRow;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.listing.StringSet;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import mod.adrenix.nostalgic.util.common.data.Pair;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

public class StringSetOverlay implements DeletableSetOverlay<String, StringSet>
{
    /* Fields */

    private final LinkedHashMap<String, String> undo;
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
        this.undo = new LinkedHashMap<>();
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
            new StringOverlay(this.set, this::createListRows, this::addString).open();
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

    @Override
    public @Nullable AbstractRow<?, ?> getRow(String key, String value)
    {
        IntegerHolder tabOrder = IntegerHolder.create(0);
        Runnable onEmptyEdit = () -> this.getSet().delete(value);

        Consumer<String> onEditFinish = (updated) -> {
            if (updated.equals(value))
                return;

            this.undo.put(updated, value);
            this.editString(value, updated);
        };

        Row row = this.createRow(key, value);

        IconWidget icon = this.getIconFactory(key, value).build(row::addWidget);

        ButtonWidget undo = this.getUndoBuilder(value).fromWidgetEndX(row, 2).onPress(() -> {
            if (this.undo.containsKey(value))
            {
                String undone = this.undo.get(value);

                this.undo.remove(value);
                this.editString(value, undone);
            }
            else
                this.getSet().add(value);
        }).disableIf(() -> {
            if (this.undo.containsKey(value))
                return false;

            return this.getSet().contains(value) || this.isLocked();
        }).build(row::addWidget);

        ButtonWidget delete = this.getDeleteBuilder(value).leftOf(undo, 1).build(row::addWidget);

        ButtonWidget edit = ButtonWidget.create(Lang.Button.EDIT)
            .icon(Icons.PENCIL)
            .onPress(() -> new StringOverlay(this.set, onEmptyEdit, onEditFinish, value).open())
            .disableIf(() -> this.getSet().isDeleted(value) || this.isLocked())
            .useTextWidth()
            .leftOf(delete, 1)
            .build(row::addWidget);

        this.getTitleBuilder(key, value, icon).extendWidthTo(edit, 2).build(row::addWidget);

        edit.setTabOrderGroup(tabOrder.getAndIncrement());
        delete.setTabOrderGroup(tabOrder.getAndIncrement());
        undo.setTabOrderGroup(tabOrder.getAndIncrement());

        return row;
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

    /**
     * Removes the old string and adds the new string.
     *
     * @param remove The old string to remove.
     * @param add    The new string to add.
     */
    private void editString(String remove, String add)
    {
        this.getSet().remove(remove);
        this.onRowAdd(add);
        this.createListRows();

        this.widgets.findAndHighlight(add, false);
    }
}