package mod.adrenix.nostalgic.client.config.gui.screen.list;

import com.google.common.collect.Sets;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.list.row.ConfigRowKey;
import mod.adrenix.nostalgic.common.config.list.ListSet;
import mod.adrenix.nostalgic.util.common.ItemCommonUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Set;

/**
 * The list set screen is responsible for managing lists that can only contain a set of keys. The keys associated with
 * these lists must point to a resource location. For example, a diamond pickaxe entry should have a key that looks
 * like: <code>minecraft:diamond_pickaxe</code>.
 */

public class ListSetScreen extends AbstractListScreen
{
    /* Fields */

    private final ArrayList<String> deletedKeys = new ArrayList<>();
    private final Set<String> defaultSet;
    private final Set<String> configSet;
    private final Set<String> undoSet;

    /* Constructors */

    /**
     * Create a new list screen that controls a set.
     * @param title The list screen title.
     * @param listSet A list set instance.
     */
    public ListSetScreen(Component title, ListSet listSet)
    {
        super(title, listSet);

        this.defaultSet = listSet.getDefaultSet();
        this.configSet = listSet.getConfigSet();
        this.undoSet = Sets.newHashSet(this.configSet);
    }

    /* Getters */

    /**
     * Get this screen's current list of deleted item resource keys.
     * @return An array list of deleted item resource keys.
     */
    public ArrayList<String> getDeletedKeys() { return this.deletedKeys; }

    /* Methods */

    /**
     * Delete an entry from the map. The changes are not saved until confirmed by the user.
     * @param resourceKey The key to delete.
     */
    public void delete(String resourceKey) { this.deletedKeys.add(resourceKey); }

    /**
     * Remove a resource key from the deleted keys list. The changes are not saved until confirmed by the user.
     * @param resourceKey The item resource key to remove from the deleted keys array list.
     */
    public void undo(String resourceKey) { this.deletedKeys.remove(resourceKey); }

    /**
     * Checks if the given item resource key is contained within the deleted keys array list.
     * @param resourceKey The resource key to check.
     * @return Whether the given resource key is deleted.
     */
    public boolean isKeyDeleted(String resourceKey) { return this.deletedKeys.contains(resourceKey); }

    /**
     * Checks if the given item is in the undo set. If not, then it was added by the user this session.
     * @param item The item to check to see if it is added to the list.
     * @return Whether the given item is in the list's undo set.
     */
    @Override
    public boolean isItemAdded(Item item) { return !this.undoSet.contains(ItemCommonUtil.getResourceKey(item)); }

    /**
     * Save an item to this list's set. No changes are saved until confirmed by the user.
     * @param item The item to save.
     */
    @Override
    public void addItem(Item item)
    {
        if (this.isItemEligible(item))
            this.configSet.add(ItemCommonUtil.getResourceKey(item));
    }

    /**
     * Delete an entry from the map using an item. The changes are not saved until confirmed by the user.
     * @param item The item to remove.
     */
    @Override
    public void deleteItem(Item item)
    {
        for (String key : this.configSet)
        {
            if (key.equals(ItemCommonUtil.getResourceKey(item)))
            {
                this.delete(key);
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
        for (String key : this.configSet)
        {
            if (!this.deletedKeys.contains(key))
                this.delete(key);
        }
    }

    /**
     * Checks if the given item has been marked for deletion.
     * @param item The item to check.
     * @return Whether there is an item entry matching the given item in the deleted set list.
     */
    @Override
    public boolean isItemDeleted(Item item)
    {
        for (String key : this.deletedKeys)
        {
            if (ItemCommonUtil.getResourceKey(item).equals(key))
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
        for (String key : this.configSet)
        {
            if (key.equals(ItemCommonUtil.getResourceKey(item)))
                return true;
        }

        return false;
    }

    /**
     * A simple return of the provided resource key. Shortcut method for the abstract get searched items method.
     * @param resourceKey An item resource key.
     * @return The given item resource key.
     */
    private String getResourceKey(String resourceKey) { return resourceKey; }

    /**
     * Adds a config row from a set item resource key to the given array list of rows.
     * @param rows The array list of config row list rows.
     * @param resourceKey An item resource key.
     */
    private void addSavedRow(ArrayList<ConfigRowList.Row> rows, String resourceKey)
    {
        rows.add(new ConfigRowKey.SavedRow(resourceKey).generate());
    }

    /**
     * Adds a default entry config row from a set item resource key to the given array list of rows.
     * @param rows The array list of config row list rows.
     * @param resourceKey An item resource key.
     */
    private void addDefaultRow(ArrayList<ConfigRowList.Row> rows, String resourceKey)
    {
        rows.add(new ConfigRowKey.DefaultRow(resourceKey).generate());
    }

    /**
     * Get the rows associated with a list's saved items. If no items are saved, then a row with a message stating
     * that no items are saved will be shown.
     *
     * @return A list of config row list rows.
     */
    @Override
    protected ArrayList<ConfigRowList.Row> getSavedRows()
    {
        String langKey = LangUtil.Gui.LIST_NO_SAVED_ITEMS;

        return this.getSearchedItems(langKey, this.configSet, this::getResourceKey, this::addSavedRow);
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

        return this.getSearchedItems(langKey, this.defaultSet, this::getResourceKey, this::addDefaultRow);
    }

    /**
     * Disables all default entries by adding them to the disabled default entries set.
     */
    @Override
    public void disableAllDefaults() { this.disabledDefaults.addAll(this.defaultSet); }

    /**
     * Enables all default entries by removing them from the disabled default entries set.
     */
    @Override
    public void enableAllDefaults() { this.disabledDefaults.removeAll(this.defaultSet); }

    /**
     * @return The number of default entries associated with this list.
     */
    @Override
    protected int getDefaultCount() { return this.defaultSet.size(); }

    /**
     * Checks if the list has been changed and can be saved by the user.
     * @return Whether changes are savable.
     */
    @Override
    protected boolean isListSavable()
    {
        return this.undoSet.size() != this.configSet.size() ||
            this.undoDisabledDefaults.size() != this.disabledDefaults.size() ||
            this.deletedKeys.size() > 0
        ;
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
            for (String entry : this.deletedKeys)
                this.configSet.remove(entry);
        }
        else
        {
            this.configSet.clear();
            this.configSet.addAll(this.undoSet);

            this.disabledDefaults.clear();
            this.disabledDefaults.addAll(this.undoDisabledDefaults);
        }

        this.deletedKeys.clear();
        this.getMinecraft().setScreen(this.parentScreen);
    }
}
