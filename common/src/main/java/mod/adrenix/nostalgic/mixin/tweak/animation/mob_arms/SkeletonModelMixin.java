package mod.adrenix.nostalgic.mixin.tweak.animation.mob_arms;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.helper.animation.MobArmHelper;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SkeletonModel.class)
public abstract class SkeletonModelMixin<T extends Mob> extends HumanoidModel<T>
{
    /* Fake Constructor */

    private SkeletonModelMixin(ModelPart root)
    {
        super(root);
    }

    /* Injections */

    /**
     * Prevents skeletons from setting up bow animations when it becomes aggressive.
     */
    @ModifyExpressionValue(
        method = "prepareMobModel(Lnet/minecraft/world/entity/Mob;FFF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;isAggressive()Z"
        )
    )
    private boolean nt_mob_arms$modifyArmModel(boolean isAggressive)
    {
        if (AnimationTweak.OLD_SKELETON_ARMS.get())
            return false;

        return isAggressive;
    }

    /**
     * Prevents the skeleton animation for firing a bow.
     */
    @ModifyExpressionValue(
        method = "setupAnim(Lnet/minecraft/world/entity/Mob;FFFFF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;isAggressive()Z"
        )
    )
    private boolean nt_mob_arms$modifyArmSetup(boolean isAggressive, T entity, float limbSwing, float limbSwingAmount, float ageInTicks)
    {
        if (!AnimationTweak.OLD_SKELETON_ARMS.get())
            return isAggressive;

        MobArmHelper.applyStaticArms(this.rightArm, this.leftArm);
        AnimationUtils.bobArms(this.rightArm, this.leftArm, ageInTicks);

        return false;
    }
}
