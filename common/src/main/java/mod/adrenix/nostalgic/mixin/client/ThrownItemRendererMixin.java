package mod.adrenix.nostalgic.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.util.MixinInjector;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownItemRenderer.class)
public abstract class ThrownItemRendererMixin
{
    /**
     * Makes thrown items, like snowballs, render in 2D.
     * Controlled by the old 2D throwing toggle.
     */
    @Inject(method = "render", at = @At(shift = At.Shift.AFTER, ordinal = 1, value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V"))
    protected <T extends Entity> void onRenderScale(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo callback)
    {
        if (MixinConfig.Candy.oldFlatThrowing())
        {
            MixinInjector.Item.flatten(poseStack);
            MixinInjector.Item.disableDiffusedLighting();
        }
    }

    /**
     * Disables the diffused lighting before item rendering and re-enables after the rendering process is complete.
     * Controlled by the old 2D throwing toggle.
     */
    @Inject(method = "render", at = @At(shift = At.Shift.AFTER, value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;IILcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    protected <T extends Entity> void onRenderFinish(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo callback)
    {
        MixinInjector.Item.enableDiffusedLighting();
    }
}
