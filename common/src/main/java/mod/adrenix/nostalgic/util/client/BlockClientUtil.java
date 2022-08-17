package mod.adrenix.nostalgic.util.client;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.PistonBaseBlock;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 * For a server safe mixin utility use {@link mod.adrenix.nostalgic.util.server.BlockServerUtil}.
 */

public abstract class BlockClientUtil
{
    public static boolean isOldChest(Block block)
    {
        boolean isOldChest = ModConfig.Candy.oldChest() && block.getClass().equals(ChestBlock.class);
        boolean isOldEnder = ModConfig.Candy.oldEnderChest() && block.getClass().equals(EnderChestBlock.class);
        boolean isOldTrap = ModConfig.Candy.oldTrappedChest() && block.getClass().equals(TrappedChestBlock.class);

        return isOldChest || isOldEnder || isOldTrap;
    }

    public static boolean isFullShape(Block block)
    {
        boolean isChest = isOldChest(block);
        boolean isAOFixed = ModConfig.Candy.fixAmbientOcclusion();
        boolean isSoulSand = isAOFixed && block.getClass().equals(SoulSandBlock.class);
        boolean isPowderedSnow = isAOFixed && block.getClass().equals(PowderSnowBlock.class);
        boolean isComposter = isAOFixed && block.getClass().equals(ComposterBlock.class);
        boolean isPiston = isAOFixed && block.getClass().equals(PistonBaseBlock.class);

        return isChest || isSoulSand || isPowderedSnow || isComposter || isPiston;
    }
}
