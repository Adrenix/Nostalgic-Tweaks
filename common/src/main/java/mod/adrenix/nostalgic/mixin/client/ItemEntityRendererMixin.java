package mod.adrenix.nostalgic.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.util.MixinInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.item.ItemEntity;
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
    @Shadow @Final private ItemRenderer itemRenderer;

    /**
     * Forces the item entity's rotation to always face the player.
     * Controlled by the old floating item toggle.
     */
    @Redirect(
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V"
        )
    )
    protected void rotationProxy(PoseStack poseStack, Quaternion quaternion, ItemEntity itemEntity, float entityYaw, float partialTicks)
    {
        if (MixinConfig.Candy.oldFloatingItems())
        {
            BakedModel model = this.itemRenderer.getModel(itemEntity.getItem(), null, null, 0);

            if (MixinInjector.Item.isModelFlat(model))
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180F - Minecraft.getInstance().gameRenderer.getMainCamera().getYRot()));
            else
                poseStack.mulPose(Vector3f.YP.rotation(itemEntity.getSpin(partialTicks)));
        }
        else
            poseStack.mulPose(Vector3f.YP.rotation(itemEntity.getSpin(partialTicks)));
    }

    /**
     * Renders floating item entities as 2D.
     * Controlled by the old floating items toggle.
     */
    @Inject(
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
        )
    )
    protected void onRender(ItemEntity itemEntity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo callback)
    {
        if (MixinConfig.Candy.oldFloatingItems())
        {
            BakedModel model = itemRenderer.getModel(itemEntity.getItem(), null, null, 0);
            if (MixinInjector.Item.isModelFlat(model))
            {
                MixinInjector.Item.flatten(poseStack);
                MixinInjector.Item.disableDiffusedLighting();
            }
        }
    }

    @Inject(
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
        )
    )
    protected void onFinishRender(ItemEntity itemEntity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo callback)
    {
        MixinInjector.Item.enableDiffusedLighting();
    }
}