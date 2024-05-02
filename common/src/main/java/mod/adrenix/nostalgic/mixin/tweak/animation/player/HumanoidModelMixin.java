package mod.adrenix.nostalgic.mixin.tweak.animation.player;

import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin
{
    /* Shadows */

    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;

    /* Injections */

    /**
     * Applies the classic walking animation to player entities.
     */
    @Inject(
        method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
        at = @At(
            ordinal = 1,
            shift = At.Shift.AFTER,
            value = "FIELD",
            target = "Lnet/minecraft/client/model/geom/ModelPart;zRot:F"
        )
    )
    private <T extends LivingEntity> void nt_player_animation$setupClassicWalkingAnimation(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callback)
    {
        if (!AnimationTweak.OLD_CLASSIC_WALK_ARMS.get() || ClassUtil.isNotInstanceOf(entity, Player.class))
            return;

        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 2.0F * limbSwingAmount;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount;
        this.rightArm.zRot = (Mth.cos(limbSwing * 0.2312F) + 1.0F) * 1.0F * limbSwingAmount;
        this.leftArm.zRot = (Mth.cos(limbSwing * 0.2812F) - 1.0F) * 1.0F * limbSwingAmount;
    }
}
