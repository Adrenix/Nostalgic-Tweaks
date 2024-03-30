package mod.adrenix.nostalgic.mixin.util.candy;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.world.ItemUtil;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This utility is used both the client and server. For safety, keep client-only code out of this class.
 */
public abstract class ChestMixinHelper
{
    /**
     * Caches valid old chest blocks until the config is changed. This cache is backed by a concurrent hash map to
     * ensure the cache can be accessed from other threads.
     */
    private static final ConcurrentHashMap<Block, Boolean> BLOCK_CACHE = new ConcurrentHashMap<>();

    /**
     * Instructions to perform after the config has been saved.
     */
    public static void runAfterSave()
    {
        BLOCK_CACHE.clear();
    }

    /**
     * Checks if the given block should be considered an "old" chest.
     *
     * @param block The {@link Block} to check.
     * @return Whether this block should be considered an old chest.
     */
    public static boolean isOld(Block block)
    {
        Boolean isCached = BLOCK_CACHE.get(block);

        if (isCached != null)
            return isCached;

        boolean isOldChest = CandyTweak.OLD_CHEST.get() && block.getClass().equals(ChestBlock.class);
        boolean isOldEnder = CandyTweak.OLD_ENDER_CHEST.get() && block.getClass().equals(EnderChestBlock.class);
        boolean isOldTrap = CandyTweak.OLD_TRAPPED_CHEST.get() && block.getClass().equals(TrappedChestBlock.class);
        boolean isOldMod = CandyTweak.OLD_MOD_CHESTS.get().containsKey(ItemUtil.getResourceKey(block));
        boolean isOld = isOldChest || isOldEnder || isOldTrap || isOldMod;

        BLOCK_CACHE.put(block, isOld);

        return isOld;
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
