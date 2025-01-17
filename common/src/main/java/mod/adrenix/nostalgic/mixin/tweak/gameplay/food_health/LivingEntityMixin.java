package mod.adrenix.nostalgic.mixin.tweak.gameplay.food_health;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    /**
     * Prevents the hunger effect from being applied to entities if it is disabled.
     */
    @ModifyReturnValue(
        method = "canBeAffected",
        at = @At("RETURN")
    )
    private boolean nt_food_health$shouldAddFoodEffect(boolean canBeAffected, MobEffectInstance effectInstance)
    {
        if (GameplayTweak.PREVENT_HUNGER_EFFECT.get() && effectInstance.getEffect() == MobEffects.HUNGER)
            return false;

        return canBeAffected;
    }
}
