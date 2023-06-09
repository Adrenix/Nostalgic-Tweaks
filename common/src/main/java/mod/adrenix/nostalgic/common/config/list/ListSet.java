package mod.adrenix.nostalgic.common.config.list;

import mod.adrenix.nostalgic.common.config.tweak.Tweak;
import mod.adrenix.nostalgic.util.common.ItemCommonUtil;
import net.minecraft.world.item.Item;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class defines the set structure for tweak sets and manual sets.
 * Some tweaks may need different setups depending on the situation.
 */

public class ListSet extends AbstractList
{
    /* Fields */

    private final Set<String> serverSet;
    private final Set<String> configSet;
    private final Set<String> defaultSet;

    /* Constructors */

    /**
     * Create a new list set instance.
     * @param tweak The tweak associated with this set based list.
     * @param listId The list identifier for this set based list.
     * @param listInclude A list include enumeration that dictates what items are allowed.
     * @param defaultSet A set of default values, if one is to be included.
     * @param configSet The config set that is stored on disk.
     * @param disabledDefaults A list of disabled default resource keys that was created by the user.
     */
    public ListSet
    (
        Tweak tweak,
        ListId listId,
        ListInclude listInclude,
        Set<String> defaultSet,
        Set<String> configSet,
        Set<String> disabledDefaults
    )
    {
        super(tweak, listId, listInclude, disabledDefaults);

        this.serverSet = new LinkedHashSet<>();
        this.configSet = configSet;
        this.defaultSet = defaultSet;
    }

    /**
     * Create a new list set instance with only a config set saved on disk.
     * @param tweak The tweak associated with this set based list.
     * @param listId The list identifier for this set based list.
     * @param listInclude A list include enumeration that dictates what items are allowed.
     * @param configSet The config set that is stored on disk.
     */
    public ListSet(Tweak tweak, ListId listId, ListInclude listInclude, Set<String> configSet)
    {
        this(tweak, listId, listInclude, new LinkedHashSet<>(), configSet, new LinkedHashSet<>());
    }

    /* Getters */

    /**
     * The config set is what's currently stored on disk. The pre-programmed default config set can be restored within
     * an abstract list screen.
     *
     * @return The configuration set that is kept on disk.
     */
    public Set<String> getConfigSet() { return this.isServerNeeded() ? this.serverSet : this.configSet; }

    /**
     * The default config set is pre-programmed. This set is used by abstract list screens to restore a list set back
     * to its default state.
     *
     * @return A pre-programmed default configuration set.
     */
    public Set<String> getDefaultSet() { return this.defaultSet; }

    /* Methods */

    /**
     * Gets an item resource key from either the saved config set or the default set (if the default key isn't disabled).
     * @param item The item to get a comparable resource key from.
     * @return An item resource key that matches the given item's resource key location.
     */
    public String getKeyFromItem(Item item)
    {
        String resourceKey = ItemCommonUtil.getResourceKey(item);

        for (String key : this.getConfigSet())
        {
            if (key.equals(resourceKey))
                return key;
        }

        for (String key : this.getDefaultSet())
        {
            if (key.equals(resourceKey) && !this.isDefaultDisabled(key))
                return key;
        }

        return null;
    }

    /**
     * Checks if the given item is within the saved set or the default set (if the default key isn't disabled).
     * @param item The item to get a comparable resource key from.
     * @return Whether the given item should be considered in this list.
     */
    public boolean isItemInList(Item item) { return this.getKeyFromItem(item) != null; }
}
