package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import mod.adrenix.nostalgic.client.config.SwingConfig;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.mixin.duck.SlotTracker;
import mod.adrenix.nostalgic.util.client.AnimationUtil;
import mod.adrenix.nostalgic.util.client.ItemClientUtil;
import mod.adrenix.nostalgic.util.client.SwingType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin
{
    /* Shadows */

    @Shadow @Final private Minecraft minecraft;
    @Shadow private float mainHandHeight;
    @Shadow private ItemStack mainHandItem;
    @Shadow private ItemStack offHandItem;
    @Shadow protected abstract void applyItemArmAttackTransform(PoseStack matrixStack, HumanoidArm hand, float swingProgress);

    /**
     * Blocks the rotation of the hand renderer on the x-axis when arm sway is disabled.
     * Controlled by the old arm sway tweak.
     */
    @Redirect
    (
        method = "renderHandsWithItems",
        at = @At
        (
            value = "INVOKE",
            ordinal = 0,
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V"
        )
    )
    private void NT$armSwayXP(PoseStack poseStack, Quaternion q, float partialTicks, PoseStack ps2, MultiBufferSource.BufferSource b, LocalPlayer player)
    {
        if (ModConfig.Animation.oldArmSway())
            return;

        float intensity = ModConfig.Animation.getArmSwayIntensity();
        float xBobInterpolate = Mth.lerp(partialTicks, player.xBobO, player.xBob);

        poseStack.mulPose(Vector3f.XP.rotationDegrees(((player.getViewXRot(partialTicks) - xBobInterpolate) * 0.1F) * intensity));
    }

    /**
     * Blocks the rotation of the hand renderer on the y-axis when arm sway is disabled.
     * Controlled by the old arm sway tweak.
     */
    @Redirect
    (
        method = "renderHandsWithItems",
        at = @At
        (
            value = "INVOKE",
            ordinal = 1,
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V"
        )
    )
    private void NT$armSwayYP(PoseStack poseStack, Quaternion q, float partialTicks, PoseStack ps2, MultiBufferSource.BufferSource b, LocalPlayer player)
    {
        if (ModConfig.Animation.oldArmSway())
            return;

        float intensity = ModConfig.Animation.getArmSwayIntensity();
        float yBobInterpolate = Mth.lerp(partialTicks, player.yBobO, player.yBob);

        poseStack.mulPose(Vector3f.YP.rotationDegrees(((player.getViewYRot(partialTicks) - yBobInterpolate) * 0.1F) * intensity));
    }

    /**
     * Prevents visual bug from flashing the previously held item when pulling an item out of the main hand.
     * Controlled by reequip tweak.
     */
    @ModifyArg
    (
        method = "renderHandsWithItems",
        at = @At
        (
            value = "INVOKE",
            ordinal = 0,
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
        )
    )
    private ItemStack NT$onRenderItem(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack itemStack, float equippedProgress, PoseStack matrix, MultiBufferSource buffer, int combinedLight)
    {
        return ItemClientUtil.getLastItem(itemStack, this.mainHandItem, player.getMainHandItem(), (SlotTracker) player);
    }

    /**
     * Forces the attack strength to be 1.0F when cooldown animation is disabled.
     * Controlled by the old cooldown tweak.
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float NT$onGetStrength(LocalPlayer player, float partialTick)
    {
        return ModConfig.Animation.oldItemCooldown() ? 1.0F : player.getAttackStrengthScale(partialTick);
    }

    /**
     * Prevents the off-hand from reequipping when only the stack size changes.
     * Controlled by reequipping tweak.
     */
    @ModifyArg(method = "tick", index = 0, at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float NT$onOffHandTick(float current)
    {
        LocalPlayer player = this.minecraft.player;

        if (!ModConfig.Animation.oldItemReequip() || player == null)
            return current;

        ItemStack offStack = player.getOffhandItem();

        if (this.offHandItem.is(offStack.getItem()) && this.offHandItem.getCount() != offStack.getCount())
            return 0.0F;

        return current;
    }

    /**
     * Blocks the addition assignment operator from incrementing the hand height when unsolicited reequipping is disabled.
     * Controlled by reequip tweak.
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float NT$onTickIncreaseMain(float current, float min, float max)
    {
        return ModConfig.Animation.oldItemReequip() ? 0.0F : Mth.clamp(current, min, max);
    }

    /**
     * Forces the item matching to return false on the main hand, so we can track what the last held item was.
     * This prevents reequip animation issues when going from an item in the main hand to air.
     *
     * Controlled by old reequip logic tweak.
     */
    @Redirect
    (
        method = "tick",
        at = @At
        (
            value = "INVOKE",
            ordinal = 0,
            target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
        )
    )
    private boolean NT$onMainItemTick(ItemStack from, ItemStack to)
    {
        return !ModConfig.Animation.oldItemReequip() && ItemStack.matches(from, to);
    }

    /**
     * Simulate old reequip logic and animation.
     * Controlled by reequip and cooldown animation tweaks.
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private void NT$onTick(CallbackInfo callback)
    {
        LocalPlayer player = this.minecraft.player;
        if (!ModConfig.Animation.oldItemReequip() || player == null)
            return;

        SlotTracker injector = (SlotTracker) player;
        ItemStack main = player.getMainHandItem();
        int slot = player.getInventory().selected;

        if (main.isEmpty() && this.mainHandItem.isEmpty() && !injector.NT$getReequip())
            injector.NT$setReequip(false);
        else if (slot != injector.NT$getLastSlot())
        {
            injector.NT$setLastSlot(slot);
            injector.NT$setReequip(true);
        }

        // Needed to fix weird bug that occurs when pulling an item out from the main hand while in an inventory.
        boolean isUnequipped = this.mainHandItem.toString().equals("0 air") && main.toString().equals("1 air");
        boolean isItemChanged = !this.mainHandItem.is(main.getItem());
        boolean isSlotUpdated = slot == injector.NT$getLastSlot() && isItemChanged && !injector.NT$getReequip();
        boolean isHandChanged = isUnequipped && !injector.NT$getLastItem().isEmpty() && !injector.NT$getReequip();

        if (isSlotUpdated || isHandChanged)
            injector.NT$setReequip(true);

        if (isUnequipped)
            this.mainHandItem = injector.NT$getLastItem();

        if (slot == injector.NT$getLastSlot() && !injector.NT$getReequip())
            this.mainHandItem = player.getMainHandItem();

        if (ModConfig.Animation.oldItemCooldown())
            this.mainHandHeight = Mth.clamp(this.mainHandHeight + (injector.NT$getReequip() ? -0.4F : 0.4F), 0.0F, 1.0F);
        else
        {
            float scale = player.getAttackStrengthScale(1.0F);
            this.mainHandHeight += Mth.clamp((!injector.NT$getReequip() ? scale * scale * scale : 0.0f) - this.mainHandHeight, -0.4F, 0.4F);
        }

        if (this.mainHandHeight < 0.1F)
            injector.NT$setReequip(false);
    }

    /**
     * Enhances photosensitivity mode by completely disabling any hand movement when placing or interacting.
     * Only checks for global photosensitivity since this will break reequip animations if checking by item.
     */
    @Inject(method = "applyItemArmTransform", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onApplyItemArmTransform(PoseStack poseStack, HumanoidArm arm, float equippedProgress, CallbackInfo callback)
    {
        if (!ModConfig.isModEnabled())
            return;

        equippedProgress = SwingConfig.getGlobalSpeed() == DefaultConfig.Swing.PHOTOSENSITIVE ? 0 : equippedProgress;
        int flip = arm == HumanoidArm.RIGHT ? 1 : -1;

        poseStack.translate((float) flip * 0.56F, -0.52F + equippedProgress * -0.6F, -0.72F);
        callback.cancel();
    }

    /**
     * Simulates old item holding positions.
     * Controlled by the old item holding tweak.
     */
    @Inject
    (
        method = "renderItem",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V"
        )
    )
    private void NT$onRenderItem(LivingEntity entity, ItemStack itemStack, ItemTransforms.TransformType transformType, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo callback)
    {
        boolean isDisabled = ModConfig.Candy.getIgnoredItemHoldings().isItemInList(itemStack.getItem());
        boolean isBlockItem = itemStack.getItem() instanceof BlockItem;
        boolean isUsingItem = itemStack == entity.getUseItem() && entity.isUsingItem() && entity.getUseItemRemainingTicks() > 0;

        if (ModConfig.Candy.oldItemHolding() && !isDisabled && !isBlockItem && !isUsingItem)
        {
            poseStack.mulPose(Vector3f.YP.rotationDegrees((leftHand ? -1 : 1) * 5.0F));
            poseStack.translate(-0.01F, -0.01F, -0.015F);
        }
    }

    /**
     * Simulates the old swinging animations.
     * Controlled by the old swing tweak.
     */
    @Inject(method = "applyItemArmAttackTransform", at = @At(value = "HEAD"))
    private void NT$onApplyItemArmAttackTransform(PoseStack poseStack, HumanoidArm hand, float swingProgress, CallbackInfo callback)
    {
        if (ModConfig.Animation.oldSwing())
        {
            float progress = Mth.sin((float) Math.PI * swingProgress);
            float scale = 1.0F - (0.3F * progress);

            poseStack.translate(-0.12F * progress, 0.085F * progress, 0.0F);
            poseStack.scale(scale, scale, scale);
        }
    }

    /**
     * Changes the arm with item rendering instructions.
     * Controlled by the old classic swing tweak is enabled.
     */
    @Redirect(method = "renderArmWithItem", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmAttackTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V"))
    private void NT$onRenderArmWithItem(ItemInHandRenderer instance, PoseStack poseStack, HumanoidArm hand, float swingProgress, AbstractClientPlayer player, float partialTick, float pitch, InteractionHand hand2, float swingProgress2, ItemStack stack, float equippedProgress)
    {
        if (ModConfig.Animation.oldClassicSwing())
        {
            poseStack.popPose();
            poseStack.pushPose();

            float flip = hand == HumanoidArm.RIGHT ? 1.0F : -1.0F;
            float rotateProgress = Mth.sin(Mth.sqrt(swingProgress) * (float) Math.PI);

            poseStack.translate(flip * 0.56F, -0.52F + equippedProgress * -0.6F, -0.72F);

            if (AnimationUtil.swingType == SwingType.LEFT_CLICK)
            {
                float x = -0.4F * Mth.sin(Mth.sqrt(swingProgress) * (float) Math.PI);
                float y = 0.2F * Mth.sin(Mth.sqrt(swingProgress) * ((float) Math.PI * 2));
                float z = -0.2F * Mth.sin(swingProgress * (float) Math.PI);

                poseStack.translate(flip * x, y, z);
            }
            else
                poseStack.translate(0.0F, rotateProgress * -0.15F, 0.0F);
        }
        else
            this.applyItemArmAttackTransform(poseStack, hand, swingProgress);
    }
}
