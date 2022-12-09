package mod.adrenix.nostalgic.client.config.gui.screen.list;

import com.google.common.collect.Sets;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.group.TextGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextAlign;
import mod.adrenix.nostalgic.common.config.list.ListId;
import mod.adrenix.nostalgic.util.client.ItemClientUtil;
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

    private final ArrayList<String> deletedEntries = new ArrayList<>();
    private final Set<String> configSet;
    private final Set<String> undoSet;

    /* Constructors */

    /**
     * Create a new list screen that controls a set.
     * @param parentScreen The parent configuration screen.
     * @param title The list screen title.
     * @param configSet A set from the client config.
     * @param listId A list screen identifier.
     * @param listFilter A list filter that controls what items appear in the all items list.
     * @param flags A var args list of list flags for this screen.
     */
    public ListSetScreen
    (
        ConfigScreen parentScreen,
        Component title,
        Set<String> configSet,
        ListId listId,
        ListFilter listFilter,
        ListFlags ...flags
    )
    {
        super(parentScreen, title, listId, listFilter, flags);

        this.configSet = configSet;
        this.undoSet = Sets.newHashSet(configSet);
    }

    /**
     * Create a new list screen (with no filters) that controls a set.
     * @param parentScreen The parent configuration screen.
     * @param title The list screen title.
     * @param configSet A set from the client config.
     * @param listId A list screen identifier.
     * @param flags A var args list of list flags for this screen.
     */
    public ListSetScreen
    (
        ConfigScreen parentScreen,
        Component title,
        Set<String> configSet,
        ListId listId,
        ListFlags ...flags
    )
    {
        this(parentScreen, title, configSet, listId, ListFilter.ALL, flags);
    }

    /* Methods */

    /**
     * Delete an entry from the map. The changes are not saved until confirmed by the user.
     * @param resourceKey The key to delete.
     */
    public void delete(String resourceKey) { this.deletedEntries.add(resourceKey); }

    /**
     * Save an item to this list's set. No changes are saved until confirmed by the user.
     * @param item The item to save.
     */
    @Override
    public void addItem(Item item) { this.configSet.add(ItemClientUtil.getResourceKey(item)); }

    /**
     * Delete an entry from the map using an item. The changes are not saved until confirmed by the user.
     * @param item The item to remove.
     */
    @Override
    public void deleteItem(Item item)
    {
        for (String entry : this.configSet)
        {
            if (entry.equals(ItemClientUtil.getResourceKey(item)))
            {
                this.delete(entry);
                break;
            }
        }
    }

    /**
     * Checks if the given item is in the undo set. If not, then it was added by the user this session.
     * @param item The item to check to see if it is added to the list.
     * @return Whether the given item is in the list's undo set.
     */
    @Override
    public boolean isItemAdded(Item item) { return !this.undoSet.contains(ItemClientUtil.getResourceKey(item)); }

    /**
     * Clears all saved entries from this list's config map.
     */
    @Override
    public void clearAllSaved()
    {
        for (String entry : this.configSet)
        {
            if (!this.deletedEntries.contains(entry))
                this.delete(entry);
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
        for (String entry : this.deletedEntries)
        {
            if (ItemClientUtil.getResourceKey(item).equals(entry))
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
        for (String entry : this.configSet)
        {
            if (entry.equals(ItemClientUtil.getResourceKey(item)))
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
        return this.undoSet.size() != this.configSet.size() || this.deletedEntries.size() > 0;
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
        return new ArrayList<>(new TextGroup(Component.translatable(LangUtil.Gui.LIST_NOTHING_SAVED), TextAlign.CENTER).generate());
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
            for (String entry : this.deletedEntries)
                this.configSet.remove(entry);
        }
        else
        {
            this.configSet.clear();
            this.configSet.addAll(this.undoSet);
        }

        this.deletedEntries.clear();
        this.getMinecraft().setScreen(this.parentScreen);
    }
}
