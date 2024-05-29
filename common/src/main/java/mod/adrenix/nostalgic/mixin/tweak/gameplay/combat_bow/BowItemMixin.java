package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_bow;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(BowItem.class)
public abstract class BowItemMixin
{
    /**
     * Prevents a bow from being destroyed before firing an arrow.
     */
    @Inject(
        method = "releaseUsing",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V"
        )
    )
    private void nt_combat_bow$onBeforeArrowRelease(ItemStack itemStack, Level level, LivingEntity entity, int timeCharged, CallbackInfo callback)
    {
        if (GameplayTweak.INVINCIBLE_BOW.get())
            itemStack.setDamageValue(0);
    }

    /**
     * Prevents a bow from receiving damage.
     */
    @WrapWithCondition(
        method = "releaseUsing",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V"
        )
    )
    private <T extends LivingEntity> boolean nt_combat_bow$isBowBreakable(ItemStack itemStack, int amount, T entity, Consumer<T> onBroken)
    {
        return !GameplayTweak.INVINCIBLE_BOW.get();
    }
}
