package mod.adrenix.nostalgic.mixin.tweak.candy.custom_outline_color;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    /**
     * Changes the voxel shape color used by the hit outline renderer.
     */
    @WrapOperation(
        method = "renderHitOutline",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDFFFF)V"
        )
    )
    private void NT$onCustomOutlineColor(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape, double x, double y, double z, float red, float green, float blue, float alpha, Operation<Void> vanilla)
    {
        float[] rgba = HexUtil.parseFloatRGBA(CandyTweak.CUSTOM_OUTLINE_COLOR.get());
        float r = rgba[0];
        float g = rgba[1];
        float b = rgba[2];
        float a = rgba[3];
        
        vanilla.call(poseStack, consumer, shape, x, y, z, r, g, b, a);
    }
}
