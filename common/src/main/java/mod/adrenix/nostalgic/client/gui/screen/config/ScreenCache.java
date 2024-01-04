package mod.adrenix.nostalgic.client.gui.screen.config;

import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.GroupRow;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.RowProvider;
import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRow;
import mod.adrenix.nostalgic.tweak.container.Category;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;

import java.util.HashSet;

class ScreenCache
{
    Container category = Category.MOD;
    RowProvider rowProvider = RowProvider.get();
    final HashSet<Container> containers = new HashSet<>();
    boolean pushed = false;
    double scrollAmount = 0.0D;
    String search = "";

    /**
     * Reset the cache back to its default state.
     */
    public void reset()
    {
        this.category = Category.MOD;
        this.rowProvider = RowProvider.get();
        this.containers.clear();
        this.pushed = false;
        this.scrollAmount = 0.0D;
        this.search = "";
    }

    /**
     * @return Whether the cache has already been pushed into memory.
     */
    public boolean isPushed()
    {
        return this.pushed;
    }

    /**
     * Caches important widget values so that those values can be restored at a later point in time.
     */
    public void push(ConfigScreen screen)
    {
        this.pushed = true;
        this.containers.clear();

        screen.getWidgetManager().getRowList().getRows().forEach(this::saveOpenedGroups);

        this.search = screen.getWidgetManager().getQuery();
        this.scrollAmount = screen.getWidgetManager().getRowList().getScrollAmount();
        this.category = screen.getCategory();
        this.rowProvider = RowProvider.get();
    }

    /**
     * Restores important widget values cached from an earlier point in time.
     */
    public void pop(ConfigScreen screen)
    {
        this.pushed = false;

        screen.setCategory(this.category);
        this.rowProvider.useAndThen(() -> screen.getWidgetManager().populateFromProvider());

        if (!this.search.isEmpty())
            screen.getWidgetManager().setQuery(this.search);

        this.openSavedGroups(screen);

        screen.getWidgetManager().getRowList().setScrollAmount(this.scrollAmount);
    }

    /**
     * Checks if the given row is a group row, and if so, adds the group row's container to the opened containers cache
     * if the group row is expanded.
     *
     * @param row A row list row instance to check.
     */
    private void saveOpenedGroups(AbstractRow<?, ?> row)
    {
        ClassUtil.cast(row, GroupRow.class)
            .stream()
            .filter(GroupRow::isExpanded)
            .map(GroupRow::getContainer)
            .forEach(this.containers::add);
    }

    /**
     * Opens all previously expanded group rows that had their containers cached.
     */
    private void openSavedGroups(ConfigScreen screen)
    {
        if (this.containers.isEmpty())
            return;

        int openedSize = this.containers.size();
        HashSet<AbstractRow<?, ?>> rows = new HashSet<>(screen.getWidgetManager().getRowList().getRows());

        CollectionUtil.fromCast(rows, GroupRow.class)
            .filter(group -> this.containers.contains(group.getContainer()))
            .forEachOrdered(group -> {
                this.containers.remove(group.getContainer());
                group.expand();
            });

        if (openedSize != this.containers.size())
            this.openSavedGroups(screen);
    }
}
