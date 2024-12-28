package mod.adrenix.nostalgic.helper.gameplay;

import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;

/**
 * This utility class is used by both the client and server.
 */
public abstract class InteractionHelper
{
    /**
     * Check if the given item should not be capable of being used.
     *
     * @param item The {@link Item} being used.
     * @return Whether the item trying to be used should not be usable.
     */
    public static boolean shouldNotUseItem(Item item)
    {
        if (GameplayTweak.DISABLE_SHOVEL_PATHING.get() && item instanceof ShovelItem)
            return true;

        if (GameplayTweak.DISABLE_AXE_STRIPPING.get() && item instanceof AxeItem)
            return true;

        return false;
    }
}
