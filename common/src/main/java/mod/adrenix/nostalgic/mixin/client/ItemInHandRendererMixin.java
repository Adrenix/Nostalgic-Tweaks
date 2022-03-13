package mod.adrenix.nostalgic.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.client.config.DefaultConfig;
import mod.adrenix.nostalgic.mixin.duck.IReequipSlot;
import mod.adrenix.nostalgic.util.MixinInjector;
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
    @Shadow private float mainHandHeight;
    @Shadow private ItemStack mainHandItem;
    @Shadow private ItemStack offHandItem;
    @Shadow @Final private Minecraft minecraft;

    /**
     * Blocks the rotation of the hand renderer on the x-axis when arm sway is disabled.
     * Controlled by the old arm sway toggle.
     */
    @Redirect(method = "renderHandsWithItems", at = @At(value = "INVOKE", ordinal = 0, target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V"))
    protected void armSwayXP(PoseStack poseStack, Quaternion q, float partialTicks, PoseStack ps2, MultiBufferSource.BufferSource b, LocalPlayer player)
    {
        if (MixinConfig.Animation.oldArmSway())
            return;

        float intensity = MixinConfig.Animation.getArmSwayIntensity();
        float xBobInterpolate = Mth.lerp(partialTicks, player.xBobO, player.xBob);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(((player.getViewXRot(partialTicks) - xBobInterpolate) * 0.1F) * intensity));
    }

    /**
     * Blocks the rotation of the hand renderer on the y-axis when arm sway is disabled.
     * Controlled by the old arm sway toggle.
     */
    @Redirect(method = "renderHandsWithItems", at = @At(value = "INVOKE", ordinal = 1, target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V"))
    protected void armSwayYP(PoseStack poseStack, Quaternion q, float partialTicks, PoseStack ps2, MultiBufferSource.BufferSource b, LocalPlayer player)
    {
        if (MixinConfig.Animation.oldArmSway())
            return;

        float intensity = MixinConfig.Animation.getArmSwayIntensity();
        float yBobInterpolate = Mth.lerp(partialTicks, player.yBobO, player.yBob);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(((player.getViewYRot(partialTicks) - yBobInterpolate) * 0.1F) * intensity));
    }

    /**
     * Prevents visual bug from flashing the previously held item when pulling an item out of the main hand.
     * Controlled by reequip toggle.
     */
    @ModifyArg(method = "renderHandsWithItems", index = 5, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    protected ItemStack onRenderItem(AbstractClientPlayer player, float f1, float f2, InteractionHand hand, float f3, ItemStack itemStack, float f4, PoseStack matrix, MultiBufferSource buffer, int i)
    {
        return MixinInjector.Item.getLastItem(itemStack, this.mainHandItem, player.getMainHandItem(), (IReequipSlot) player);
    }

    /**
     * Forces the attack strength to be 1.0F when cooldown animation is disabled.
     * Controlled by the old cooldown toggle.
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    protected float onGetStrength(LocalPlayer player, float partialTick)
    {
        return MixinConfig.Animation.oldItemCooldown() ? 1.0F : player.getAttackStrengthScale(partialTick);
    }

    /**
     * Prevents the off-hand from reequipping when only the stack size changes.
     * Controlled by reequipping toggle.
     */
    @ModifyArg(method = "tick", index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F", ordinal = 3))
    protected float onOffHandTick(float current)
    {
        LocalPlayer player = this.minecraft.player;
        if (!MixinConfig.Animation.oldItemReequip() || player == null)
            return current;

        ItemStack offStack = player.getOffhandItem();

        if (this.offHandItem.is(offStack.getItem()) && this.offHandItem.getCount() != offStack.getCount())
            return 0.0F;
        return current;
    }

    /**
     * Blocks the addition assignment operator from incrementing the hand height when unsolicited reequipping is disabled.
     * Controlled by reequip toggle.
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F", ordinal = 2))
    protected float onTickIncreaseMain(float current, float min, float max)
    {
        return MixinConfig.Animation.oldItemReequip() ? 0.0F : Mth.clamp(current, min, max);
    }

    /**
     * Forces the item matching to return false on the main hand, so we can track what the last held item was.
     * This prevents reequip animation issues when going from an item in the main hand to air.
     * Controlled by reequip toggle.
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    protected boolean onMainItemTick(ItemStack from, ItemStack to)
    {
        return !MixinConfig.Animation.oldItemReequip() && ItemStack.matches(from, to);
    }

    /**
     * Simulate old reequip logic and animation.
     * Controlled by reequip and cooldown animation toggles.
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F", ordinal = 3))
    protected void onTick(CallbackInfo callback)
    {
        LocalPlayer player = this.minecraft.player;
        if (!MixinConfig.Animation.oldItemReequip() || player == null)
            return;

        IReequipSlot injector = (IReequipSlot) player;
        ItemStack main = player.getMainHandItem();
        int slot = player.getInventory().selected;

        if (main.isEmpty() && this.mainHandItem.isEmpty() && !injector.getReequip())
            injector.setReequip(false);
        else if (slot != injector.getLastSlot())
        {
            injector.setLastSlot(slot);
            injector.setReequip(true);
        }

        // Needed to fix weird bug that occurs when pulling an item out from the main hand while in an inventory.
        boolean isUnequipped = this.mainHandItem.toString().equals("0 air") && main.toString().equals("1 air");
        boolean isItemChanged = !this.mainHandItem.is(main.getItem());
        boolean isSlotUpdated = slot == injector.getLastSlot() && isItemChanged && !injector.getReequip();
        boolean isHandChanged = isUnequipped && !injector.getLastItem().isEmpty() && !injector.getReequip();

        if (isSlotUpdated || isHandChanged)
            injector.setReequip(true);

        if (isUnequipped)
            this.mainHandItem = injector.getLastItem();

        if (slot == injector.getLastSlot() && !injector.getReequip())
            this.mainHandItem = player.getMainHandItem();

        if (MixinConfig.Animation.oldItemCooldown())
            this.mainHandHeight = Mth.clamp(this.mainHandHeight + (injector.getReequip() ? -0.4F : 0.4F), 0.0F, 1.0F);
        else
        {
            float scale = player.getAttackStrengthScale(1.0F);
            this.mainHandHeight += Mth.clamp((!injector.getReequip() ? scale * scale * scale : 0.0f) - this.mainHandHeight, -0.4F, 0.4F);
        }

        if (this.mainHandHeight < 0.1F)
            injector.setReequip(false);
    }

    /**
     * Enhances photosensitivity mode by completely disabling any hand movement when placing or interacting.
     * Only checks for global photosensitivity since this will break reequip animations if checking by item.
     */
    @Inject(method = "applyItemArmTransform", at = @At(value = "HEAD"), cancellable = true)
    protected void onApplyItemArmTransform(PoseStack poseStack, HumanoidArm arm, float equippedProgress, CallbackInfo callback)
    {
        if (!MixinConfig.isModEnabled(null))
            return;

        equippedProgress = MixinConfig.Swing.getGlobalSpeed() == DefaultConfig.Swing.PHOTOSENSITIVE ? 0 : equippedProgress;
        int i = arm == HumanoidArm.RIGHT ? 1 : -1;

        poseStack.translate((float) i * 0.56F, -0.52F + equippedProgress * -0.6F, -0.72F);
        callback.cancel();
    }

    /**
     * Simulates old item holding positions.
     * Controlled by the old item holding toggle.
     */
    @Inject(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V"))
    protected void onRenderItem(LivingEntity livingEntity, ItemStack itemStack, ItemTransforms.TransformType transformType, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo callback)
    {
        if (MixinConfig.Candy.oldItemHolding())
        {
            poseStack.mulPose(Vector3f.YP.rotationDegrees(5F));
            poseStack.translate(-0.01F, -0.01F, -0.015F);
        }
    }

    /**
     * Simulates the old swinging animations.
     * Controlled by the old swing toggle.
     */
    @Inject(method = "applyItemArmAttackTransform", at = @At(value = "HEAD"))
    protected void onApplyItemArmAttackTransform(PoseStack poseStack, HumanoidArm hand, float swingProgress, CallbackInfo callback)
    {
        if (MixinConfig.Animation.oldSwing())
        {
            float progress = Mth.sin((float) Math.PI * swingProgress);
            float scale = 1.0F - (0.3F * progress);
            poseStack.translate(-0.12F * progress, 0.085F * progress, 0.0F);
            poseStack.scale(scale, scale, scale);
        }
    }
}