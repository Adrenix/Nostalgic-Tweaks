package mod.adrenix.nostalgic.util.common.world;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This utility is used by both client and server.
 */
public abstract class BlockUtil
{
    /**
     * Check if the given {@link BlockState} matches any of the given blocks.
     *
     * @param blockState The {@link BlockState} to check.
     * @param blocks     A varargs list of {@link Block} to compare to.
     * @return Whether the {@link BlockState} matched any of the given blocks.
     */
    public static boolean match(BlockState blockState, Block... blocks)
    {
        for (Block block : blocks)
        {
            if (blockState.is(block))
                return true;
        }

        return false;
    }

    /**
     * Check if the given block state matches a chest-like block.
     *
     * @param blockState The {@link BlockState} to check.
     * @return Whether the block is chest-like.
     */
    public static boolean isChestLike(BlockState blockState)
    {
        return isChestLike(blockState.getBlock());
    }

    /**
     * Check if the given block is chest-like.
     *
     * @param block The {@link Block} to check.
     * @return Whether the block is chest-like.
     */
    public static boolean isChestLike(Block block)
    {
        return block instanceof AbstractChestBlock<?>;
    }

    /**
     * Check if the given block state matches a water-like block. There are no block or block pos overload methods since
     * it is possible to get an incorrect value. For example, if the default block state is given, then it cannot be
     * checked if the block state is waterlogged.
     *
     * @param blockState The {@link BlockState} to check.
     * @return Whether the block is water-like.
     */
    public static boolean isWaterLike(BlockState blockState)
    {
        return blockState.getFluidState().is(FluidTags.WATER);
    }
}
