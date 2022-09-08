package mod.adrenix.nostalgic.util.client;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.common.BlockCommonUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 * For a server safe mixin utility use {@link mod.adrenix.nostalgic.util.server.BlockServerUtil}.
 */

public abstract class BlockClientUtil
{
    /**
     * Gets a block state at the given block position.
     * @param pos The position to get a block state from.
     * @return The block state at the given position. Will be <code>AIR</code> if there is no client level.
     */
    public static BlockState getState(BlockPos pos)
    {
        ClientLevel level = Minecraft.getInstance().level;
        return level != null ? level.getBlockState(pos) : Blocks.AIR.defaultBlockState();
    }

    /**
     * Gets a random block position within the given <code>bounds</code>.
     * @param randomSource Used to get a random integer from.
     * @param bound How far out from the source should get a random position from.
     * @return A random block position using the given arguments.
     */
    public static BlockPos getRandomPos(RandomSource randomSource, int bound)
    {
        return new BlockPos(randomSource.nextInt(bound), randomSource.nextInt(bound), randomSource.nextInt(bound));
    }

    /**
     * Checks if the given block position is near the bedrock layer.
     * @param pos The block position to check.
     * @param level The level to get the minimum build height from.
     * @return Whether the block position is less than 5 blocks above the minimum build height.
     */
    public static boolean isNearBedrock(BlockPos pos, Level level)
    {
        return pos.getY() < level.getMinBuildHeight() + 5;
    }

    /**
     * Checks if the given block should be considered a 'full block' shape.
     * If fixed ambient occlusion is enabled, then the result will be <code>true</code> if the block matches.
     * @param block The block to see if it should be considered 'full'.
     * @return Whether the block should be considered to have a 'full' block-like shape.
     */
    public static boolean isFullShape(Block block)
    {
        boolean isChest = BlockCommonUtil.isOldChest(block);
        boolean isAOFixed = ModConfig.Candy.fixAmbientOcclusion();
        boolean isSoulSand = isAOFixed && block.getClass().equals(SoulSandBlock.class);
        boolean isPowderedSnow = isAOFixed && block.getClass().equals(PowderSnowBlock.class);
        boolean isComposter = isAOFixed && block.getClass().equals(ComposterBlock.class);
        boolean isPiston = isAOFixed && block.getClass().equals(PistonBaseBlock.class);

        return isChest || isSoulSand || isPowderedSnow || isComposter || isPiston;
    }
}
