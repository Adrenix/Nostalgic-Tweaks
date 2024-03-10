package mod.adrenix.nostalgic.util.common.world;

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
}
