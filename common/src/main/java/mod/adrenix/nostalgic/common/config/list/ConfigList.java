package mod.adrenix.nostalgic.common.config.list;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.reflect.TweakCommonCache;
import mod.adrenix.nostalgic.common.config.tweak.CandyTweak;
import mod.adrenix.nostalgic.common.config.tweak.GameplayTweak;
import mod.adrenix.nostalgic.common.config.tweak.SwingTweak;
import mod.adrenix.nostalgic.server.config.ServerConfig;
import mod.adrenix.nostalgic.server.config.ServerConfigCache;
import mod.adrenix.nostalgic.util.common.ItemCommonUtil;
import net.minecraft.world.item.Items;

/**
 * This utility class is responsible for keeping and maintaining manual and tweak associated lists. The config will
 * store a map or an array while the user interface will interact with a manual or auto-generated {@link ListMap}.
 */

public abstract class ConfigList
{
    /* List Getters */

    /**
     * Get a list map instance from the given list identifier.
     * @param listId The list identifier.
     * @return A list map instance.
     */
    public static ListMap<?> getMapFromId(ListId listId)
    {
        return switch (listId)
        {
            case CUSTOM_ITEM_STACKING -> CUSTOM_ITEM_STACKING;
            case CUSTOM_FOOD_STACKING -> CUSTOM_FOOD_STACKING;
            case CUSTOM_FOOD_HEALTH -> CUSTOM_FOOD_HEALTH;
            case RIGHT_CLICK_SPEEDS -> RIGHT_CLICK_SPEEDS;
            case LEFT_CLICK_SPEEDS -> LEFT_CLICK_SPEEDS;
            default -> null;
        };
    }

    /**
     * Get a list map instance that is associated with a tweak, if it exists.
     * @param tweak The tweak to get list data from.
     * @return A list map instance, or null if no list is associated with the tweak.
     */
    public static ListMap<?> getMapFromTweak(TweakCommonCache tweak)
    {
        if (tweak.getList() == null)
            return null;

        return getMapFromId(tweak.getList().id());
    }

    /**
     * Get a list set instance from the given list identifier.
     * @param listId A list identifier.
     * @return A list set instance.
     */
    public static ListSet getSetFromId(ListId listId)
    {
        return switch (listId)
        {
            case FULL_BLOCK_OUTLINE -> FULL_BLOCK_OUTLINE;
            case IGNORED_ITEM_HOLDING -> IGNORED_ITEM_HOLDING;
            default -> null;
        };
    }

    /**
     * Get a list set instance that is associated with a tweak, if it exists.
     * @param tweak The tweak to get list data from.
     * @return A list set instance, or null if no list is associated with the tweak.
     */
    public static ListSet getSetFromTweak(TweakCommonCache tweak)
    {
        if (tweak.getList() == null)
            return null;

        return getSetFromId(tweak.getList().id());
    }

    /* Config Caching & Sided Check */

    private static final boolean IS_CLIENT = NostalgicTweaks.isClient();

    private static final ClientConfig.Gameplay GAMEPLAY = ClientConfigCache.getGameplay();
    private static final ClientConfig.EyeCandy CANDY = ClientConfigCache.getCandy();
    private static final ClientConfig.Swing SWING = ClientConfigCache.getSwing();

    private static final ServerConfig.Gameplay SERVER_GAMEPLAY = ServerConfigCache.getGameplay();

    /* Sets */

    /**
     * The ignored old item holding set contains a set of items that should be ignored by the old item holding tweak.
     * The user can add more items to ignore if they need.
     */
    private static final ListSet IGNORED_ITEM_HOLDING = new ListSet
    (
        CandyTweak.IGNORED_ITEM_HOLDING,
        ListId.IGNORED_ITEM_HOLDING,
        ListInclude.NO_BLOCKS,
        ItemCommonUtil.getKeysFromItems(Items.CROSSBOW),
        CANDY.ignoredHoldingItems,
        CANDY.disabledIgnoredHoldingItems
    );

