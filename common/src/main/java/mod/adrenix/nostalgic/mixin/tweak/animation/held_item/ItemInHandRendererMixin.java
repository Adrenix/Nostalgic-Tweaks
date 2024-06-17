package mod.adrenix.nostalgic.mixin.tweak.animation.held_item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.mixin.duck.SlotTracker;
import mod.adrenix.nostalgic.mixin.util.animation.HeldItemMixinHelper;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.common.data.Holder;
import mod.adrenix.nostalgic.util.common.data.NumberHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin
{
    /* Shadows */

    @Shadow private ItemStack mainHandItem;
    @Shadow private ItemStack offHandItem;
    @Shadow private float mainHandHeight;

    /* Injections */

    /**
     * Forces the attack strength to be 1.0F when the cooldown animation is disabled.
     */
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"
        )
    )
    private float nt_held_item$getAttackStrengthScale(float scale)
    {
        return AnimationTweak.OLD_ITEM_COOLDOWN.get() ? 1.0F : scale;
    }

    /**
     * Simulate the old item reequip logic and animation.
     */
    @Inject(
        method = "tick",
        at = @At(
            ordinal = 3,
            value = "INVOKE",
            target = "Lnet/minecraft/util/Mth;clamp(FFF)F"
        )
    )
    private void nt_held_item$onItemInHandTick(CallbackInfo callback)
    {
        if (!ModTweak.ENABLED.get())
            return;

        Holder<ItemStack> handItem = Holder.create(this.mainHandItem);
        NumberHolder<Float> handHeight = NumberHolder.create(this.mainHandHeight);

        HeldItemMixinHelper.oldReequipLogic(handItem, handHeight);

        this.mainHandItem = handItem.get();
        this.mainHandHeight = handHeight.get();
    }

    /**
     * Prevents the flashing of the previously held item when the player pulls the held item out of their main hand.
     */
    @ModifyVariable(
        argsOnly = true,
        method = "renderArmWithItem",
        at = @At("HEAD")
    )
    private ItemStack nt_held_item$changeRendererItemStack(ItemStack itemStack, AbstractClientPlayer player, float partialTick, float pitch, InteractionHand hand)
    {
        if (hand != InteractionHand.MAIN_HAND)
            return itemStack;

        return HeldItemMixinHelper.getLastHeldItem(itemStack, this.mainHandItem, player.getMainHandItem(), (SlotTracker) player);
    }

    /**
     * Prevents the addition assignment operator from incrementing the main hand height.
     */
    @ModifyArg(
        index = 0,
        method = "tick",
        at = @At(
            ordinal = 2,
            value = "INVOKE",
            target = "Lnet/minecraft/util/Mth;clamp(FFF)F"
        )
    )
    private float nt_held_item$onIncreaseMainHand(float current)
    {
        return AnimationTweak.OLD_ITEM_REEQUIP.get() ? 0.0F : current;
    }

    /**
     * Prevents the off-hand from reequipping when only the stack size changes.
     */
    @ModifyArg(
        index = 0,
        method = "tick",
        at = @At(
            ordinal = 3,
            value = "INVOKE",
            target = "Lnet/minecraft/util/Mth;clamp(FFF)F"
        )
    )
    private float nt_held_item$onIncreaseOffHand(float current)
    {
        LocalPlayer player = Minecraft.getInstance().player;

        if (!AnimationTweak.OLD_ITEM_REEQUIP.get() || player == null)
            return current;

        ItemStack offHandStack = player.getOffhandItem();

        if (this.offHandItem.is(offHandStack.getItem()) && this.offHandItem.getCount() != offHandStack.getCount())
            return 0.0F;

        return current;
    }

    /**
     * Changes the item matching to return false, on the main hand, so that the last held item can be tracked. This
     * prevents reequip animation issues when going from an item in the main hand to air.
     */
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
        )
    )
    private boolean nt_held_item$modifyHeldItemMatching(boolean isMatched)
    {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null)
            return isMatched;

        return !AnimationTweak.OLD_ITEM_REEQUIP.get() && ItemStack.matches(this.mainHandItem, player.getMainHandItem());
    }
}
