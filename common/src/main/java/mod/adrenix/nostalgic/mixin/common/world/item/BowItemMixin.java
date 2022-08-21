package mod.adrenix.nostalgic.mixin.common.world.item;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BowItem.class)
public abstract class BowItemMixin
{
    /* Shadows */

    @Shadow public abstract void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged);

    /* Injections */

    /**
     * Skips preparing an arrow fire. Instead, just shoot the arrow with the given charge value.
     * Controlled by the instant bow tweak.
     */
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void NT$onUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> callback)
    {
        // It is important to not now allow instant-fire with a bow in the off-hand to prevent glitches
        if (!ModConfig.Gameplay.instantBow() || hand.equals(InteractionHand.OFF_HAND))
            return;

        // 72000 charge won't fire, a charge < 71980 has no change on arrow speed, therefore range is 71980 to 72000
        int charge = 72000 - (int) (((float) ModConfig.Gameplay.instantBowSpeed() / 100.0F) * 20.0F);
        this.releaseUsing(player.getMainHandItem(), level, player, charge);
        callback.setReturnValue(InteractionResultHolder.pass(player.getMainHandItem()));
    }

    /**
     * Prevents the bow from receiving damage after firing an arrow.
     * Controlled by the invincible bow tweak.
     */
    @ModifyArg(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V"))
    private int NT$onDamageBow(int vanilla)
    {
        return ModConfig.Gameplay.invincibleBow() ? 0 : vanilla;
    }

    /**
     * Prevents any animations from playing before, during, or after arrow fire.
     * Controlled by the instant bow tweak.
     */
    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void NT$onGetUseAnimation(ItemStack stack, CallbackInfoReturnable<UseAnim> callback)
    {
        if (ModConfig.Gameplay.instantBow())
            callback.setReturnValue(UseAnim.NONE);
    }
}