    /**
     * The full block outline set contains a set of block items that should have full-block hit box outlines.
     * The user can add custom block items to this list in addition to the other block outline tweaks.
     */
    private static final ListSet FULL_BLOCK_OUTLINE = new ListSet
    (
        CandyTweak.FULL_BLOCK_OUTLINE,
        ListId.FULL_BLOCK_OUTLINE,
        ListInclude.ONLY_BLOCKS,
        CANDY.oldBlockOutlines
    );

    /* Maps */

    /**
     * The custom left click swing speed list map contains a map of swing speeds that range from
     * {@link DefaultConfig.Swing#MIN_SPEED} to {@link DefaultConfig.Swing#MAX_SPEED}.
     */
    public static final ListMap<Integer> LEFT_CLICK_SPEEDS = new ListMap<>
    (
        SwingTweak.LEFT_CLICK_SPEEDS,
        ListId.LEFT_CLICK_SPEEDS,
        ListInclude.ALL,
        DefaultConfig.Swing.OLD_SPEED,
        SWING.leftClickSwingSpeeds
    );

    /**
     * The custom right click swing speed list map contains a map of swing speeds that range from
     * {@link DefaultConfig.Swing#MIN_SPEED} to {@link DefaultConfig.Swing#MAX_SPEED}.
     */
    public static final ListMap<Integer> RIGHT_CLICK_SPEEDS = new ListMap<>
    (
        SwingTweak.RIGHT_CLICK_SPEEDS,
        ListId.RIGHT_CLICK_SPEEDS,
        ListInclude.ALL,
        DefaultConfig.Swing.OLD_SPEED,
        SWING.rightClickSwingSpeeds
    );

    /**
     * The custom food health map contains a map of food items the amount of half-hearts to restore. Typical restore
     * values include 0, 1, 10, and 20.
     */
    private static final ListMap<Integer> CUSTOM_FOOD_HEALTH = new ListMap<>
    (
        GameplayTweak.CUSTOM_FOOD_HEALTH,
        ListId.CUSTOM_FOOD_HEALTH,
        ListInclude.ONLY_EDIBLE,
        DefaultConfig.Gameplay.HEALTH_RESET,
        DefaultConfig.Gameplay.DEFAULT_FOOD_HEALTH,
        IS_CLIENT ? GAMEPLAY.customFoodHealth : SERVER_GAMEPLAY.customFoodHealth,
        IS_CLIENT ? GAMEPLAY.disabledFoodHealth : SERVER_GAMEPLAY.disabledFoodHealth
    );

    /**
     * The custom food stacking map contains a map of food items and their maximum stack sizes. Most food items in beta
     * only had stack sizes of {@link DefaultConfig.Gameplay#FOOD_STACK_RESET}.
     */
    private static final ListMap<Integer> CUSTOM_FOOD_STACKING = new ListMap<>
    (
        GameplayTweak.CUSTOM_FOOD_STACKING,
        ListId.CUSTOM_FOOD_STACKING,
        ListInclude.ONLY_EDIBLE,
        DefaultConfig.Gameplay.FOOD_STACK_RESET,
        DefaultConfig.Gameplay.DEFAULT_OLD_FOOD_STACKING,
        IS_CLIENT ? GAMEPLAY.customFoodStacking : SERVER_GAMEPLAY.customFoodStacking,
        IS_CLIENT ? GAMEPLAY.disabledFoodStacking : SERVER_GAMEPLAY.disabledFoodStacking
    );

    /**
     * The custom item stacking map contains a map of items and their maximum allowed stack sizes. Some item stacks in
     * alpha/beta had different stack sizes such as doors.
     */
    private static final ListMap<Integer> CUSTOM_ITEM_STACKING = new ListMap<>
    (
        GameplayTweak.CUSTOM_ITEM_STACKING,
        ListId.CUSTOM_ITEM_STACKING,
        ListInclude.ALL,
        DefaultConfig.Gameplay.ITEM_STACK_RESET,
        IS_CLIENT ? GAMEPLAY.customItemStacking : SERVER_GAMEPLAY.customItemStacking
    );
}
