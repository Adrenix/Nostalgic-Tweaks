package mod.adrenix.nostalgic.mixin.tweak.animation.player;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer
{
    /* Fake Constructor */

    private LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile)
    {
        super(clientLevel, gameProfile);
    }

    /* Injections */

    /**
     * Lets the crouching state be active while the player is flying on the client.
     */
    @ModifyExpressionValue(
        method = "aiStep",
        at = @At(
            ordinal = 0,
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/player/Abilities;flying:Z"
        )
    )
    private boolean nt_player_animation$modifyClientFlyingPose(boolean isFlying)
    {
        if (AnimationTweak.OLD_CREATIVE_CROUCH.get() && this.getAbilities().flying)
            return false;

        return isFlying;
    }
}
