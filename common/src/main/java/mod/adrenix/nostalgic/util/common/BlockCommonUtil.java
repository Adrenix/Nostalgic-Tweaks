package mod.adrenix.nostalgic.util.common;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This utility is used by both client and server. For safety, keep client-only code out.
 * For a client only utility use {@link mod.adrenix.nostalgic.util.client.BlockClientUtil}.
 * For a server only utility use {@link mod.adrenix.nostalgic.util.server.BlockServerUtil}.
 */

public abstract class BlockCommonUtil
{
    /**
     * Checks if the given block should be considered an 'old' chest. This only returns <code>true</code> if the
     * chest tweak is enabled and the block matches the expected block class.
     * @param block The block to check against.
     * @return Whether this block should be considered an old chest.
     */
    public static boolean isOldChest(Block block)
    {
        boolean isOldChest = ModConfig.Candy.oldChest() && block.getClass().equals(ChestBlock.class);
        boolean isOldEnder = ModConfig.Candy.oldEnderChest() && block.getClass().equals(EnderChestBlock.class);
        boolean isOldTrap = ModConfig.Candy.oldTrappedChest() && block.getClass().equals(TrappedChestBlock.class);

        return isOldChest || isOldEnder || isOldTrap;
    }

    /**
     * Checks if the given block position is within water.
     * @param level The level that can retrieve fluid states.
     * @param pos The position of the block to check.
     * @return Whether the block is within a water block.
     */
    public static boolean isInWater(BlockGetter level, BlockPos pos)
    {
        boolean up = false;
        boolean down = false;
        boolean east = false;
        boolean west = false;
        boolean north = false;
        boolean south = false;

        for (Direction direction : Direction.values())
        {
            boolean isFaceWatered = level.getFluidState(pos.relative(direction)).is(FluidTags.WATER);

            switch (direction)
            {
                case UP -> up = isFaceWatered;
                case DOWN -> down = isFaceWatered;
                case EAST -> east = isFaceWatered;
                case WEST -> west = isFaceWatered;
                case NORTH -> north = isFaceWatered;
                case SOUTH -> south = isFaceWatered;
            }
        }

        boolean isDry = !up && !down && !east && !west && !north && !south;
        boolean isAtop = !up && down && !east && !west && !north && !south;

        return !isDry && !isAtop;
    }

    /**
     * Checks if the given <code>block</code> is equal to any of the provided blocks.
     * @param block The block to check for in the provided varargs.
     * @param equalTo A list of blocks (varargs) to check against.
     * @return Whether the given <code>block</code> matched any of the provided <code>varargs</code>.
     */
    public static boolean isBlockEqualTo(BlockState block, Block ...equalTo)
    {
        for (Block equal : equalTo)
        {
            if (block.is(equal))
                return true;
        }

        return false;
    }

    /**
     * Checks if the given <code>block state</code> matches a vanilla water-like block such as ice.
     * @param block The block to see if it is water related.
     * @return Whether the given <code>block</code> is a water-like block.
     */
    public static boolean isWaterRelated(BlockState block)
    {
        return isBlockEqualTo(block, Blocks.WATER, Blocks.ICE, Blocks.FROSTED_ICE, Blocks.BLUE_ICE, Blocks.PACKED_ICE);
    }
}
