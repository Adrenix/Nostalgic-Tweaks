package mod.adrenix.nostalgic.mixin.tweak.gameplay.mob_ai;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RangedBowAttackGoal.class)
public abstract class RangedBowAttackGoalMixin<T extends Monster & RangedAttackMob>
{
    /* Shadows */

    @Shadow @Final private T mob;

    /* Injections */

    /**
     * Prevents skeletons from strafing while attacking with their bows.
     */
    @WrapWithCondition(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/control/MoveControl;strafe(FF)V"
        )
    )
    private boolean nt_mob_ai$onRangedBowAttackTick(MoveControl moveControl, float forward, float strafe)
    {
        LivingEntity target = this.mob.getTarget();

        if (GameplayTweak.DISABLE_SKELETON_STRAFING.get() && this.mob instanceof AbstractSkeleton && target != null)
        {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            return false;
        }

        return true;
    }
}
