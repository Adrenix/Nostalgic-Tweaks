package mod.adrenix.nostalgic.forge.mixin.tweak.candy.armor_damage;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.mixin.util.candy.ArmorMixinHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity>
{
    /* Unique */

    @Unique private T nt$entity;

    /* Injections */

    /**
     * Tracks the entity wearing armor.
     */
    @Inject(
        method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
        at = @At("HEAD")
    )
    private void nt_neoforge_armor_damage$onRenderHead(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callback)
    {
        this.nt$entity = entity;
    }

    /**
     * Clears entity tracking field.
     */
    @Inject(
        method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
        at = @At("RETURN")
    )
    private void nt_neoforge_armor_damage$onRenderReturn(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callback)
    {
        this.nt$entity = null;
    }

    /**
     * Changes the vertex consumer used by the armor renderer.
     */
    @ModifyExpressionValue(
        remap = false,
        method = "renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;Lnet/minecraft/client/model/Model;ZFFFLnet/minecraft/resources/ResourceLocation;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
        )
    )
    private VertexConsumer nt_neoforge_armor_damage$setRenderTypeConsumer(VertexConsumer original, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ArmorItem armorItem, Model model, boolean withGlint, float red, float green, float blue, ResourceLocation armorLocation)
    {
        return ArmorMixinHelper.getDamagedConsumer(this.nt$entity, original, bufferSource, armorLocation);
    }

    /**
     * Changes the packed overlay to red while the entity is hurt.
     */
    @ModifyArg(
        remap = false,
        index = 3,
        method = "renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;Lnet/minecraft/client/model/Model;ZFFFLnet/minecraft/resources/ResourceLocation;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
        )
    )
    private int nt_neoforge_armor_damage$setOverlayTexture(int packedOverlay)
    {
        return ArmorMixinHelper.getDamagedPackedOverlay(this.nt$entity, packedOverlay);
    }
}
