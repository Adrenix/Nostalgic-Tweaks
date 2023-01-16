package mod.adrenix.nostalgic.util.common;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

/**
 * This utility is used by the client and server. For safety, keep client-only code out.
 * For a client only utility use {@link mod.adrenix.nostalgic.util.client.WorldClientUtil}
 * For a server only utility use {@link mod.adrenix.nostalgic.util.server.WorldServerUtil}
 */

public abstract class WorldCommonUtil
{
    /**
     * Get the vanilla brightness value from the provided light layer and block position.
     *
     * @param level An object that implements the {@link BlockAndTintGetter} interface.
     * @param lightLayer The light layer to retrieve data from.
     * @param blockPos A block position to retrieve data from.
     * @return A vanilla brightness value (0-15)
     */
    public static int getBrightness(BlockAndTintGetter level, LightLayer lightLayer, BlockPos blockPos)
    {
        return level.getLightEngine().getLayerListener(lightLayer).getLightValue(blockPos);
    }

    /**
     * Returns a new skylight value based on the time of day.
     * @param level A level instance.
     * @return A skylight value based on the time of day.
     */
    public static int getDayLight(LevelAccessor level)
    {
        int timeOfDay = (int) (level.dayTime() % 24000L);
        int skyLight = 15;

        if (MathUtil.isInRange(timeOfDay, 13670, 22330))
            skyLight = 4;
        else if (MathUtil.isInRange(timeOfDay, 22331, 22491) || MathUtil.isInRange(timeOfDay, 13509, 13669))
            skyLight = 5;
        else if (MathUtil.isInRange(timeOfDay, 22492, 22652) || MathUtil.isInRange(timeOfDay, 13348, 13508))
            skyLight = 6;
        else if (MathUtil.isInRange(timeOfDay, 22653, 22812) || MathUtil.isInRange(timeOfDay, 13188, 13347))
            skyLight = 7;
        else if (MathUtil.isInRange(timeOfDay, 22813, 22973) || MathUtil.isInRange(timeOfDay, 13027, 13187))
            skyLight = 8;
        else if (MathUtil.isInRange(timeOfDay, 22974, 23134) || MathUtil.isInRange(timeOfDay, 12867, 13026))
            skyLight = 9;
        else if (MathUtil.isInRange(timeOfDay, 23135, 23296) || MathUtil.isInRange(timeOfDay, 12705, 12866))
            skyLight = 10;
        else if (MathUtil.isInRange(timeOfDay, 23297, 23459) || MathUtil.isInRange(timeOfDay, 12542, 12704))
            skyLight = 11;
        else if (MathUtil.isInRange(timeOfDay, 23460, 23623) || MathUtil.isInRange(timeOfDay, 12377, 12541))
            skyLight = 12;
        else if (MathUtil.isInRange(timeOfDay, 23624, 23790) || MathUtil.isInRange(timeOfDay, 12210, 12376))
            skyLight = 13;
        else if (MathUtil.isInRange(timeOfDay, 23791, 23960) || MathUtil.isInRange(timeOfDay, 12041, 12209))
            skyLight = 14;

        return skyLight;
    }

    /**
     * Adds a possible grass seed entity into the world when grass is tilled.
     * @param state The state of the current grass block.
     * @param context The use context.
     */
    public static void onTillGrass(BlockState state, UseOnContext context)
    {
        Level level = context.getLevel();
        Random random = level.getRandom();

        boolean isGrass = level.getBlockState(context.getClickedPos()).is(Blocks.GRASS_BLOCK);
        boolean isHoeItem = context.getItemInHand().getItem() instanceof HoeItem;
        boolean isFarmland = state.is(Blocks.FARMLAND);

        if (!ModConfig.Gameplay.tilledGrassSeeds() || !isHoeItem || !isGrass || !isFarmland)
            return;

        if (random.nextInt(10) == 0)
        {
            ItemStack seedItem = new ItemStack(Items.WHEAT_SEEDS);
            BlockPos clickPos = context.getClickedPos();

            double x = (double) clickPos.getX() + 0.5D + Mth.nextDouble(random, -0.05D, 0.05D);
            double y = (double) clickPos.getY() + 1.0D;
            double z = (double) clickPos.getZ() + 0.5D + Mth.nextDouble(random, -0.05D, 0.05D);

            double dx = Mth.nextDouble(random, -0.1D, 0.1D);
            double dy = Mth.nextDouble(random, 0.18D, 0.2D);
            double dz = Mth.nextDouble(random, -0.1D, 0.1D);

            ItemEntity seedEntity = new ItemEntity(level, x, y, z, seedItem, dx, dy, dz);
            seedEntity.setDefaultPickUpDelay();

            level.addFreshEntity(seedEntity);
        }
    }

    /**
     * Check if a bonemeal item can be applied to provided block.
     * @param block A block with the {@link BonemealableBlock} interface inherited.
     * @param level A level instance.
     * @param random A random source.
     * @param pos A block position.
     * @param state A block state.
     * @return Whether the block can accept bonemeal.
     */
    public static boolean isBonemealApplicable(BonemealableBlock block, Level level, Random random, BlockPos pos, BlockState state)
    {
        return ModConfig.Gameplay.instantBonemeal() || block.isBonemealSuccess(level, random, pos, state);
    }
}
