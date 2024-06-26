package mod.adrenix.nostalgic.mixin.util.gameplay.combat;

import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

/**
 * This utility class is used by both the client and server.
 */
public abstract class SwordBlockMixinHelper
{
    /**
     * Check if the player is sword blocking.
     *
     * @param player The {@link Player} instance.
     * @return Whether the player is sword blocking.
     */
    public static boolean isBlocking(Player player)
    {
        return player.isUsingItem() && canBlock(player);
    }

    /**
     * Check if the player should sword block when a shield is being used in the off-hand.
     *
     * @param player The {@link Player} instance.
     * @return Whether the player should sword block when shielding in the off-hand.
     */
    public static boolean shouldBlockOnShield(Player player)
    {
        if (!player.isUsingItem())
            return false;

        ItemStack mainItemStack = player.getMainHandItem();
        ItemStack offItemStack = player.getOffhandItem();

        if (GameplayTweak.BLOCK_WITH_SWORD_ON_SHIELD.get())
            return offItemStack.getItem() instanceof ShieldItem && mainItemStack.is(ItemTags.SWORDS);

        return false;
    }

    /**
     * Check if the player can sword block.
     *
     * @param player The {@link Player} instance.
     * @return Whether the player can sword block.
     */
    public static boolean canBlock(Player player)
    {
        if (!player.getOffhandItem().isEmpty())
            return false;

        return GameplayTweak.OLD_SWORD_BLOCKING.get() && player.getMainHandItem().is(ItemTags.SWORDS);
    }
}
