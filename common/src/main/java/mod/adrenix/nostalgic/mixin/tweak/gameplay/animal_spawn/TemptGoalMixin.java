package mod.adrenix.nostalgic.mixin.tweak.gameplay.animal_spawn;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TemptGoal.class)
public abstract class TemptGoalMixin
{
    /* Shadows */

    @Shadow @Final protected PathfinderMob mob;

    /* Injections */

    /**
     * Prevent animals from following the player if they are holding a food item the animal likes.
     */
    @ModifyReturnValue(
        method = "shouldFollow",
        at = @At("RETURN")
    )
    private boolean nt_animal_spawn$modifyShouldFollow(boolean shouldFollow)
    {
        if (this.mob instanceof AgeableMob && GameplayTweak.DISABLE_ANIMAL_TEMPTING.get())
            return false;

        return shouldFollow;
    }
}
