package mod.adrenix.nostalgic.util.common;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockAndTintGetter;
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
     * Gets the correct skylight block value for water based on its stored skylight level.
     * @param skylight The stored skylight from the level.
     * @return A new light block amount.
     */
    public static int getWaterLightBlock(int skylight)
    {
        return switch (skylight)
        {
            case 15 -> 15;
            case 14 -> 12;
            case 13 -> 9;
            case 12 -> 6;
            case 11 -> 3;
            default -> 0;
        };
    }

    /**
     * Checks if the given block position is within water.
     * @param level The level that can retrieve fluid states.
     * @param pos The position of the block to check.
     * @return Whether the block is within a water block.
     */
    public static boolean isInWater(BlockAndTintGetter level, BlockPos pos)
    {
        return level.getFluidState(pos).is(FluidTags.WATER);
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
}
