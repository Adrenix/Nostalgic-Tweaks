package mod.adrenix.nostalgic.common.config.list;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.DefaultConfig;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This class is responsible for checking the lists within a config. List validation can be performed by both the client
 * and the server. Although list manipulation should be done through the mod's configuration user interface, it is
 * important to validate the lists anyway when they are read from disk.
 */

public abstract class ValidateList
{
    /**
     * Runs validation instructions based on the provided list identifier. No errors will be thrown; instead, list data
     * will be cleaned or reset.
     *
     * @param id A list identifier.
     * @param list A list object.
     * @return Whether validation was successful.
     */
    @SuppressWarnings("unchecked") // List types are associated with their list identifier
    public static boolean scan(ListId id, Object list)
    {
        return switch (id)
        {
            case LEFT_CLICK_SPEEDS, RIGHT_CLICK_SPEEDS -> customSwings((Map<String, Integer>) list, id);
            case FULL_BLOCK_OUTLINE -> customOutlines((Set<String>) list);
            case CUSTOM_FOOD_HEALTH -> customFoodHealth((Map<String, Integer>) list);
            case CUSTOM_FOOD_STACKING -> customFoodStacking((Map<String, Integer>) list);
            case CUSTOM_ITEM_STACKING -> customItemStacking((Map<String, Integer>) list);
            case IGNORED_ITEM_HOLDING -> ignoredHoldings((Set<String>) list);
        };
    }

    /**
     * Scan an integer map to ensure entries are within bounds and still exist.
     * @param map The map to scan.
     * @param min The minimum allowed value.
     * @param max The maximum allowed value.
     * @param reset The value to reset to when out-of-bounds.
     * @param onInvalid A bi-consumer that accepts a key and an invalid value.
     * @param onUpdated A bi-consumer that accepts a key and a reset value.
     * @param onFinish A consumer that accepts a map size.
     * @return Whether the list validation is successful.
     */
    private static boolean scanIntegerMap
    (
        Map<String, Integer> map,
        int min,
        int max,
        int reset,
        BiConsumer<String, Integer> onInvalid,
        BiConsumer<String, Integer> onUpdated,
        Consumer<Integer> onFinish
    )
    {
        boolean isValid = true;

        for (Map.Entry<String, Integer> entry : map.entrySet())
        {
            String key = entry.getKey();
            int value = entry.getValue();

            if (value < min || value > max)
            {
                // Log invalid entry notice
                onInvalid.accept(key, value);
                onUpdated.accept(key, reset);

                // Update entry to reset value
                entry.setValue(reset);

                isValid = false;
            }
        }

        // Log number of registered entries
        onFinish.accept(map.size());

        return isValid;
    }

    /* List Validators */

    /**
     * Validate the contents of the custom swing speed map.
     * @param speedMap A custom swing speed map.
     * @param listId A list identifier to determine whether this is left/right list.
     * @return Whether validation is successful.
     */
    private static boolean customSwings(Map<String, Integer> speedMap, ListId listId)
    {
        String list = switch (listId)
        {
            case LEFT_CLICK_SPEEDS -> "left-click";
            case RIGHT_CLICK_SPEEDS -> "right-click";
            default -> "N/A";
        };

        return scanIntegerMap
        (
            speedMap,
            DefaultConfig.Swing.MIN_SPEED,
            DefaultConfig.Swing.MAX_SPEED,
            DefaultConfig.Swing.OLD_SPEED,
            (key, value) -> NostalgicTweaks.LOGGER.warn("%s has an invalid %s swing speed: %s", key, list, value),
            (key, reset) -> NostalgicTweaks.LOGGER.warn("%s has been updated to speed: %s", key, reset),
            (size) -> NostalgicTweaks.LOGGER.info("Validated %d %s customized swing speeds", size, list)
        );
    }

    /**
     * Validate the contents of the custom food health map.
     * @param healthMap A custom food health map.
     * @return Whether validation is successful.
     */
    private static boolean customFoodHealth(Map<String, Integer> healthMap)
    {
        return scanIntegerMap
        (
            healthMap,
            DefaultConfig.Gameplay.HEALTH_MIN,
            DefaultConfig.Gameplay.HEALTH_MAX,
            DefaultConfig.Gameplay.HEALTH_RESET,
            (key, value) -> NostalgicTweaks.LOGGER.warn("%s has an invalid health value: %s", key, value),
            (key, reset) -> NostalgicTweaks.LOGGER.warn("%s has been updated to a value of: %s", key, reset),
            (size) -> NostalgicTweaks.LOGGER.info("Validated %d customized food health values", size)
        );
    }

    /**
     * Validate the contents of the custom food stacking map.
     * @param stackMap A custom food stack map.
     * @return Whether validation is successful.
     */
    private static boolean customFoodStacking(Map<String, Integer> stackMap)
    {
        return scanIntegerMap
        (
            stackMap,
            DefaultConfig.Gameplay.ITEM_STACK_MIN,
            DefaultConfig.Gameplay.ITEM_STACK_MAX,
            DefaultConfig.Gameplay.FOOD_STACK_RESET,
            (key, value) -> NostalgicTweaks.LOGGER.warn("%s has an invalid food stacking size: %s", key, value),
            (key, reset) -> NostalgicTweaks.LOGGER.warn("%s has been updated to a stack size of: %s", key, reset),
            (size) -> NostalgicTweaks.LOGGER.info("Validated %d customized maximum food item stack sizes", size)
        );
    }

    /**
     * Validate the contents of the custom item stacking map.
     * @param itemMap A custom item stack map.
     * @return Whether validation is successful.
     */
    private static boolean customItemStacking(Map<String, Integer> itemMap)
    {
        return scanIntegerMap
        (
            itemMap,
            DefaultConfig.Gameplay.ITEM_STACK_MIN,
            DefaultConfig.Gameplay.ITEM_STACK_MAX,
            DefaultConfig.Gameplay.ITEM_STACK_RESET,
            (key, value) -> NostalgicTweaks.LOGGER.warn("%s has an invalid item stacking size: %s", key, value),
            (key, reset) -> NostalgicTweaks.LOGGER.warn("%s has been updated to a stack size of: %s", key, reset),
            (size) -> NostalgicTweaks.LOGGER.info("Validated %d customized item stack sizes", size)
        );
    }

    /**
     * Validate the contents of the old holding ignored items list.
     * @param list A set of item resource key locations.
     * @return Whether validation is successful.
     */
    private static boolean ignoredHoldings(Set<String> list)
    {
        // Log number of registered entries
        NostalgicTweaks.LOGGER.info("Validated %d ignored old item holding entries", list.size());

        return true;
    }

    /**
     * Validate the contents of the custom block outlines list.
     * @param list A set of block item resource key locations.
     * @return Whether validation is successful.
     */
    private static boolean customOutlines(Set<String> list)
    {
        // Log number of registered entries
        NostalgicTweaks.LOGGER.info("Validated %d customized block outline entries", list.size());

        return true;
    }
}
