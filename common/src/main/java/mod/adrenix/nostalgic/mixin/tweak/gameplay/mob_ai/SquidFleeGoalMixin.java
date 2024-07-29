package mod.adrenix.nostalgic.mixin.tweak.gameplay.mob_ai;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net/minecraft/world/entity/animal/Squid$SquidFleeGoal")
public abstract class SquidFleeGoalMixin
{
    /**
     * Prevents squids from being able to flee.
     */
    @ModifyReturnValue(
        method = "canUse",
        at = @At("RETURN")
    )
    private boolean nt_mob_ai$canSquidUseFleeGoal(boolean canUse)
    {
        if (GameplayTweak.DISABLE_ANIMAL_PANIC.get())
            return false;

        return canUse;
    }
}
