package mod.adrenix.nostalgic.mixin.tweak.animation.player;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.helper.animation.AnimationConstant;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * This mixin is applied to both the client and server.
 */
@Mixin(Player.class)
public abstract class PlayerMixin
{
    /* Shadows */

    @Shadow @Final private Abilities abilities;

    /* Injections */

    /**
     * Lets the crouching state be active while the player is flying on the server.
     */
    @ModifyExpressionValue(
        method = "updatePlayerPose",
        at = @At(
            ordinal = 0,
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/player/Abilities;flying:Z"
        )
    )
    private boolean nt_player_animation$modifyServerFlyingPose(boolean isFlying)
    {
        if (AnimationTweak.OLD_CREATIVE_CROUCH.get() && this.abilities.flying)
            return false;

        return isFlying;
    }

    /**
     * Changes the camera position when the player is crouching on the client.
     */
    @ModifyExpressionValue(
        method = "getStandingEyeHeight",
        at = @At(
            value = "CONSTANT",
            args = "floatValue=1.27F"
        )
    )
    private float nt_player_animation$modifyStandingEyeHeight(float eyeHeight)
    {
        if (NostalgicTweaks.isServer())
            return eyeHeight;

        if (AnimationTweak.OLD_CREATIVE_CROUCH.get() && this.abilities.flying)
            return 1.62F;

        return AnimationTweak.OLD_SNEAKING.get() ? AnimationConstant.SNEAK_EYE_HEIGHT : eyeHeight;
    }
}
