package mod.adrenix.nostalgic.util.server;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import java.util.function.Consumer;

/**
 * This utility is used by the server. For safety, keep client-only code out. Only the server should interface with this utility.
 * For a client only mixin utility use {@link mod.adrenix.nostalgic.util.client.ModClientUtil}.
 */

public abstract class ModServerUtil
{
    /* Gameplay Helpers */

    public static class Gameplay
    {
        public static boolean isClimbable(net.minecraft.world.World level, BlockState state, BlockPos pos)
        {
            boolean isClimbable = state.isIn(BlockTags.CLIMBABLE);

            if (!ModConfig.Gameplay.oldLadderGap())
                return isClimbable;
            return isClimbable || level.getBlockState(pos.up()).isIn(BlockTags.CLIMBABLE);
        }
    }

    /* World Candy Helpers */

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

    /* Item Candy Helpers */

    public static class Item
    {
        // Used to disable old food stacking when food is being dropped from a loot table
        public static boolean isDroppingLoot = false;

        // Used to handle differences between forge and fabric when separating merged items
        public static Consumer<ItemStack> explodeStack(Consumer<ItemStack> consumer)
        {
            if (!ModConfig.Candy.oldItemMerging())
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
