package mod.adrenix.nostalgic.mixin.tweak.gameplay.animal_sheep;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.helper.gameplay.SheepHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EatBlockGoal.class)
public abstract class EatBlockGoalMixin
{
    /* Shadows */

    @Shadow @Final private Mob mob;
    @Shadow private int eatAnimationTick;

    /* Injections */

    /**
     * Prevents sheep from eating grass.
     */
    @ModifyReturnValue(
        method = "canUse",
        at = @At("RETURN")
    )
    private boolean nt_animal_sheep$modifyCanEatGrass(boolean canEatGrass)
    {
        if (SheepHelper.isEatGrassDisabled(this.mob))
            return false;

        return canEatGrass;
    }

    /**
     * Prevents the broadcast of sheep eating grass.
     */
    @WrapWithCondition(
        method = "start",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V"
        )
    )
    private boolean nt_animal_sheep$canBroadcastEatEvent(Level level, Entity entity, byte state)
    {
        return !SheepHelper.isRandomWoolRegen(this.mob);
    }

    /**
     * Prevents the navigation ceasing when sheep start to eat grass.
     */
    @WrapWithCondition(
        method = "start",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/navigation/PathNavigation;stop()V"
        )
    )
    private boolean nt_animal_sheep$canEatStopNavigation(PathNavigation navigation)
    {
        return !SheepHelper.isRandomWoolRegen(this.mob);
    }

    /**
     * Prevents the eating animation for sheep.
     */
    @ModifyReturnValue(
        method = "getEatAnimationTick",
        at = @At("RETURN")
    )
    private int nt_animal_sheep$modifyGetEatAnimationTick(int eatAnimationTick)
    {
        if (SheepHelper.isRandomWoolRegen(this.mob))
            return 0;

        return eatAnimationTick;
    }

    /**
     * Prevents default eating grass behavior.
     */
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/EatBlockGoal;adjustedTickDelay(I)I"
        )
    )
    private int nt_animal_sheep$modifyActionOnFinishEatGoal(int adjustedTickDelay)
    {
        if (SheepHelper.isRandomWoolRegen(this.mob) && this.eatAnimationTick == adjustedTickDelay)
        {
            this.mob.ate();

            return 0;
        }

        return adjustedTickDelay;
    }
}
