package mod.adrenix.nostalgic.mixin.tweak.candy.armor_damage;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.helper.candy.ArmorHelper;
import mod.adrenix.nostalgic.mixin.duck.ArmorLayerState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin implements ArmorLayerState
{
    /**
     * Changes the vertex consumer used by the armor model.
     */
    @ModifyExpressionValue(
        method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getArmorFoilBuffer(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;Z)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
        )
    )
    private VertexConsumer nt_armor_damage$setRenderTypeConsumerForModel(VertexConsumer vertexConsumer, @Local(argsOnly = true) MultiBufferSource bufferSource, @Local(ordinal = 1) ResourceLocation armorLocation)
    {
        return ArmorHelper.getDamagedConsumer(this.nt$getRenderState(), vertexConsumer, bufferSource, armorLocation);
    }

    /**
     * Changes the packed overlay used by the armor model to red while the entity is hurt.
     */
    @ModifyArg(
        index = 3,
        method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"
        )
    )
    private int nt_armor_damage$setOverlayTextureForModel(int packedOverlay)
    {
        return ArmorHelper.getDamagedPackedOverlay(this.nt$getRenderState(), packedOverlay);
    }

    /**
     * Changes the vertex consumer by the armor trim.
     */
    @ModifyExpressionValue(
        method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;wrap(Lcom/mojang/blaze3d/vertex/VertexConsumer;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
        )
    )
    private VertexConsumer nt_armor_damage$setRenderTypeConsumerForTrim(VertexConsumer vertexConsumer, @Local(argsOnly = true) MultiBufferSource bufferSource, @Local TextureAtlasSprite trim)
    {
        if (ArmorHelper.useOldTint(this.nt$getRenderState()))
            return trim.wrap(ArmorHelper.getDamagedConsumer(this.nt$getRenderState(), vertexConsumer, bufferSource, trim.atlasLocation()));

        return vertexConsumer;
    }

    /**
     * Changes the packed overlay used by the armor trim model to red while the entity is hurt.
     */
    @ModifyArg(
        index = 3,
        method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"
        )
    )
    private int nt_armor_damage$setOverlayTextureForTrim(int packedOverlay)
    {
        return ArmorHelper.getDamagedPackedOverlay(this.nt$getRenderState(), packedOverlay);
    }
}
