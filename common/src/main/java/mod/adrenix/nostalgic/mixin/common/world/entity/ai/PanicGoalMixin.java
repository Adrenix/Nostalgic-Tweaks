package mod.adrenix.nostalgic.mixin.common.world.entity.ai;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PanicGoal.class)
public abstract class PanicGoalMixin
{
    /* Shadows */

    @Shadow @Final protected PathfinderMob mob;

    /* Injections */

    /**
     * Prevents animals from panicking.
     * Controlled by the disable animal panic tweak.
     */
    @Inject(method = "shouldPanic", at = @At("HEAD"), cancellable = true)
    private void NT$onStart(CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Gameplay.disableAnimalPanic() && this.mob instanceof Animal)
            callback.setReturnValue(false);
    }
}
