package mod.adrenix.nostalgic.helper.gameplay;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This utility class is used by both the client and server.
 */
public abstract class ClimbableHelper
{
    /**
     * Used to check if a climbable block is within two blocks of the given block position.
     *
     * @param level    The {@link Level} to get block states from.
     * @param blockPos The {@link BlockPos} to check.
     * @return Whether there is a climbable block within two blocks.
     */
    private static boolean isClimbableWithinTwoBlocks(Level level, BlockPos blockPos)
    {
        boolean isBelowClimbable = level.getBlockState(blockPos.below()).is(BlockTags.CLIMBABLE);
        boolean isBelowNextClimbable = level.getBlockState(blockPos.below().below()).is(BlockTags.CLIMBABLE);

        return isBelowClimbable || isBelowNextClimbable;
    }

    /**
     * This is used by the old ladder gaps tweak and checks if a block should be considered climbable by the server. If
     * there is a single gap between two ladders, then the gap in-between should be considered climbable. Trap doors
     * will be climbable if there is a ladder within two blocks below an opened trap door.
     *
     * @param level      The {@link Level} to get block states from.
     * @param blockState The {@link BlockState} to check if a climbable tag is present.
     * @param blockPos   The {@link BlockPos} of the climbable block.
     * @return Whether the block above the given position should be considered climbable.
     */
    public static boolean isClimbable(Level level, BlockState blockState, BlockPos blockPos)
    {
        if (blockState.getBlock() instanceof TrapDoorBlock && blockState.getValue(TrapDoorBlock.OPEN))
        {
            if (isClimbableWithinTwoBlocks(level, blockPos))
                return true;
        }

        BlockPos posAbove = blockPos.above();
        BlockState stateAbove = level.getBlockState(posAbove);

        if (stateAbove.getBlock() instanceof TrapDoorBlock && stateAbove.getValue(TrapDoorBlock.OPEN))
        {
            if (isClimbableWithinTwoBlocks(level, posAbove))
                return true;
        }

        return blockState.is(BlockTags.CLIMBABLE) || stateAbove.is(BlockTags.CLIMBABLE);
    }
}
