package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_player;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.mixin.util.gameplay.combat.SwordBlockMixinHelper;
import mod.adrenix.nostalgic.mixin.util.gameplay.combat.SwordBlockRenderer;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin
{
    /**
     * Applies pre-first-person view sword blocking animation if applicable.
     */
    @Inject(
        method = "renderArmWithItem",
        at = @At(
            ordinal = 1,
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
        )
    )
    private void nt_combat_player$applyPreSwordBlockingAnimation(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack itemStack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo callback)
    {
        if (ModTweak.ENABLED.get())
            poseStack.pushPose();

        if (SwordBlockMixinHelper.isBlocking(player) || SwordBlockMixinHelper.shouldBlockOnShield(player))
            SwordBlockRenderer.applyFirstPerson(poseStack, player, hand);
    }

    /**
     * Applies post-first-person view sword blocking animation if applicable.
     */
    @Inject(
        method = "renderArmWithItem",
        at = @At(
            ordinal = 1,
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
        )
    )
    private void nt_combat_player$applyPostSwordBlockingAnimation(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack itemStack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo callback)
    {
        if (ModTweak.ENABLED.get())
            poseStack.popPose();
    }

    /**
     * Hides the shield item in the off-hand while the player is sword blocking with the main hand.
     */
    @WrapWithCondition(
        method = "renderHandsWithItems",
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
        )
    )
    private boolean nt_combat_player$shouldRenderShieldInOffHand(ItemInHandRenderer renderer, AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight)
    {
        return !SwordBlockMixinHelper.shouldBlockOnShield(player);
    }

    /**
     * Allows the item renderer to swing the sword if it is currently blocking.
     */
    @ModifyExpressionValue(
        method = "renderArmWithItem",
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/AbstractClientPlayer;isUsingItem()Z"
        )
    )
    private boolean nt_combat_player$shouldIndicateItemUse(boolean isUsingItem, @Local(argsOnly = true) AbstractClientPlayer player)
    {
        if (SwordBlockMixinHelper.isBlocking(player))
            return false;

        return isUsingItem;
    }
}
