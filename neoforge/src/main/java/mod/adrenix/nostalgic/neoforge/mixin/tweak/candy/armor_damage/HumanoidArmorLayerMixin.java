package mod.adrenix.nostalgic.neoforge.mixin.tweak.candy.armor_damage;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.helper.candy.ArmorHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
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
     * Changes the vertex consumer used by the armor model.
     */
    @ModifyExpressionValue(
        remap = false,
        method = "renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/Model;FFFLnet/minecraft/resources/ResourceLocation;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
        )
    )
    private VertexConsumer nt_neoforge_armor_damage$setRenderTypeConsumerForModel(VertexConsumer vertexConsumer, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, Model armorModel, float red, float green, float blue, ResourceLocation armorLocation)
    {
        return ArmorHelper.getDamagedConsumer(this.nt$entity, vertexConsumer, bufferSource, armorLocation);
    }

    /**
     * Changes the packed overlay used by the armor model to red while the entity is hurt.
     */
    @ModifyArg(
        remap = false,
        index = 3,
        method = "renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/Model;FFFLnet/minecraft/resources/ResourceLocation;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
        )
    )
    private int nt_neoforge_armor_damage$setOverlayTextureForModel(int packedOverlay)
    {
        return ArmorHelper.getDamagedPackedOverlay(this.nt$entity, packedOverlay);
    }

    /**
     * Changes the vertex consumer by the armor trim.
     */
    @ModifyExpressionValue(
        method = "renderTrim(Lnet/minecraft/core/Holder;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;wrap(Lcom/mojang/blaze3d/vertex/VertexConsumer;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
        )
    )
    private VertexConsumer nt_neoforge_armor_damage$setRenderTypeConsumerForTrim(VertexConsumer vertexConsumer, Holder<ArmorMaterial> armorMaterial, PoseStack poseStack, MultiBufferSource bufferSource, @Local TextureAtlasSprite trim)
    {
        if (ArmorHelper.useOldTint(this.nt$entity))
            return trim.wrap(ArmorHelper.getDamagedConsumer(this.nt$entity, vertexConsumer, bufferSource, trim.atlasLocation()));

        return vertexConsumer;
    }

    /**
     * Changes the packed overlay used by the armor trim model to red while the entity is hurt.
     */
    @ModifyArg(
        index = 3,
        method = "renderTrim(Lnet/minecraft/core/Holder;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
        )
    )
    private int nt_neoforge_armor_damage$setOverlayTextureForTrim(int packedOverlay)
    {
        return ArmorHelper.getDamagedPackedOverlay(this.nt$entity, packedOverlay);
    }
}
