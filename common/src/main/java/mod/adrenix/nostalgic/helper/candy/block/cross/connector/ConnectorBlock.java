package mod.adrenix.nostalgic.helper.candy.block.cross.connector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface ConnectorBlock
{
    /**
     * Provide the logic to use to determine if the given block state is eligible for modification.
     *
     * @param blockState The {@link BlockState} to check.
     * @return Whether the given state is eligible for modification by this {@link ConnectorBlock} instance.
     */
    boolean matches(BlockState blockState);

    /**
     * Provide the logic to use to determine if the cross block is connecting to a similar neighbor block face.
     *
     * @param blockState    The {@link BlockState} of the cross block.
     * @param neighborState The {@link BlockState} of the neighbor relative to the cross block's given face.
     * @param face          The {@link Direction} face to check.
     * @return Whether a connection should be rendered for the given face.
     */
    boolean connects(BlockState blockState, BlockState neighborState, Direction face);

    /**
     * Update the face of a cross block so that its arm disconnects from dissimilar connector blocks.
     *
     * @param level         The {@link BlockAndTintGetter} level instance.
     * @param blockState    The {@link BlockState} to use for state updates.
     * @param blockPos      The {@link BlockPos} of the cross block being updated.
     * @param neighborState The {@link BlockState} of the neighbor relative to the given face.
     * @param face          The {@link Direction} face to modify.
     * @return The updated cross block state.
     */
    BlockState updateFace(BlockAndTintGetter level, BlockState blockState, BlockPos blockPos, BlockState neighborState, Direction face);

    /**
     * Get the beta-like cross block appearance.
     *
     * @param level      The {@link BlockAndTintGetter} level instance.
     * @param blockState The {@link BlockState} to use for state updates.
     * @param blockPos   The {@link BlockPos} of the cross block.
     * @return The {@link BlockState} to use for rendering.
     */
    default BlockState getState(BlockAndTintGetter level, BlockState blockState, BlockPos blockPos)
    {
        for (Direction face : Direction.Plane.HORIZONTAL)
        {
            BlockPos neighborPos = blockPos.relative(face);
            BlockState neighborState = level.getBlockState(neighborPos);

            blockState = this.updateFace(level, blockState, blockPos, neighborState, face);
        }

        return blockState;
    }
}
