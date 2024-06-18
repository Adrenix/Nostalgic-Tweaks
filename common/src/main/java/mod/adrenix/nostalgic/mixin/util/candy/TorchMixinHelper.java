package mod.adrenix.nostalgic.mixin.util.candy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.world.BlockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public abstract class TorchMixinHelper
{
    /* Fields */

    public static final float SHEAR = 0.401F;
    public static final float Y_SHIFT = 0.198F;
    public static final float XZ_SHIFT = 0.579F;

    /* Methods */

    /**
     * Check if the given block state is a torch-like block.
     *
     * @param blockState The {@link BlockState} instance to check.
     * @return Whether the given block state is torch-like.
     */
    public static boolean isLikeTorch(BlockState blockState)
    {
        return BlockUtil.match(blockState, Blocks.TORCH, Blocks.REDSTONE_TORCH, Blocks.SOUL_TORCH, Blocks.WALL_TORCH, Blocks.REDSTONE_WALL_TORCH, Blocks.SOUL_WALL_TORCH);
    }

    /**
     * Check if the given block state is not a torch-like block.
     *
     * @param blockState The {@link BlockState} instance to check.
     * @return Whether the given block state is not torch-like.
     */
    public static boolean isNotLikeTorch(BlockState blockState)
    {
        return !isLikeTorch(blockState);
    }

    /**
     * Check if the given block state is a wall-torch-like block.
     *
     * @param blockState The {@link BlockState} instance to check.
     * @return Whether the given block state is wall-torch-like.
     */
    public static boolean isOldWall(BlockState blockState)
    {
        return CandyTweak.OLD_TORCH_MODEL.get() && blockState.is(Blocks.WALL_TORCH);
    }

    /**
     * Check if the given block state is a soul-wall-torch-like block.
     *
     * @param blockState The {@link BlockState} instance to check.
     * @return Whether the given block state is soul-wall-torch-like.
     */
    public static boolean isOldSoulWall(BlockState blockState)
    {
        return CandyTweak.OLD_SOUL_TORCH_MODEL.get() && blockState.is(Blocks.SOUL_WALL_TORCH);
    }

    /**
     * Check if the given block state is a redstone-wall-torch-like block.
     *
     * @param blockState The {@link BlockState} instance to check.
     * @return Whether the given block state is redstone-wall-torch-like.
     */
    public static boolean isOldRedstoneWall(BlockState blockState)
    {
        return CandyTweak.OLD_REDSTONE_TORCH_MODEL.get() && blockState.is(Blocks.REDSTONE_WALL_TORCH);
    }

    /**
     * Check if the given block state is an old wall torch and whether that torch's model should be sheared.
     *
     * @param blockState The {@link BlockState} instance to check.
     * @return Whether the block state is eligible for old torch model shearing.
     */
    public static boolean isSheared(BlockState blockState)
    {
        return isOldWall(blockState) || isOldSoulWall(blockState) || isOldRedstoneWall(blockState);
    }

    /**
     * Get the correct model from the given torch block state.
     *
     * @param blockState The {@link BlockState} instance to get data from.
     * @return The {@link BakedModel} to use for retrieving vertices data.
     */
    public static BakedModel getModel(BlockState blockState)
    {
        BlockState defaultTorch = Blocks.TORCH.defaultBlockState();

        if (isOldRedstoneWall(blockState))
            defaultTorch = Blocks.REDSTONE_TORCH.withPropertiesOf(blockState);
        else if (isOldSoulWall(blockState))
            defaultTorch = Blocks.SOUL_TORCH.withPropertiesOf(blockState);

        return Minecraft.getInstance().getBlockRenderer().getBlockModel(defaultTorch);
    }

    /**
     * Check if a torch should use the old brightness value.
     *
     * @param blockState The {@link BlockState} to check.
     * @return Whether to use old torch brightness.
     */
    public static boolean isBright(BlockState blockState)
    {
        if (BlockUtil.match(blockState, Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH))
            return CandyTweak.OLD_TORCH_BRIGHTNESS.get() && blockState.getValue(RedstoneTorchBlock.LIT);

        return CandyTweak.OLD_TORCH_BRIGHTNESS.get();
    }

    /**
     * Apply shearing to the given {@link PoseStack}. This will not push or pop the stack.
     *
     * @param poseStack  The current {@link PoseStack}.
     * @param blockState The {@link BlockState} to get direction data from.
     */
    public static void applyShear(PoseStack poseStack, BlockState blockState)
    {
        Matrix4f matrix = poseStack.last().pose();
        Direction direction = blockState.getValue(WallTorchBlock.FACING);

        switch (direction)
        {
            case NORTH ->
            {
                matrix.m12(-SHEAR);
                poseStack.translate(0.0F, Y_SHIFT, XZ_SHIFT);
            }
            case SOUTH ->
            {
                matrix.m12(SHEAR);
                poseStack.translate(0.0F, Y_SHIFT, -XZ_SHIFT);
            }
            case EAST ->
            {
                matrix.m10(SHEAR);
                poseStack.translate(-XZ_SHIFT, Y_SHIFT, 0.0F);
            }
            case WEST ->
            {
                matrix.m10(-SHEAR);
                poseStack.translate(XZ_SHIFT, Y_SHIFT, 0.0F);
            }
        }
    }

    /**
     * Write sheared torch model data to a vertex consumer.
     *
     * @param poseStack          The current {@link PoseStack}.
     * @param blockAndTintGetter The {@link BlockAndTintGetter} to get light data from.
     * @param vertexConsumer     The {@link VertexConsumer} to write vertex data to.
     * @param model              The original torch {@link BakedModel}.
     * @param blockState         The torch {@link BlockState} instance.
     * @param blockPos           The torch {@link BlockPos} instance.
     * @param random             The {@link RandomSource} to use.
     */
    public static void writeVertices(PoseStack poseStack, BlockAndTintGetter blockAndTintGetter, VertexConsumer vertexConsumer, BakedModel model, BlockState blockState, BlockPos blockPos, RandomSource random)
    {
        int blockLight = blockAndTintGetter.getBrightness(LightLayer.BLOCK, blockPos);
        int skyLight = blockAndTintGetter.getBrightness(LightLayer.SKY, blockPos);
        int brightness = isBright(blockState) ? LightTexture.FULL_BRIGHT : LightTexture.pack(blockLight, skyLight);

        if (!isSheared(blockState))
        {
            for (BakedQuad quad : model.getQuads(blockState, null, random))
                vertexConsumer.putBulkData(poseStack.last(), quad, 1.0F, 1.0F, 1.0F, 1.0F, brightness, OverlayTexture.NO_OVERLAY);

            return;
        }

        boolean isBottomDisabled = CandyTweak.OLD_TORCH_BOTTOM.get();

        poseStack.pushPose();
        applyShear(poseStack, blockState);

        for (BakedQuad quad : getModel(blockState).getQuads(blockState, null, random))
        {
            if (isBottomDisabled && quad.getDirection() == Direction.DOWN)
                continue;

            vertexConsumer.putBulkData(poseStack.last(), quad, 1.0F, 1.0F, 1.0F, 1.0F, brightness, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
    }
}
