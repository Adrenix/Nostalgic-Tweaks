package mod.adrenix.nostalgic.util.server;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This utility is used by the server. For safety, keep client-only code out. For a client only utility use
 * {@link mod.adrenix.nostalgic.util.client.BlockClientUtil}.
 */

public abstract class BlockServerUtil
{
    /**
     * This is used by the old ladder gaps tweak and checks if a block should be considered climbable by the server. If
     * there is a single gap between two ladders, then the gap in-between should be considered climbable.
     *
     * @param level The level to get block states from.
     * @param state The block state to check if a climbable tag is present.
     * @param pos   The block position of the climbable block.
     * @return Whether the block above the given position should be considered climbable.
     */
    public static boolean isClimbable(Level level, BlockState state, BlockPos pos)
    {
        boolean isClimbable = state.is(BlockTags.CLIMBABLE);

        if (!ModConfig.Gameplay.oldLadderGap())
            return isClimbable;

        if (state.getBlock() instanceof TrapDoorBlock && state.getValue(TrapDoorBlock.OPEN))
            return true;

        BlockPos posAbove = pos.above();
        BlockState stateAbove = level.getBlockState(posAbove);

        boolean isAboveClimbable = stateAbove.is(BlockTags.CLIMBABLE);

        if (stateAbove.getBlock() instanceof TrapDoorBlock && stateAbove.getValue(TrapDoorBlock.OPEN))
            return true;

        return isClimbable || isAboveClimbable;
    }
}
