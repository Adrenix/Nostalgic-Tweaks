package mod.adrenix.nostalgic.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerFabricMixin <T extends LivingEntity, A extends HumanoidModel<T>>
{
    /* Shadows & Unique */

    @Shadow protected abstract ResourceLocation getArmorLocation(ArmorItem armorItem, boolean isLayered, @Nullable String location);
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
    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
    private void NT$onRenderModel(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ArmorItem armorItem, A model, boolean isLayered, float r, float g, float b, @Nullable String location, CallbackInfo callback)
    {
        boolean isHurt = ModConfig.Candy.oldDamageArmorTint() && (this.NT$armorWearer.hurtTime > 0 || this.NT$armorWearer.deathTime > 0);

        if (!isHurt)
            return;

        ResourceLocation armorLocation = this.getArmorLocation(armorItem, isLayered, location);
        VertexConsumer modConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(armorLocation));
        int packedOverlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(true));

        model.renderToBuffer(poseStack, modConsumer, packedLight, packedOverlay, r, g, b, 1.0F);
        callback.cancel();
    }
}
