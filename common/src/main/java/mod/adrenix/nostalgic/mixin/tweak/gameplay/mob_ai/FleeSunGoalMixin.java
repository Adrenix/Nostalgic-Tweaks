package mod.adrenix.nostalgic.mixin.tweak.gameplay.mob_ai;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FleeSunGoal.class)
public abstract class FleeSunGoalMixin
{
    /**
     * Prevents monsters from being capable of fleeing the sun.
     */
    @ModifyReturnValue(
        method = "canUse",
        at = @At("RETURN")
    )
    private boolean nt_mob_ai$canFleeSun(boolean canFleeSun)
    {
        if (GameplayTweak.DISABLE_MONSTER_AVOID_SUN.get())
            return false;

        return canFleeSun;
    }
}
