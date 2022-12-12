package mod.adrenix.nostalgic.client.config.gui.screen.list;

import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.list.row.ConfigRowKey;
import mod.adrenix.nostalgic.common.config.list.ListMap;
import mod.adrenix.nostalgic.util.common.ItemCommonUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.network.chat.Component;
import com.google.common.collect.Maps;
import net.minecraft.world.item.Item;

import java.util.*;

/**
 * The list map screen is responsible for managing configuration lists that use maps. The keys associated with these
 * maps must point to a resource location. For example, a diamond pickaxe entry should have a key that looks like:
 * <code>minecraft:diamond_pickaxe</code>. The value associated with these maps can be anything that can be stored in a
 * JSON file.
 *
 * @param <V> The value associated with this map.
 */

public class ListMapScreen<V> extends AbstractListScreen
{
    /* Fields */

    private final ArrayList<Map.Entry<String, V>> deletedEntries = new ArrayList<>();
    private final Map<String, V> defaultMap;
    private final Map<String, V> configMap;
    private final Map<String, V> copyMap;
    private final Map<String, V> undoMap;
    private final V resetValue;

    /* Constructors */

    /**
     * Create a new list screen that controls a map.
     * @param title The list screen title.
     * @param listMap A list map instance.
     */
    public ListMapScreen(Component title, ListMap<V> listMap)
    {
        super(title, listMap);

        this.resetValue = listMap.getResetValue();
        this.defaultMap = listMap.getDefaultMap();
        this.configMap = listMap.getConfigMap();
        this.copyMap = Maps.newHashMap();
        this.undoMap = Maps.newHashMap(this.configMap);

        this.sortByResource();
    }

    /* Getters */

    /**
     * Get this screen's current list of deleted map entries.
     * @return An array list of deleted map entries.
     */
    public ArrayList<Map.Entry<String, V>> getDeletedEntries() { return this.deletedEntries; }

    /**
     * Get the copied reset value associated with the given entry. If there is no cached value, then the provided
     * entry's value will be returned.
     *
     * @param entry The map entry to check for a copied reset value.
     * @return A cached copied reset value or the given entry's current value.
     */
    public V getCopiedValue(Map.Entry<String, V> entry)
    {
        if (this.copyMap.containsKey(entry.getKey()))
            return this.copyMap.get(entry.getKey());

        return entry.getValue();
    }

    /* Methods */

    /**
     * Copy an entry's value into the reset cache map for later use.
     * @param entry The map entry to cache in the reset map.
     */
    public void copy(Map.Entry<String, V> entry) { this.copyMap.put(entry.getKey(), entry.getValue()); }

    /**
     * Delete an entry from the map. The changes are not saved until confirmed by the user.
     * @param entry The entry to delete.
     */
    public void delete(Map.Entry<String, V> entry) { this.deletedEntries.add(entry); }

    /**
     * Remove an entry from the deleted entries list. The changes are not saved until confirmed by the user.
     * @param entry The entry to remove from the deleted entries array list.
     */
    public void undo(Map.Entry<String, V> entry) { this.deletedEntries.remove(entry); }

    /**
     * Checks if the given entry is contained within the deleted entries array list.
     * @param entry The entry to check.
     * @return Whether the given entry is currently in the deleted list.
     */
    public boolean isDeleted(Map.Entry<String, V> entry) { return this.deletedEntries.contains(entry); }

    /**
     * Delete an entry from the map using an item. The changes are not saved until confirmed by the user.
     * @param item The item to get data from.
     */
    @Override
    public void deleteItem(Item item)
    {
        for (Map.Entry<String, V> entry : this.configMap.entrySet())
        {
            if (entry.getKey().equals(ItemCommonUtil.getResourceKey(item)))
            {
                this.delete(entry);
                break;
            }
        }
    }

    /**
     * Clears all saved entries from this list's config map.
     */
    @Override
    public void clearAllSaved()
    {
        for (Map.Entry<String, V> entry : this.configMap.entrySet())
        {
            if (!this.deletedEntries.contains(entry))
                this.delete(entry);
        }
    }

    /**
     * Checks if the given item has been marked for deletion.
     * @param item The item to check.
     * @return Whether there is an item entry matching the given item in the deleted entries list.
     */
    @Override
    public boolean isItemDeleted(Item item)
    {
        for (Map.Entry<String, V> entry : this.deletedEntries)
        {
            if (ItemCommonUtil.getResourceKey(item).equals(entry.getKey()))
                return true;
        }

        return false;
    }

    /**
     * Save an item to this list's entries map. No changes are saved until confirmed by the user.
     * @param item The item to save.
     */
    @Override
    public void addItem(Item item)
    {
        if (this.isItemSaved(item) || !this.isItemEligible(item))
            return;

        this.configMap.put(ItemCommonUtil.getResourceKey(item), this.resetValue);
        this.sortByResource();
    }

    /**
     * Checks if the given item is in the undo map, if not, then this item was added.
     * @param item The item to check to see if it is added to the list.
     * @return Whether the given item was added by the user this session.
     */
    @Override
    public boolean isItemAdded(Item item) { return !this.undoMap.containsKey(ItemCommonUtil.getResourceKey(item)); }

    /**
     * Checks if the given item is saved to the list's saved entries.
     * @param item The item to check.
     */
    @Override
    public boolean isItemSaved(Item item)
    {
        for (Map.Entry<String, V> entry : this.configMap.entrySet())
        {
            if (entry.getKey().equals(ItemCommonUtil.getResourceKey(item)))
                return true;
        }

        return false;
    }

