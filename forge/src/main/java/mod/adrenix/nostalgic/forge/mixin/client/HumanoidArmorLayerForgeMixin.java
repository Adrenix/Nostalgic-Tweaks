package mod.adrenix.nostalgic.forge.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerForgeMixin <T extends LivingEntity>
{
    /* Shadows & Unique */

    @Unique private LivingEntity NT$armorWearer;

    /* Injections */

    /**
     * Tracks the current living entity wearing this armor piece.
     */
    @Inject(at = @At("HEAD"), method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V")
    private void NT$onStartRender(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callback)
    {
        this.NT$armorWearer = livingEntity;
    }

    /**
     * Removes tracking information.
     */
    @Inject(at = @At("RETURN"), method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V")
    private void NT$onFinishRender(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callback)
    {
        this.NT$armorWearer = null;
    }

    /**
     * Changes the rendering of the armor piece depending on whether the tracked entity wearing this armor is hurt or dead.
     * Controlled by the old damage armor tint tweak.
     */
    @Inject(at = @At("HEAD"), remap = false, cancellable = true, method = "renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IZLnet/minecraft/client/model/Model;FFFLnet/minecraft/resources/ResourceLocation;)V")
    private void NT$onRenderModel(PoseStack poseStack, MultiBufferSource buffer, int packedLight, boolean hasFoil, Model model, float r, float g, float b, ResourceLocation location, CallbackInfo callback)
    {
        boolean isHurt = ModConfig.Candy.oldDamageArmorTint() && (this.NT$armorWearer.hurtTime > 0 || this.NT$armorWearer.deathTime > 0);

        if (!isHurt)
            return;

        VertexConsumer modConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(location));
        int packedOverlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(true));

        model.renderToBuffer(poseStack, modConsumer, packedLight, packedOverlay, r, g, b, 1.0F);
        callback.cancel();
    }
}
