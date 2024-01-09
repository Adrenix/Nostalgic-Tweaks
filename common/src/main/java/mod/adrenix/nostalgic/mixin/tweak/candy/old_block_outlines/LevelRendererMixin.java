package mod.adrenix.nostalgic.mixin.tweak.candy.old_block_outlines;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    /**
     * Changes the voxel shape used by the hit outline renderer.
     */
    @WrapOperation(
        method = "renderHitOutline",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDFFFF)V"
        )
    )
    private void NT$onRenderHitOutline(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape, double x, double y, double z, float r, float g, float b, float a, Operation<Void> vanilla, PoseStack arg1, VertexConsumer arg2, Entity arg3, double arg4, double arg5, double arg6, BlockPos arg7, BlockState state)
    {
        boolean isFullBlock = CandyTweak.OLD_BLOCK_OUTLINES.get().containsBlock(state.getBlock());
        VoxelShape box = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

        vanilla.call(poseStack, consumer, isFullBlock ? box : shape, x, y, z, r, g, b, a);
    }
}
