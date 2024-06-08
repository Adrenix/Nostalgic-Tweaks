package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_player;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.mixin.access.LivingEntityAccess;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
