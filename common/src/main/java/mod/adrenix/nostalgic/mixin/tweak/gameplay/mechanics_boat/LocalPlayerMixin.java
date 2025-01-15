package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_boat;

import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin
{
    /* Shadows */

    @Shadow private boolean handsBusy;

    /* Injections */

    /**
     * Prevents the player's from being considering busy when controlling a boat.
     */
    @Inject(
        method = "rideTick",
        at = @At("RETURN")
    )
    private void nt_mechanics_boat$onFinishRideTick(CallbackInfo callback)
    {
        if (GameplayTweak.DISABLE_BOAT_BUSY_HANDS.get())
            this.handsBusy = false;
    }
}
