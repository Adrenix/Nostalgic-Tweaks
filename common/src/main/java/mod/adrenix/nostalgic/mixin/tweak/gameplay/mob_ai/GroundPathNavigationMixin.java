package mod.adrenix.nostalgic.mixin.tweak.gameplay.mob_ai;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GroundPathNavigation.class)
public abstract class GroundPathNavigationMixin
{
    /**
     * Prevents monsters from creating paths that avoid sunlight.
     */
    @ModifyExpressionValue(
        method = "trimPath",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/ai/navigation/GroundPathNavigation;avoidSun:Z"
        )
    )
    private boolean nt_mob_ai$preventSunAvoidance(boolean avoidSun)
    {
        if (GameplayTweak.DISABLE_MONSTER_AVOID_SUN.get())
            return false;

        return avoidSun;
    }
}
