package mod.adrenix.nostalgic.util.server;

import mod.adrenix.nostalgic.common.config.MixinConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.PistonBaseBlock;

import java.util.function.Consumer;

/**
 * This mixin utility uses Minecraft classes that are available to the server. For safety, the server should only
 * interface with this utility.
 *
 * For a client only mixin utility use {@link mod.adrenix.nostalgic.util.client.MixinClientUtil}.
 */

public abstract class MixinServerUtil
{
    /* Block Mixin Helpers */

    public static class Block
    {
        public static boolean isBlockOldChest(net.minecraft.world.level.block.Block block)
        {
            boolean isOldChest = MixinConfig.Candy.oldChest() && block.getClass().equals(ChestBlock.class);
            boolean isOldEnder = MixinConfig.Candy.oldEnderChest() && block.getClass().equals(EnderChestBlock.class);
            boolean isOldTrap = MixinConfig.Candy.oldTrappedChest() && block.getClass().equals(TrappedChestBlock.class);

            return isOldChest || isOldEnder || isOldTrap;
        }

        public static boolean isBlockFullShape(net.minecraft.world.level.block.Block block)
        {
            boolean isChest = isBlockOldChest(block);
            boolean isAOFixed = MixinConfig.Candy.fixAmbientOcclusion();
            boolean isSoulSand = isAOFixed && block.getClass().equals(SoulSandBlock.class);
            boolean isPowderedSnow = isAOFixed && block.getClass().equals(PowderSnowBlock.class);
            boolean isComposter = isAOFixed && block.getClass().equals(ComposterBlock.class);
            boolean isPiston = isAOFixed && block.getClass().equals(PistonBaseBlock.class);

            return isChest || isSoulSand || isPowderedSnow || isComposter || isPiston;
        }
    }

    /* World Candy Mixin Helpers */

    public static class World
    {
        // Checks if a chunk is on the edge of a square border render distance.
        public static int squareDistance(int chunkX, int chunkZ, int secX, int secZ)
        {
            int diffX = chunkX - secX;
            int diffY = chunkZ - secZ;
            return Math.max(Math.abs(diffX), Math.abs(diffY));
        }
    }

    /* Item Candy Mixin Helpers */

    public static class Item
    {
        // Used to handle differences between forge and fabric when separating merged items
        public static Consumer<ItemStack> explodeStack(Consumer<ItemStack> consumer)
        {
            if (!MixinConfig.Candy.oldItemMerging())
                return consumer;
            return stack -> {
                ItemStack instance = stack.copy();
                instance.setCount(1);

                for (int i = 0; i < stack.getCount(); i++)
                    consumer.accept(instance);
            };
        }
    }
}
