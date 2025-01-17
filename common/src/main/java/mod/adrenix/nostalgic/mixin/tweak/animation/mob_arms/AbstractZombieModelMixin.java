package mod.adrenix.nostalgic.mixin.tweak.animation.mob_arms;

import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.model.AbstractZombieModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractZombieModel.class)
public abstract class AbstractZombieModelMixin
{
    /**
     * Prevents the zombie's arms from moving up when it becomes aggressive.
     */
    @ModifyArg(
        index = 2,
        method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/ZombieRenderState;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/AnimationUtils;animateZombieArms(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;ZFF)V"
        )
    )
    private boolean nt_mob_arms$setIsZombieAggressive(boolean isAggressive)
    {
        if (AnimationTweak.OLD_ZOMBIE_ARMS.get())
            return false;

        return isAggressive;
    }

    /**
     * Prevents the zombie's arm animations by setting the attack time to zero.
     */
    @ModifyArg(
        index = 3,
        method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/ZombieRenderState;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/AnimationUtils;animateZombieArms(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;ZFF)V"
        )
    )
    private float nt_mob_arms$setZombieAttackTime(float attackTime)
    {
        return AnimationTweak.OLD_ZOMBIE_ARMS.get() ? 0.0F : attackTime;
    }
}
