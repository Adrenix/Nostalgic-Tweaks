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
     * Used to check if a climbable block is within two blocks of the given block position.
     *
     * @param level The level to get block states from.
     * @param pos   The block position to check from.
     * @return Whether there is a climbable block within two blocks.
     */
    public static boolean isClimbableWithinTwoBlocks(Level level, BlockPos pos)
    {
        boolean isBelowClimbable = level.getBlockState(pos.below()).is(BlockTags.CLIMBABLE);
        boolean isBelowNextClimbable = level.getBlockState(pos.below().below()).is(BlockTags.CLIMBABLE);

        return isBelowClimbable || isBelowNextClimbable;
    }

    /**
     * This is used by the old ladder gaps tweak and checks if a block should be considered climbable by the server. If
     * there is a single gap between two ladders, then the gap in-between should be considered climbable. Trap doors
     * will be climbable if there is a ladder within two blocks below an opened trap door.
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
        {
            if (BlockServerUtil.isClimbableWithinTwoBlocks(level, pos))
                return true;
        }

        BlockPos posAbove = pos.above();
        BlockState stateAbove = level.getBlockState(posAbove);

        boolean isAboveClimbable = stateAbove.is(BlockTags.CLIMBABLE);

        if (stateAbove.getBlock() instanceof TrapDoorBlock && stateAbove.getValue(TrapDoorBlock.OPEN))
        {
            if (BlockServerUtil.isClimbableWithinTwoBlocks(level, posAbove))
                return true;
        }

        return isClimbable || isAboveClimbable;
    }
}
