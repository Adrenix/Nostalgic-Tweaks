package mod.adrenix.nostalgic.mixin.tweak.animation.player;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import mod.adrenix.nostalgic.helper.animation.AnimationConstant;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin
{
    /* Shadows */

    @Shadow private Entity entity;
    @Shadow private float eyeHeight;

    /* Injections */

    /**
     * Tracks camera eye height data before it is modified by the game.
     */
    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void nt_player_animation$onPreTick(CallbackInfo callback, @Share("eyeHeight") LocalFloatRef eyeHeight)
    {
        eyeHeight.set(this.eyeHeight);
    }

    /**
     * Disables smooth sneaking.
     */
    @Inject(
        method = "tick",
        at = @At("RETURN")
    )
    private void nt_player_animation$onPostTick(CallbackInfo callback, @Share("eyeHeight") LocalFloatRef eyeHeight)
    {
        if (!AnimationTweak.OLD_SNEAKING.get() || this.entity == null)
            return;

        float entityEyeHeight = this.entity.getEyeHeight();

        if (this.eyeHeight < entityEyeHeight)
            this.eyeHeight += (entityEyeHeight - this.eyeHeight) * 0.7F;
        else
            this.eyeHeight = entityEyeHeight;

        if (this.entity instanceof Player player && entityEyeHeight == 1.62F)
        {
            if (!player.getAbilities().flying && player.isCrouching())
                this.eyeHeight = AnimationConstant.SNEAK_EYE_HEIGHT;
        }
    }
}
