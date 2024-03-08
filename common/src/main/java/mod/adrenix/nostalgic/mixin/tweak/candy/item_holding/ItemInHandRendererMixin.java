package mod.adrenix.nostalgic.mixin.tweak.candy.item_holding;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin
{
    /**
     * Translate and rotate the item holding position to the old position.
     */
    @Inject(
        method = "renderItem",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V"
        )
    )
    private void nt_item_holding$onRenderItem(LivingEntity entity, ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int seed, CallbackInfo callback, @Share("useOldHolding") LocalBooleanRef useOldHoldingRef)
    {
        boolean isDisabled = CandyTweak.IGNORED_HOLDING_ITEMS.get().containsItem(itemStack);
        boolean isBlockItem = itemStack.getItem() instanceof BlockItem;
        boolean isUsingItem = itemStack.equals(entity.getUseItem()) && entity.isUsingItem() && entity.getUseItemRemainingTicks() > 0;

        if (!CandyTweak.OLD_ITEM_HOLDING.get() || isDisabled || isBlockItem || isUsingItem)
            return;

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees((leftHand ? -1 : 1) * 5.0F));
        poseStack.translate(-0.01F, -0.01F, -0.015F);
        useOldHoldingRef.set(true);
    }

    /**
     * Pop the translation and rotation from the pose stack if using the old item holding position.
     */
    @Inject(
        method = "renderItem",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V"
        )
    )
    private void nt_item_holding$onFinishRenderItem(LivingEntity entity, ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int seed, CallbackInfo callback, @Share("useOldHolding") LocalBooleanRef useOldHoldingRef)
    {
        if (useOldHoldingRef.get())
            poseStack.popPose();
    }
}
