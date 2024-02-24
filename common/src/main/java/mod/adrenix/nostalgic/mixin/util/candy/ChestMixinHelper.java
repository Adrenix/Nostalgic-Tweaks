package mod.adrenix.nostalgic.mixin.util.candy;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.world.ItemCommonUtil;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This utility is used both the client and server. For safety, keep client-only code out of this class.
 */
public abstract class ChestMixinHelper
{
    /**
     * Checks if the given block should be considered an "old" chest.
     *
     * @param block The {@link Block} to check.
     * @return Whether this block should be considered an old chest.
     */
    public static boolean isOld(Block block)
    {
        boolean isOldChest = CandyTweak.OLD_CHEST.get() && block.getClass().equals(ChestBlock.class);
        boolean isOldEnder = CandyTweak.OLD_ENDER_CHEST.get() && block.getClass().equals(EnderChestBlock.class);
        boolean isOldTrap = CandyTweak.OLD_TRAPPED_CHEST.get() && block.getClass().equals(TrappedChestBlock.class);
        boolean isOldMod = CandyTweak.OLD_MOD_CHESTS.get().containsKey(ItemCommonUtil.getResourceKey(block));

        return isOldChest || isOldEnder || isOldTrap || isOldMod;
    }

    /**
     * Checks if the given block state should be considered an "old" chest.
     *
     * @param state The {@link BlockState} to check.
     * @return Whether this block state should be considered an old chest.
     */
    public static boolean isOld(BlockState state)
    {
        return isOld(state.getBlock());
    }
}
