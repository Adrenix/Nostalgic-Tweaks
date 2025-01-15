package mod.adrenix.nostalgic.mixin.tweak.animation.entity;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BodyRotationControl.class)
public abstract class BodyRotationControlMixin
{
    /**
     * Prevents moving the mob's torso to face towards its looking direction after a brief period of time. This helps
     * simulate the old mob head and body rotation movement.
     */
    @WrapWithCondition(
        method = "clientTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/control/BodyRotationControl;rotateHeadTowardsFront()V"
        )
    )
    private boolean nt_animation_entity$shouldHeadRotateTowardsFront(BodyRotationControl controller)
    {
        return !AnimationTweak.OLD_MOB_HEAD_BODY_TURN.get();
    }
}
