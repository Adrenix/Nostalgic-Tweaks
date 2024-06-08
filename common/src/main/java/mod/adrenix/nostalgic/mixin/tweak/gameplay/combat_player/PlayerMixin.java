package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_player;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.mixin.access.LivingEntityAccess;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    /* Shadows */

    @Shadow
    public abstract float getCurrentItemAttackStrengthDelay();

    /* Injections */

    /**
     * Resets the attack strength scale back to current item attack strength delay every tick.
     */
    @Inject(
        method = "attack",
        at = @At("HEAD")
    )
    private void nt_combat_player$onAttack(Entity target, CallbackInfo callback)
    {
        if (!GameplayTweak.DISABLE_COOLDOWN.get())
            return;

        ((LivingEntityAccess) this).nt$setAttackStrengthTicker((int) Math.ceil(this.getCurrentItemAttackStrengthDelay()));
    }

    /**
     * Ensures the player's attack strength scale is always at 1.0F.
     */
    @ModifyReturnValue(
        method = "getAttackStrengthScale",
        at = @At("RETURN")
    )
    private float nt_combat_player$modifyAttackStrengthScale(float attackStrengthScale)
    {
        return GameplayTweak.DISABLE_COOLDOWN.get() ? 1.0F : attackStrengthScale;
    }

    /**
     * Prevents the application of critical hits by tricking the critical hit flag into thinking the player is
     * climbing.
     */
    @ModifyExpressionValue(
        method = "attack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;onClimbable()Z"
        )
    )
    private boolean nt_combat_player$modifyCriticalHitAttack(boolean isOnClimbable)
    {
        return GameplayTweak.DISABLE_CRITICAL_HIT.get() || isOnClimbable;
    }

    /**
     * Prevents sweep attacks by providing the attack algorithm an empty list of entities within sweep range.
     */
    @ModifyExpressionValue(
        method = "attack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
        )
    )
    private List<LivingEntity> nt_combat_player$modifyListOfSweepAttacks(List<LivingEntity> listOfSweepAttacks)
    {
        return GameplayTweak.DISABLE_SWEEP.get() ? List.of() : listOfSweepAttacks;
    }
}
