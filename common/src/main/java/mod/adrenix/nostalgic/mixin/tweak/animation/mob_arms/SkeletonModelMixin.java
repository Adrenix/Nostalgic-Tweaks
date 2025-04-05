package mod.adrenix.nostalgic.mixin.tweak.animation.mob_arms;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.helper.animation.MobArmHelper;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SkeletonModel.class)
public abstract class SkeletonModelMixin<T extends SkeletonRenderState> extends HumanoidModel<T>
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
        method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/SkeletonRenderState;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/entity/state/SkeletonRenderState;isAggressive:Z"
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
        method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/SkeletonRenderState;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/entity/state/SkeletonRenderState;isHoldingBow:Z"
        )
    )
    private boolean nt_mob_arms$modifyArmSetup(boolean isHoldingBow, SkeletonRenderState state)
    {
        if (!AnimationTweak.OLD_SKELETON_ARMS.get())
            return isHoldingBow;

        MobArmHelper.applyStaticArms(this.rightArm, this.leftArm);
        AnimationUtils.bobArms(this.rightArm, this.leftArm, state.ageInTicks);

        return false;
    }
}
