package mod.adrenix.nostalgic.helper.candy.block.cross.connector;

import mod.adrenix.nostalgic.mixin.access.WallBlockAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WallConnector implements ConnectorBlock
{
    /**
     * Maps {@link Direction} faces to {@link EnumProperty} {@link WallSide} enumeration values.
     *
     * @param face The {@link Direction} block face to map.
     * @return The {@link EnumProperty} {@link WallSide} if it exists, otherwise {@code null}.
     */
    protected @Nullable EnumProperty<WallSide> getWallProperty(Direction face)
    {
        return switch (face)
        {
            case NORTH -> WallBlock.NORTH_WALL;
            case SOUTH -> WallBlock.SOUTH_WALL;
            case EAST -> WallBlock.EAST_WALL;
            case WEST -> WallBlock.WEST_WALL;
            default -> null;
        };
    }

    @Override
    public BlockState updateFace(BlockAndTintGetter level, BlockState blockState, BlockPos blockPos, BlockState neighborState, Direction face)
    {
        EnumProperty<WallSide> property = this.getWallProperty(face);

        if (property == null || !blockState.hasProperty(property))
            return blockState;

        if (blockState.getBlock() instanceof WallBlock wallBlock)
        {
            BlockState aboveState = level.getBlockState(blockPos.relative(Direction.UP));
            VoxelShape shape = aboveState.getCollisionShape(level, blockPos).getFaceShape(Direction.DOWN);

            blockState = blockState.setValue(property, this.connects(blockState, neighborState, face) ? blockState.getValue(property) : WallSide.NONE);
            blockState = blockState.setValue(WallBlock.UP, ((WallBlockAccess) wallBlock).nt$shouldRaisePost(blockState, aboveState, shape));
        }

        return blockState;
    }

    @Override
    public boolean matches(BlockState blockState)
    {
        return blockState.getBlock() instanceof WallBlock;
    }

    @Override
    public boolean connects(BlockState blockState, BlockState neighborState, Direction face)
    {
        if (blockState.getBlock() instanceof WallBlock wallBlock)
        {
            if (neighborState.getBlock() instanceof IronBarsBlock)
                return false;

            return ((WallBlockAccess) wallBlock).nt$connectsTo(neighborState, false, face);
        }

        return false;
    }
}
