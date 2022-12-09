package mod.adrenix.nostalgic.client.config.gui.screen.list;

import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.group.TextGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextAlign;
import mod.adrenix.nostalgic.common.config.list.ListId;
import mod.adrenix.nostalgic.util.client.ItemClientUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import com.google.common.collect.Maps;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private final Map<String, V> configMap;
    private final Map<String, V> copyMap;
    private final Map<String, V> undoMap;
    private final V resetValue;

    /* Constructors */

    /**
     * Create a new list screen that controls a map.
     * @param parentScreen The parent configuration screen.
     * @param title The list screen title.
     * @param configMap A map from the client config.
     * @param resetValue A value to reset to when the reset button is pressed.
     * @param listId A list screen identifier.
     * @param listFilter A list filter that controls what items appear in the all items list.
     * @param flags A var args list of list flags for this map screen.
     */
    public ListMapScreen
    (
        ConfigScreen parentScreen,
        Component title,
        Map<String, V> configMap,
        V resetValue,
        ListId listId,
        ListFilter listFilter,
        ListFlags ...flags
    )
    {
        super(parentScreen, title, listId, listFilter, flags);

        this.resetValue = resetValue;
        this.configMap = configMap;
        this.copyMap = Maps.newHashMap();
        this.undoMap = Maps.newHashMap(configMap);

        this.sortByResource();
    }

    /**
     * Create a new list screen (with no filters) that controls a map.
     * @param parentScreen The parent configuration screen.
     * @param title The list screen title.
     * @param configMap A map from the client config.
     * @param resetValue A value to reset to when the reset button is pressed.
     * @param listId A list screen identifier.
     * @param flags A var args list of list flags for this map screen.
     */
    public ListMapScreen
    (
        ConfigScreen parentScreen,
        Component title,
        Map<String, V> configMap,
        V resetValue,
        ListId listId,
        ListFlags ...flags
    )
    {
        this(parentScreen, title, configMap, resetValue, listId, ListFilter.ALL, flags);
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
            if (entry.getKey().equals(ItemClientUtil.getResourceKey(item)))
            {
                this.delete(entry);
                break;
            }
        }
    }

    /**
     * Checks if the given item is in the undo map, if not, then this item was added.
     * @param item The item to check to see if it is added to the list.
     * @return Whether the given item was added by the user this session.
     */
    @Override
    public boolean isItemAdded(Item item) { return !this.undoMap.containsKey(ItemClientUtil.getResourceKey(item)); }

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
            if (ItemClientUtil.getResourceKey(item).equals(entry.getKey()))
                return true;
        }

        return false;
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
     * Checks if the given item is saved to the list's saved entries.
     * @param item The item to check.
     */
    @Override
    public boolean isItemSaved(Item item)
    {
        for (Map.Entry<String, V> entry : this.configMap.entrySet())
        {
            if (entry.getKey().equals(ItemClientUtil.getResourceKey(item)))
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
            this.sortByResource();
        }

        this.deletedEntries.clear();
        this.getMinecraft().setScreen(this.parentScreen);
    }

    /**
     * Save an item to this list's entries map. No changes are saved until confirmed by the user.
     * @param item The item to save.
     */
    @Override
    public void addItem(Item item)
    {
        this.configMap.put(ItemClientUtil.getResourceKey(item), this.resetValue);
        this.sortByResource();
    }

    /**
     * Sorts two localized item names alphabetically.
     * @param first The first entry.
     * @param second The second entry.
     * @return A negative integer, zero, or a positive integer ignoring case considerations.
     */
    private int getLocalizedItem(Map.Entry<String, V> first, Map.Entry<String, V> second)
    {
        String firstItem = ItemClientUtil.getLocalizedItem(first.getKey());
        String secondItem = ItemClientUtil.getLocalizedItem(second.getKey());

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
            if (!ItemClientUtil.isValidEntry(entry.getKey()))
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
     * Get the rows associated with a list's saved items. If no items are saved, then a row with a message stating
     * that no items are saved will be shown.
     *
     * @return A list of config row list rows.
     */
    @Override
    protected ArrayList<ConfigRowList.Row> getSavedItems()
    {
        ArrayList<ConfigRowList.Row> rows = new ArrayList<>();
        String query = this.getSearchBox().getValue();

        if (query.isEmpty())
        {
            for (Map.Entry<String, V> entry : this.configMap.entrySet())
                rows.add(this.getConfigRowList().rowFromEntry(entry, this.resetValue));
        }
        else
        {
            NonNullList<ItemStack> allItems = NonNullList.create();

            for (Map.Entry<String, V> entry : this.configMap.entrySet())
            {
                if (ItemClientUtil.isValidEntry(entry.getKey()))
                    allItems.add(ItemClientUtil.getItemStack(entry.getKey()));
            }

            this.addSearchedItems(allItems);

            for (Map.Entry<String, V> entry : this.configMap.entrySet())
            {
                for (ItemStack itemStack : allItems)
                {
                    if (ItemClientUtil.getResourceKey(itemStack.getItem()).equals(entry.getKey()))
                    {
                        rows.add(this.getConfigRowList().rowFromEntry(entry, this.resetValue));
                        break;
                    }
                }
            }
        }

        if (rows.size() == 0)
        {
            Component translate = Component.translatable(LangUtil.Gui.LIST_NOTHING_SAVED);
            Component text = Component.literal(ChatFormatting.RED + translate.getString());

            rows.addAll(new TextGroup(text, TextAlign.CENTER).generate());
        }

        return rows;
    }
}