    /**
     * Sorts two localized item names alphabetically.
     * @param first The first entry.
     * @param second The second entry.
     * @return A negative integer, zero, or a positive integer ignoring case considerations.
     */
    private int getLocalizedItem(Map.Entry<String, V> first, Map.Entry<String, V> second)
    {
        String firstItem = ItemCommonUtil.getLocalizedItem(first.getKey());
        String secondItem = ItemCommonUtil.getLocalizedItem(second.getKey());

        return firstItem.compareToIgnoreCase(secondItem);
    }

    /**
     * Sorts the lists configuration entry map alphabetically.
     */
    private void sortByResource()
    {
        List<Map.Entry<String, V>> list = new ArrayList<>(this.configMap.entrySet());
        Map<String, V> unknowns = new LinkedHashMap<>();
        Map<String, V> result = new LinkedHashMap<>();

        for (Map.Entry<String, V> entry : list)
        {
            if (!ItemCommonUtil.isValidKey(entry.getKey()))
                unknowns.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, V> entry : unknowns.entrySet())
            list.remove(entry);

        list.sort(this::getLocalizedItem);

        for (Map.Entry<String, V> entry : list)
            result.put(entry.getKey(), entry.getValue());

        this.configMap.clear();
        this.configMap.putAll(unknowns);
        this.configMap.putAll(result);
    }

    /**
     * Gets the item resource key from an entry.
     * @param entry The entry to get the item resource key from.
     * @return The item resource key from the map entry.
     */
    private String getResourceKey(Map.Entry<String, V> entry) { return entry.getKey(); }

    /**
     * Adds a config row from a map entry to the given array list of rows.
     * @param rows The array list of config row list rows.
     * @param entry A map entry to reference.
     */
    private void addSavedRow(ArrayList<ConfigRowList.Row> rows, Map.Entry<String, V> entry)
    {
        rows.add(this.getConfigRowList().rowFromEntry(entry, this.resetValue));
    }

    /**
     * Adds a default entry config row from a set item resource key to the given array list of rows.
     * @param rows The array list of config row list rows.
     * @param entry A default map entry.
     */
    private void addDefaultRow(ArrayList<ConfigRowList.Row> rows, Map.Entry<String, V> entry)
    {
        rows.add(new ConfigRowKey.DefaultRow(entry.getKey()).generate());
    }

    /**
     * Get the rows associated with a list's saved items. If no items are saved, then a row with a message stating
     * that no items are saved will be shown.
     *
     * @return A list of config row list rows based on the current search query.
     */
    @Override
    protected ArrayList<ConfigRowList.Row> getSavedRows()
    {
        String langKey = LangUtil.Gui.LIST_NO_SAVED_ITEMS;

        return this.getSearchedItems(langKey, this.configMap.entrySet(), this::getResourceKey, this::addSavedRow);
    }

    /**
     * Get the rows associated with a list's default items. If no default entries are available, then the container that
     * shows default entries will not be available within the config row list.
     *
     * @return A list of config row list rows based on the current search query.
     */
    @Override
    protected ArrayList<ConfigRowList.Row> getDefaultRows()
    {
        String langKey = LangUtil.Gui.LIST_NO_DEFAULT_ITEMS;

        return this.getSearchedItems(langKey, this.defaultMap.entrySet(), this::getResourceKey, this::addDefaultRow);
    }

    /**
     * @return The number of default entries associated with this list.
     */
    @Override
    protected int getDefaultCount() { return this.defaultMap.size(); }

    /**
     * Disables all default entries by adding them to the disabled default entries set.
     */
    @Override
    public void disableAllDefaults()
    {
        for (Map.Entry<String, V> entry : this.defaultMap.entrySet())
            this.disabledDefaults.add(entry.getKey());
    }

    /**
     * Enables all default entries by removing them from the disabled default entries set.
     */
    @Override
    public void enableAllDefaults()
    {
        for (Map.Entry<String, V> entry : this.defaultMap.entrySet())
            this.disabledDefaults.remove(entry.getKey());
    }

    /**
     * Checks if the list has been changed and can be saved by the user.
     * @return Whether changes are savable.
     */
    @Override
    protected boolean isListSavable()
    {
        if (this.undoMap.size() != this.configMap.size())
            return true;
        else if (this.undoDisabledDefaults.size() != this.disabledDefaults.size())
            return true;
        else if (this.deletedEntries.size() > 0)
            return true;

        for (Map.Entry<String, V> entry : this.configMap.entrySet())
        {
            if (!this.undoMap.get(entry.getKey()).equals(entry.getValue()))
                return true;
        }

        return false;
    }

    /**
     * Instructions to run when the screen is closed.
     * @param isCancelled Whether changes made are cancelled.
     */
    @Override
    protected void closeList(boolean isCancelled)
    {
        if (!isCancelled)
        {
            for (Map.Entry<String, V> entry : this.deletedEntries)
                this.configMap.remove(entry.getKey());
        }
        else
        {
            this.configMap.clear();
            this.configMap.putAll(this.undoMap);

            this.disabledDefaults.clear();
            this.disabledDefaults.addAll(this.undoDisabledDefaults);

            this.sortByResource();
        }

        this.deletedEntries.clear();
        this.getMinecraft().setScreen(this.parentScreen);
    }
}
