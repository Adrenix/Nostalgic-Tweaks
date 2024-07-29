package mod.adrenix.nostalgic.mixin.tweak.animation.boat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Boat.class)
public abstract class BoatMixin
{
    /**
     * Disables the sounds emitted by boat paddling.
     */
    @ModifyReturnValue(
        method = "getPaddleSound",
        at = @At("RETURN")
    )
    private SoundEvent nt_animation_boat$modifyPaddleSound(SoundEvent paddleSound)
    {
        return AnimationTweak.HIDE_BOAT_ROWING.get() ? null : paddleSound;
    }
}
