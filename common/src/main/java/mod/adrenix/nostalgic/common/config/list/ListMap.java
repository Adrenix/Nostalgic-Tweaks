package mod.adrenix.nostalgic.common.config.list;

import mod.adrenix.nostalgic.common.config.tweak.Tweak;
import mod.adrenix.nostalgic.util.common.ItemCommonUtil;
import net.minecraft.world.item.Item;

import java.util.*;

/**
 * This class defines the map structure for tweak maps and manual maps.
 * Some tweaks may need different setups depending on the situation.
 */

public class ListMap<V> extends AbstractList
{
    /* Fields */

    private final Map<String, V> configMap;
    private final Map<String, V> defaultMap;
    private final V resetValue;

    /* Constructors */

    /**
     * Create a new list map instance.
     * @param tweak The tweak associated with this map based list.
     * @param listId The list identifier for this map based list.
     * @param listInclude A list include enumeration that dictates what items are allowed.
     * @param resetValue The value to reset to when the reset button is clicked.
     * @param defaultMap A map of default values, if one is to be included.
     * @param configMap The config map that is stored on disk.
     * @param disabledDefaults A list of disabled default values that was set by the user.
     */
    public ListMap
    (
        Tweak tweak,
        ListId listId,
        ListInclude listInclude,
        V resetValue,
        Map<String, V> defaultMap,
        Map<String, V> configMap,
        Set<String> disabledDefaults
    )
    {
        super(tweak, listId, listInclude, disabledDefaults);

        this.configMap = configMap;
        this.defaultMap = defaultMap;
        this.resetValue = resetValue;
    }

    /**
     * Create a new list map instance with only a config map saved on disk.
     * @param tweak The tweak associated with this map based list.
     * @param listId The list identifier for this map based list.
     * @param listInclude A list include enumeration that dictates what items are allowed.
     * @param resetValue The value to reset to when the reset button is clicked.
     * @param configMap The config map that is stored on disk.
     */
    public ListMap(Tweak tweak, ListId listId, ListInclude listInclude, V resetValue, Map<String, V> configMap)
    {
        this(tweak, listId, listInclude, resetValue, new HashMap<>(), configMap, new HashSet<>());
    }

    /* Getters */

    /**
     * The reset value is used by the reset buttons that are included in list entry rows. When the reset button is
     * pressed, this value will be used to convert the current entry value.
     *
     * @return The reset value associated with this list map.
     */
    public V getResetValue() { return this.resetValue; }

    /**
     * The config map is what's currently stored on disk. The pre-programmed default config map can be restored within
     * an abstract list screen.
     *
     * @return The configuration map that is kept on disk.
     */
    public Map<String, V> getConfigMap() { return this.configMap; }

    /**
     * The default config map is pre-programmed. This map is used by abstract list screens to restore a list to its
     * default state.
     *
     * @return A pre-programmed default configuration map.
     */
    public Map<String, V> getDefaultMap() { return this.defaultMap; }

    /* Methods */

    /**
     * Gets an entry from either the saved entries map or the default map (if the default isn't disabled).
     * @param item The item to get a comparable resource key from.
     * @return An entry that matches the given item's resource key location.
     */
    public Map.Entry<String, V> getEntryFromItem(Item item)
    {
        String resourceKey = ItemCommonUtil.getResourceKey(item);
        List<Map.Entry<String, V>> saved = new ArrayList<>(this.configMap.entrySet());
        List<Map.Entry<String, V>> defaults = new ArrayList<>(this.defaultMap.entrySet());

        for (Map.Entry<String, V> entry : saved)
        {
            if (entry.getKey().equals(resourceKey))
                return entry;
        }

        for (Map.Entry<String, V> entry : defaults)
        {
            if (entry.getKey().equals(resourceKey) && !this.isDefaultDisabled(entry.getKey()))
                return entry;
        }

        return null;
    }
}
