package mod.adrenix.nostalgic.mixin.util.candy;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.data.CacheValue;
import mod.adrenix.nostalgic.util.common.world.ItemUtil;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.BitSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This utility is used by both the client and server. For safety, keep client-only code out of this class.
 */
public abstract class ChestMixinHelper
{
    /**
     * Caches valid old chest blocks until the config is changed. This cache is backed by a concurrent hash map to
     * ensure the cache can be accessed from other threads. The bit set has the zeroth index being whether the block is
     * an old chest and the first index being whether the chest block is translucent.
     */
    private static final ConcurrentHashMap<Block, BitSet> BLOCK_CACHE = new ConcurrentHashMap<>();

    /**
     * Caches the value stored by the chest light blocking tweak so a decision can be made if all the block cache needs
     * cleared and recalculated.
     */
    private static final CacheValue<Boolean> CHEST_RELIGHT_CACHE = CacheValue.create(CandyTweak.CHEST_LIGHT_BLOCK::get);

    /**
     * Instructions to perform after the config has been saved.
     */
    public static void runAfterSave()
    {
        if (CHEST_RELIGHT_CACHE.isExpired())
        {
            BLOCK_CACHE.clear();
            CHEST_RELIGHT_CACHE.update();
        }
    }

    /**
     * Checks if the given block should be considered an "old" chest.
     *
     * @param block The {@link Block} to check.
     * @return Whether this block should be considered an old chest.
     */
    public static boolean isOld(Block block)
    {
        BitSet cache = BLOCK_CACHE.get(block);

        if (cache != null)
            return cache.get(0);

        BitSet bitSet = new BitSet(2);
        boolean isOldChest = CandyTweak.OLD_CHEST.get() && block.getClass().equals(ChestBlock.class);
        boolean isOldEnder = CandyTweak.OLD_ENDER_CHEST.get() && block.getClass().equals(EnderChestBlock.class);
        boolean isOldTrap = CandyTweak.OLD_TRAPPED_CHEST.get() && block.getClass().equals(TrappedChestBlock.class);
        boolean isOldMod = CandyTweak.OLD_MOD_CHESTS.get().containsKey(ItemUtil.getResourceKey(block));
        boolean isOld = isOldChest || isOldEnder || isOldTrap || isOldMod;

        bitSet.set(0, isOld);
        bitSet.set(1, CandyTweak.TRANSLUCENT_CHESTS.get().containsKey(ItemUtil.getResourceKey(block)));
        BLOCK_CACHE.put(block, bitSet);

        return isOld;
    }

    /**
     * Checks if the given block state should be considered an "old" chest.
     *
     * @param blockState The {@link BlockState} to check.
     * @return Whether this block state should be considered an old chest.
     */
    public static boolean isOld(BlockState blockState)
    {
        return isOld(blockState.getBlock());
    }

    /**
     * Check if the given block is a translucent chest. This uses a concurrent cache which will be faster than querying
     * the translucent chest tweak.
     *
     * @param block The {@link Block} to check.
     * @return Whether the given block is a translucent chest.
     */
    public static boolean isTranslucent(Block block)
    {
        BitSet cache = BLOCK_CACHE.get(block);

        if (cache != null)
            return cache.get(0) && cache.get(1);
        else
            isOld(block);

        BitSet bitSet = BLOCK_CACHE.get(block);

        return bitSet.get(0) && bitSet.get(1);
    }

    /**
     * Check if the given block state is a translucent chest. This uses a concurrent cache which will be faster than
     * querying the translucent chest tweak.
     *
     * @param blockState The {@link BlockState} to check.
     * @return Whether the given block state is a translucent chest.
     */
    public static boolean isTranslucent(BlockState blockState)
    {
        return isTranslucent(blockState.getBlock());
    }
}
