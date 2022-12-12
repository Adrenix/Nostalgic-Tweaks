package mod.adrenix.nostalgic.common.config.list;

import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.reflect.TweakCommonCache;
import mod.adrenix.nostalgic.common.config.tweak.CandyTweak;
import mod.adrenix.nostalgic.common.config.tweak.SwingTweak;
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
     * Get a list map instance that is associated with a tweak, if it exists.
     * @param tweak The tweak to get list data from.
     * @return A list map instance, or null if no list is associated with the tweak.
     */
    public static ListMap<?> getMapFromTweak(TweakCommonCache tweak)
    {
        if (tweak.getList() == null)
            return null;

        return switch (tweak.getList().id())
        {
            case CUSTOM_SWING -> CUSTOM_SWING;
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

        return switch (tweak.getList().id())
        {
            case IGNORED_ITEM_HOLDING -> IGNORED_ITEM_HOLDING;
            default -> null;
        };
    }

    /* Sets */

    /**
     * The ignored old item holding set contains a set of items that should be ignored by the old item holding tweak.
     * The user can add more items to ignore if they need.
     */
    public static final ListSet IGNORED_ITEM_HOLDING = new ListSet
    (
        CandyTweak.IGNORED_ITEM_HOLDING,
        ListId.IGNORED_ITEM_HOLDING,
        ListInclude.NO_BLOCKS,
        ItemCommonUtil.getKeysFromItems(Items.CROSSBOW),
        ClientConfigCache.getCandy().ignoredHoldingItems,
        ClientConfigCache.getCandy().disabledIgnoredHoldingItems
    );

    /* Maps */

    /**
     * The custom swing list map contains a map of swing speeds that range from {@link DefaultConfig.Swing#MIN_SPEED} to
     * {@link DefaultConfig.Swing#MAX_SPEED}.
     */
    public static final ListMap<Integer> CUSTOM_SWING = new ListMap<>
    (
        SwingTweak.CUSTOM_SWING,
        ListId.CUSTOM_SWING,
        ListInclude.ALL,
        DefaultConfig.Swing.OLD_SPEED,
        ClientConfigCache.getRoot().customSwingSpeeds
    );
}
