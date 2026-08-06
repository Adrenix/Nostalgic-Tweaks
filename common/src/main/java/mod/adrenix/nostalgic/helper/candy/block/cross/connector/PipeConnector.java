package mod.adrenix.nostalgic.helper.candy.block.cross.connector;

import mod.adrenix.nostalgic.helper.candy.block.cross.CrossBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class PipeConnector implements ConnectorBlock
{
    @Override
    public BlockState updateFace(BlockAndTintGetter level, BlockState blockState, BlockPos blockPos, BlockState neighborState, Direction face)
    {
        BooleanProperty property = PipeBlock.PROPERTY_BY_DIRECTION.get(face);

        if (property == null || !blockState.hasProperty(property))
            return blockState;

        return blockState.setValue(property, this.connects(blockState, neighborState, face));
    }

    /**
     * Vanilla-like fence blocks.
     */
    public static class Fence extends PipeConnector
    {
        @Override
        public boolean matches(BlockState blockState)
        {
            return blockState.getBlock() instanceof FenceBlock;
        }

        @Override
        public boolean connects(BlockState blockState, BlockState neighborState, Direction face)
        {
            if (blockState.getBlock() instanceof FenceBlock fenceBlock)
                return fenceBlock.connectsTo(neighborState, false, face);

            return false;
        }
    }

    /**
     * Iron-bars-like blocks.
     */
    public static class IronBars extends PipeConnector
    {
        @Override
        public boolean matches(BlockState blockState)
        {
            if (CrossBlock.GLASS_PANES.getInstance().matches(blockState))
                return false;

            return blockState.getBlock() instanceof IronBarsBlock;
        }

        @Override
        public boolean connects(BlockState blockState, BlockState neighborState, Direction face)
        {
            if (blockState.getBlock() instanceof IronBarsBlock ironBarsBlock)
            {
                if (CrossBlock.GLASS_PANES.getInstance().matches(neighborState) || neighborState.is(BlockTags.WALLS))
                    return false;

                return ironBarsBlock.attachsTo(neighborState, false);
            }

            return false;
        }
    }

    /**
     * Glass-panes-like blocks.
     */
    public static class GlassPanes extends PipeConnector
    {
        @Override
        public boolean matches(BlockState blockState)
        {
            return blockState.is(Blocks.GLASS_PANE) || blockState.getBlock() instanceof StainedGlassPaneBlock;
        }

        @Override
        public boolean connects(BlockState blockState, BlockState neighborState, Direction face)
        {
            if (blockState.getBlock() instanceof IronBarsBlock glassPaneBlock)
            {
                if (neighborState.is(Blocks.IRON_BARS) || neighborState.is(BlockTags.WALLS))
                    return false;

                return glassPaneBlock.attachsTo(neighborState, false);
            }

            return false;
        }
    }
}
