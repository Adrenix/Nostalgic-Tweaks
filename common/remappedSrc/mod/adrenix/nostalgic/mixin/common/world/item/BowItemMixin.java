package mod.adrenix.nostalgic.mixin.common.world.item;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
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

    @Shadow public abstract void releaseUsing(ItemStack stack, World level, LivingEntity livingEntity, int timeCharged);

    /* Injections */

    /**
     * Skips preparing an arrow fire. Instead, just shoot the arrow with the given charge value.
     * Controlled by the instant bow tweak.
     */
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void NT$onUse(World level, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> callback)
    {
        if (!ModConfig.Gameplay.instantBow())
            return;

        // 72000 charge won't fire, a charge < 71980 has no change on arrow speed, therefore range is 71980 to 72000
        int charge = 72000 - (int) (((float) ModConfig.Gameplay.instantBowSpeed() / 100.0F) * 20.0F);
        this.releaseUsing(player.getMainHandStack(), level, player, charge);
        callback.setReturnValue(TypedActionResult.pass(player.getMainHandStack()));
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
    private void NT$onGetUseAnimation(ItemStack stack, CallbackInfoReturnable<UseAction> callback)
    {
        if (ModConfig.Gameplay.instantBow())
            callback.setReturnValue(UseAction.NONE);
    }
}
