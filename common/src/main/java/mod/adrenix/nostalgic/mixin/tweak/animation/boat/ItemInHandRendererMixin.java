package mod.adrenix.nostalgic.mixin.tweak.animation.boat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin
{
    /**
     * Prevents the player's arm from disappearing when controlling a boat.
     */
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isHandsBusy()Z"
        )
    )
    private boolean nt_animation_boat$preventBusyHandsOnBoat(boolean isHandsBusy, @Local LocalPlayer player)
    {
        if (AnimationTweak.HIDE_BOAT_ROWING.get() && player.getControlledVehicle() instanceof Boat)
            return false;

        return isHandsBusy;
    }
}
