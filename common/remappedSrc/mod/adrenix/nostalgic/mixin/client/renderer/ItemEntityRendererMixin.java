package mod.adrenix.nostalgic.mixin.client.renderer;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin
{
    /* Shadows */

    @Shadow @Final private ItemRenderer itemRenderer;

    /**
     * Forces the item entity's rotation to always face the player.
     * Controlled by the old floating item tweak.
     */
    @Redirect
    (
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At
        (
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V"
        )
    )
    private void NT$rotationProxy(MatrixStack poseStack, Quaternion quaternion, ItemEntity itemEntity, float entityYaw, float partialTicks)
    {
        if (ModConfig.Candy.oldFloatingItems())
        {
            BakedModel model = this.itemRenderer.getModel(itemEntity.getStack(), null, null, 0);

            if (ModClientUtil.Item.isModelFlat(model))
                poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180F - MinecraftClient.getInstance().gameRenderer.getCamera().getYaw()));
            else
                poseStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(itemEntity.getRotation(partialTicks)));
        }
        else
            poseStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(itemEntity.getRotation(partialTicks)));
    }

    /**
     * Renders floating item entities as 2D.
     * Controlled by the old floating items tweak.
     */
    @Inject
    (
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At
        (
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
        )
    )
    private void NT$onRender(ItemEntity itemEntity, float entityYaw, float partialTicks, MatrixStack poseStack, VertexConsumerProvider buffer, int combinedLight, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldFloatingItems())
        {
            BakedModel model = itemRenderer.getModel(itemEntity.getStack(), null, null, 0);
            if (ModClientUtil.Item.isModelFlat(model))
            {
                ModClientUtil.Item.flatten(poseStack);
                ModClientUtil.Item.disableDiffusedLighting();
            }
        }
    }

    /**
     * Enables diffused lighting after it has been disabled before rendering the item entity.
     * Not controlled by any tweak.
     */
    @Inject
    (
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At
        (
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
        )
    )
    private void NT$onFinishRender(ItemEntity itemEntity, float entityYaw, float partialTicks, MatrixStack poseStack, VertexConsumerProvider buffer, int combinedLight, CallbackInfo callback)
    {
        ModClientUtil.Item.enableDiffusedLighting();
    }
}