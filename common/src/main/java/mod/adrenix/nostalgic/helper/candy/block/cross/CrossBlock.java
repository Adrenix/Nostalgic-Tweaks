package mod.adrenix.nostalgic.helper.candy.block.cross;

import mod.adrenix.nostalgic.helper.candy.block.cross.connector.ConnectorBlock;
import mod.adrenix.nostalgic.helper.candy.block.cross.connector.PipeConnector;
import mod.adrenix.nostalgic.helper.candy.block.cross.connector.WallConnector;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.factory.TweakFlag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public enum CrossBlock
{
    FENCE(CandyTweak.OLD_FENCE_CONNECTIONS, new PipeConnector.Fence()),
    IRON_BARS(CandyTweak.OLD_STYLE_IRON_BARS_CONNECTIONS, new PipeConnector.IronBars()),
    GLASS_PANES(CandyTweak.OLD_STYLE_GLASS_PANES_CONNECTIONS, new PipeConnector.GlassPanes()),
    WALL(CandyTweak.OLD_STYLE_WALL_CONNECTIONS, new WallConnector());

    private final TweakFlag tweak;
    private final ConnectorBlock connector;

    CrossBlock(TweakFlag tweak, ConnectorBlock connector)
    {
        this.tweak = tweak;
        this.connector = connector;
    }

    public ConnectorBlock getInstance()
    {
        return this.connector;
    }

    public TweakFlag getTweak()
    {
        return this.tweak;
    }

    /**
     * A connector can only edit a block state if the tweak is enabled and the block state is eligible for
     * modification.
     *
     * @param blockState The {@link BlockState} to check.
     * @return Whether this connector can edit the given state.
     */
    public boolean canEdit(BlockState blockState)
    {
        return this.tweak.get() && this.connector.matches(blockState);
    }

    /**
     * Process the given cross block state and modify it based on tweak context.
     *
     * @param level      The {@link BlockAndTintGetter} level.
     * @param blockState The {@link BlockState}.
     * @param blockPos   The {@link BlockPos}.
     * @return A modified {@link BlockState} if changed, or the original argument.
     */
    public static BlockState getState(BlockAndTintGetter level, BlockState blockState, BlockPos blockPos)
    {
        if (blockState.isAir())
            return blockState;

        for (CrossBlock block : values())
        {
            if (block.canEdit(blockState))
                return block.connector.getState(level, blockState, blockPos);
        }

        return blockState;
    }
}
