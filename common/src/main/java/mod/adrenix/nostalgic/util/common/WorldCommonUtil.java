package mod.adrenix.nostalgic.util.common;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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

/**
 * This utility is used by the client and server. For safety, keep client-only code out.
 * For a client only utility use {@link mod.adrenix.nostalgic.util.client.WorldClientUtil}
 * For a server only utility use {@link mod.adrenix.nostalgic.util.server.WorldServerUtil}
 */

public abstract class WorldCommonUtil
{
    /**
     * Get the vanilla brightness value from the provided light layer and block position.
     * This bypasses the old light rendering values.
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
     * Returns a new skylight value based on the sun angle.
     * @param level A level instance.
     * @return A skylight value based on the sun angle.
     */
    public static int getDynamicSkylight(LevelAccessor level)
    {
        float sunAngle = level.getTimeOfDay(1.0F) * ((float) Math.PI * 2.0F);
        int skyLight = 15;

        if (MathUtil.isInRange(sunAngle, 1.8235918F, 4.459594F))
            skyLight = 4;
        else if (MathUtil.isInRange(sunAngle, 4.459884F, 4.5061855F) || MathUtil.isInRange(sunAngle, 1.7769997F, 1.8233016F))
            skyLight = 5;
        else if (MathUtil.isInRange(sunAngle, 4.5064745F, 4.55252F) || MathUtil.isInRange(sunAngle, 1.7306658F, 1.7767112F))
            skyLight = 6;
        else if (MathUtil.isInRange(sunAngle, 4.552807F, 4.5983024F) || MathUtil.isInRange(sunAngle, 1.684883F, 1.7303787F))
            skyLight = 7;
        else if (MathUtil.isInRange(sunAngle, 4.598588F, 4.6440983F) || MathUtil.isInRange(sunAngle, 1.6390872F, 1.6845976F))
            skyLight = 8;
        else if (MathUtil.isInRange(sunAngle, 4.6443815F, 4.689612F) || MathUtil.isInRange(sunAngle, 1.5938551F, 1.6388037F))
            skyLight = 9;
        else if (MathUtil.isInRange(sunAngle, 4.6898937F, 4.735117F) || MathUtil.isInRange(sunAngle, 1.548349F, 1.5935733F))
            skyLight = 10;
        else if (MathUtil.isInRange(sunAngle, 4.7353964F, 4.7805977F) || MathUtil.isInRange(sunAngle, 1.5028657F, 1.548069F))
            skyLight = 11;
        else if (MathUtil.isInRange(sunAngle, 4.780876F, 4.826043F) || MathUtil.isInRange(sunAngle, 1.4571424F, 1.5025874F))
            skyLight = 12;
        else if (MathUtil.isInRange(sunAngle, 4.826319F, 4.8719864F) || MathUtil.isInRange(sunAngle, 1.4111987F, 1.4568661F))
            skyLight = 13;
        else if (MathUtil.isInRange(sunAngle, 4.8722606F, 4.9184027F) || MathUtil.isInRange(sunAngle, 1.3650552F, 1.4109247F))
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
        RandomSource random = level.getRandom();

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
    public static boolean isBonemealApplicable(BonemealableBlock block, Level level, RandomSource random, BlockPos pos, BlockState state)
    {
        return ModConfig.Gameplay.instantBonemeal() || block.isBonemealSuccess(level, random, pos, state);
    }
}
