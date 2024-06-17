package mod.adrenix.nostalgic.mixin.tweak.animation.mob_arms;

import mod.adrenix.nostalgic.mixin.util.animation.MobArmMixinHelper;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimationUtils.class)
public abstract class AnimationUtilsMixin
{
    /**
     * Prevents zombie arms from bobbing.
     */
    @Inject(
        method = "animateZombieArms",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/AnimationUtils;bobArms(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;F)V"
        )
    )
    private static void nt_mob_arms$onBobZombieArms(ModelPart leftArm, ModelPart rightArm, boolean isAggressive, float attackTime, float ageInTicks, CallbackInfo callback)
    {
        if (AnimationTweak.OLD_ZOMBIE_ARMS.get())
            MobArmMixinHelper.applyStaticArms(rightArm, leftArm);
    }
}
