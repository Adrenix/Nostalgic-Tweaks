package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.ItemClientUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownItemRenderer.class)
public abstract class ThrownItemRendererMixin<T extends Entity>
{
    /**
     * Makes thrown items, like snowballs, render in 2D. Controlled by the old 2D throwing tweak.
     */
    @Inject(
        method = "render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;IILcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;I)V"
        )
    )
    private void NT$onRenderStart(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldFlatThrowing())
        {
            ItemClientUtil.flatten(poseStack);
            ItemClientUtil.disableDiffusedLighting();
        }
    }

    /**
     * Disables the diffused lighting before item rendering and re-enables after the rendering process is complete.
     * Controlled by the old 2D throwing tweak.
     */
    @Inject(
        method = "render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;IILcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;I)V"
        )
    )
    private void NT$onRenderFinish(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo callback)
    {
        ItemClientUtil.enableDiffusedLighting();
    }
}
