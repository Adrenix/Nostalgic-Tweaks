package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.common.BlockCommonUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 * For a server safe mixin utility use {@link mod.adrenix.nostalgic.util.server.BlockServerUtil}.
 */

public abstract class BlockClientUtil
{
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

    /**
     * Checks if the given block state is a torch model that should be rendered in the old style.
     * @param state A known block state.
     * @return Whether the block state is applicable to old torch rendering.
     */
    public static boolean isTorchModel(BlockState state)
    {
        return BlockCommonUtil.isBlockEqualTo(state, Blocks.TORCH, Blocks.REDSTONE_TORCH, Blocks.SOUL_TORCH, Blocks.WALL_TORCH, Blocks.REDSTONE_WALL_TORCH, Blocks.SOUL_WALL_TORCH);
    }

    /**
     * Brings back the old torch rendering by applying a full-bright int to the model texture and rendering wall torches
     * as parallelepiped as it was done before Minecraft release 1.8.
     * @param poseStack The current matrix stack.
     * @param consumer The current vertex consumer.
     * @param model The original torch model to render.
     * @param state The torch's current block state.
     * @param position The torch's current block position in the world.
     * @param random A random source.
     */
    public static void oldTorch(PoseStack poseStack, VertexConsumer consumer, BakedModel model, BlockState state, BlockPos position, RandomSource random)
    {
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null)
            return;

        boolean isWallTorch = ModConfig.Candy.oldTorchModel() && state.is(Blocks.WALL_TORCH);
        boolean isSoulWallTorch = ModConfig.Candy.oldSoulTorchModel() && state.is(Blocks.SOUL_WALL_TORCH);
        boolean isRedstoneWallTorch = ModConfig.Candy.oldRedstoneTorchModel() && state.is(Blocks.REDSTONE_WALL_TORCH);
        boolean isOldBrightness = ModConfig.Candy.oldTorchBrightness();
        boolean isTorchSheared = isWallTorch || isSoulWallTorch || isRedstoneWallTorch;

        int blockLight = level.getBrightness(LightLayer.BLOCK, position);
        int skyLight =  level.getBrightness(LightLayer.SKY, position);
        int brightness = isOldBrightness ? LightTexture.FULL_BRIGHT : LightTexture.pack(blockLight, skyLight);
        int overlay = OverlayTexture.NO_OVERLAY;

        if (isTorchSheared)
        {
            poseStack.pushPose();

            BlockState baseTorch = Blocks.TORCH.defaultBlockState();
            Direction direction = state.getValue(WallTorchBlock.FACING);
            Matrix4f matrix = poseStack.last().pose();

            if (isRedstoneWallTorch)
                baseTorch = Blocks.REDSTONE_TORCH.withPropertiesOf(state);
            else if (isSoulWallTorch)
                baseTorch = Blocks.SOUL_TORCH.defaultBlockState();

            BakedModel torch = Minecraft.getInstance().getBlockRenderer().getBlockModel(baseTorch);

            float shear = 0.401F;
            float yShift = 0.198F;
            float xzShift = 0.579F;

            if (direction == Direction.EAST)
            {
                matrix.m10(shear);
                poseStack.translate(-xzShift, yShift, 0.0F);
            }
            else if (direction == Direction.WEST)
            {
                matrix.m10(-shear);
                poseStack.translate(xzShift, yShift, 0.0F);
            }
            else if (direction == Direction.SOUTH)
            {
                matrix.m12(shear);
                poseStack.translate(0.0F, yShift, -xzShift);
            }
            else if (direction == Direction.NORTH)
            {
                matrix.m12(-shear);
                poseStack.translate(0.0F, yShift, xzShift);
            }

            for (BakedQuad quad : torch.getQuads(state, null, random))
                consumer.putBulkData(poseStack.last(), quad, 1.0F, 1.0F, 1.0F, brightness, overlay);

            poseStack.popPose();
        }
        else
        {
            for (BakedQuad quad : model.getQuads(state, null, random))
                consumer.putBulkData(poseStack.last(), quad, 1.0F, 1.0F, 1.0F, brightness, overlay);
        }
    }
}
