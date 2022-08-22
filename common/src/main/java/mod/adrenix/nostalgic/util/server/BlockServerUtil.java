package mod.adrenix.nostalgic.util.server;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This utility is used by the server. For safety, keep client-only code out.
 * For a client only utility use {@link mod.adrenix.nostalgic.util.client.BlockClientUtil}.
 */

public abstract class BlockServerUtil
{
    public static boolean isClimbable(Level level, BlockState state, BlockPos pos)
    {
        boolean isClimbable = state.is(BlockTags.CLIMBABLE);

        if (!ModConfig.Gameplay.oldLadderGap())
            return isClimbable;
        return isClimbable || level.getBlockState(pos.above()).is(BlockTags.CLIMBABLE);
    }

    public static boolean isChest(Block block)
    {
        boolean isChest = block.getClass().equals(ChestBlock.class);
        boolean isEnderChest = block.getClass().equals(EnderChestBlock.class);
        boolean isTrappedChest = block.getClass().equals(TrappedChestBlock.class);

        return isChest || isEnderChest || isTrappedChest;
    }
}
