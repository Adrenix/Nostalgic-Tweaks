package mod.adrenix.nostalgic.mixin.common.world.entity.animal;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.animal.Chicken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Chicken.class)
public abstract class ChickenMixin
{
    /**
     * Changes the behavior of chicken entity removal. This is needed for chickens that have jockeys attached.
     * Controlled by the old animal spawning tweak.
     */
    @Inject(method = "removeWhenFarAway", at = @At("HEAD"), cancellable = true)
    private void NT$onRemoveWhenFarAway(double distanceToClosestPlayer, CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Gameplay.oldAnimalSpawning())
            callback.setReturnValue(true);
    }
}
