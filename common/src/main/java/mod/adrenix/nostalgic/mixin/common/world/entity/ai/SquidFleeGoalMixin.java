package mod.adrenix.nostalgic.mixin.common.world.entity.ai;

import mod.adrenix.nostalgic.common.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/world/entity/animal/Squid$SquidFleeGoal")
public abstract class SquidFleeGoalMixin
{
    /**
     * Prevents squids from being able to flee.
     * Controlled by the disable animal panic tweak.
     */
    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void NT$onCanUse(CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Gameplay.disableAnimalPanic())
            callback.setReturnValue(false);
    }
}
