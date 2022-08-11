package mod.adrenix.nostalgic.mixin.client.renderer;

import mod.adrenix.nostalgic.client.config.SwingConfig;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.mixin.duck.IReequipSlot;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class ItemInHandRendererMixin
{
    /* Shadows */

    @Shadow private float mainHandHeight;
    @Shadow private ItemStack mainHandItem;
    @Shadow private ItemStack offHandItem;
    @Shadow @Final private MinecraftClient minecraft;

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
    private void NT$armSwayXP(MatrixStack poseStack, Quaternion q, float partialTicks, MatrixStack ps2, VertexConsumerProvider.Immediate b, ClientPlayerEntity player)
    {
        if (ModConfig.Animation.oldArmSway())
            return;

        float intensity = ModConfig.Animation.getArmSwayIntensity();
        float xBobInterpolate = MathHelper.lerp(partialTicks, player.lastRenderPitch, player.renderPitch);
        poseStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(((player.getPitch(partialTicks) - xBobInterpolate) * 0.1F) * intensity));
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
    private void NT$armSwayYP(MatrixStack poseStack, Quaternion q, float partialTicks, MatrixStack ps2, VertexConsumerProvider.Immediate b, ClientPlayerEntity player)
    {
        if (ModConfig.Animation.oldArmSway())
            return;

        float intensity = ModConfig.Animation.getArmSwayIntensity();
        float yBobInterpolate = MathHelper.lerp(partialTicks, player.lastRenderYaw, player.renderYaw);
        poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(((player.getYaw(partialTicks) - yBobInterpolate) * 0.1F) * intensity));
    }

    /**
     * Prevents visual bug from flashing the previously held item when pulling an item out of the main hand.
     * Controlled by reequip tweak.
     */
    @ModifyArg
    (
        method = "renderHandsWithItems",
        index = 5,
        at = @At
        (
            value = "INVOKE",
            ordinal = 0,
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
        )
    )
    private ItemStack NT$onRenderItem(AbstractClientPlayerEntity player, float partialTicks, float pitch, Hand hand, float swingProgress, ItemStack itemStack, float equippedProgress, MatrixStack matrix, VertexConsumerProvider buffer, int combinedLight)
    {
        return ModClientUtil.Item.getLastItem(itemStack, this.mainHandItem, player.getMainHandStack(), (IReequipSlot) player);
    }

    /**
     * Forces the attack strength to be 1.0F when cooldown animation is disabled.
     * Controlled by the old cooldown tweak.
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float NT$onGetStrength(ClientPlayerEntity player, float partialTick)
    {
        return ModConfig.Animation.oldItemCooldown() ? 1.0F : player.getAttackCooldownProgress(partialTick);
    }

    /**
     * Prevents the off-hand from reequipping when only the stack size changes.
     * Controlled by reequipping tweak.
     */
    @ModifyArg(method = "tick", index = 0, at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float NT$onOffHandTick(float current)
    {
        ClientPlayerEntity player = this.minecraft.player;
        if (!ModConfig.Animation.oldItemReequip() || player == null)
            return current;

        ItemStack offStack = player.getOffHandStack();

        if (this.offHandItem.isOf(offStack.getItem()) && this.offHandItem.getCount() != offStack.getCount())
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
        return ModConfig.Animation.oldItemReequip() ? 0.0F : MathHelper.clamp(current, min, max);
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
        return !ModConfig.Animation.oldItemReequip() && ItemStack.areEqual(from, to);
    }

    /**
     * Simulate old reequip logic and animation.
     * Controlled by reequip and cooldown animation tweaks.
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private void NT$onTick(CallbackInfo callback)
    {
        ClientPlayerEntity player = this.minecraft.player;
        if (!ModConfig.Animation.oldItemReequip() || player == null)
            return;

        IReequipSlot injector = (IReequipSlot) player;
        ItemStack main = player.getMainHandStack();
        int slot = player.getInventory().selectedSlot;

        if (main.isEmpty() && this.mainHandItem.isEmpty() && !injector.NT$getReequip())
            injector.NT$setReequip(false);
        else if (slot != injector.NT$getLastSlot())
        {
            injector.NT$setLastSlot(slot);
            injector.NT$setReequip(true);
        }

        // Needed to fix weird bug that occurs when pulling an item out from the main hand while in an inventory.
        boolean isUnequipped = this.mainHandItem.toString().equals("0 air") && main.toString().equals("1 air");
        boolean isItemChanged = !this.mainHandItem.isOf(main.getItem());
        boolean isSlotUpdated = slot == injector.NT$getLastSlot() && isItemChanged && !injector.NT$getReequip();
        boolean isHandChanged = isUnequipped && !injector.NT$getLastItem().isEmpty() && !injector.NT$getReequip();

        if (isSlotUpdated || isHandChanged)
            injector.NT$setReequip(true);

        if (isUnequipped)
            this.mainHandItem = injector.NT$getLastItem();

        if (slot == injector.NT$getLastSlot() && !injector.NT$getReequip())
            this.mainHandItem = player.getMainHandStack();

        if (ModConfig.Animation.oldItemCooldown())
            this.mainHandHeight = MathHelper.clamp(this.mainHandHeight + (injector.NT$getReequip() ? -0.4F : 0.4F), 0.0F, 1.0F);
        else
        {
            float scale = player.getAttackCooldownProgress(1.0F);
            this.mainHandHeight += MathHelper.clamp((!injector.NT$getReequip() ? scale * scale * scale : 0.0f) - this.mainHandHeight, -0.4F, 0.4F);
        }

        if (this.mainHandHeight < 0.1F)
            injector.NT$setReequip(false);
    }

    /**
     * Enhances photosensitivity mode by completely disabling any hand movement when placing or interacting.
     * Only checks for global photosensitivity since this will break reequip animations if checking by item.
     */
    @Inject(method = "applyItemArmTransform", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onApplyItemArmTransform(MatrixStack poseStack, Arm arm, float equippedProgress, CallbackInfo callback)
    {
        if (!ModConfig.isModEnabled())
            return;

        equippedProgress = SwingConfig.getGlobalSpeed() == DefaultConfig.Swing.PHOTOSENSITIVE ? 0 : equippedProgress;
        int i = arm == Arm.RIGHT ? 1 : -1;

        poseStack.translate((float) i * 0.56F, -0.52F + equippedProgress * -0.6F, -0.72F);
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
    private void NT$onRenderItem(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Mode transformType, boolean leftHand, MatrixStack poseStack, VertexConsumerProvider buffer, int combinedLight, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldItemHolding() && !(itemStack.getItem() instanceof BlockItem))
        {
            poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((leftHand ? -1 : 1) * 5F));
            poseStack.translate(-0.01F, -0.01F, -0.015F);
        }
    }

    /**
     * Simulates the old swinging animations.
     * Controlled by the old swing tweak.
     */
    @Inject(method = "applyItemArmAttackTransform", at = @At(value = "HEAD"))
    private void NT$onApplyItemArmAttackTransform(MatrixStack poseStack, Arm hand, float swingProgress, CallbackInfo callback)
    {
        if (ModConfig.Animation.oldSwing())
        {
            float progress = MathHelper.sin((float) Math.PI * swingProgress);
            float scale = 1.0F - (0.3F * progress);
            poseStack.translate(-0.12F * progress, 0.085F * progress, 0.0F);
            poseStack.scale(scale, scale, scale);
        }
    }
}
