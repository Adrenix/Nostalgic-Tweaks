package mod.adrenix.nostalgic.mixin.tweak.gameplay.mob_ai;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PanicGoal.class)
public abstract class PanicGoalMixin
{
    /* Shadows */

    @Shadow @Final protected PathfinderMob mob;

    /* Injections */

    /**
     * Prevents activation of the panic goal.
     */
    @ModifyReturnValue(
        method = "shouldPanic",
        at = @At("RETURN")
    )
    private boolean nt_mob_ai$modifyShouldPanic(boolean shouldPanic)
    {
        if (GameplayTweak.DISABLE_ANIMAL_PANIC.get() && this.mob instanceof Animal)
            return false;

        return shouldPanic;
    }
}
