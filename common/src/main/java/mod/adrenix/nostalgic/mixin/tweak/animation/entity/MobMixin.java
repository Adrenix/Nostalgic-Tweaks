package mod.adrenix.nostalgic.mixin.tweak.animation.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.mixin.access.BodyRotationControlAccess;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity
{
    /* Shadows */

    @Shadow @Final private BodyRotationControl bodyRotationControl;

    /* Fake Constructor */

    private MobMixin(EntityType<? extends LivingEntity> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Injections */

    /**
     * Simulates the old mob head and body rotation movement by invoking the super of {@code tickHeadTurn} and then
     * checking head and body bounds as necessary.
     */
    @ModifyReturnValue(
        method = "tickHeadTurn",
        at = @At("RETURN")
    )
    private float nt_animation_entity$modifyLivingTickHeadTurn(float animStep, float yRot)
    {
        if (!AnimationTweak.OLD_MOB_HEAD_BODY_TURN.get())
            return animStep;

        BodyRotationControlAccess bodyRotationControl = ((BodyRotationControlAccess) this.bodyRotationControl);
        float livingAnimStep = super.tickHeadTurn(yRot, animStep);

        if (bodyRotationControl.nt$isMoving())
            bodyRotationControl.nt$rotateHeadIfNecessary();
        else if (bodyRotationControl.nt$notCarryingMobPassengers())
            bodyRotationControl.nt$rotateBodyIfNecessary();

        return livingAnimStep;
    }
}
