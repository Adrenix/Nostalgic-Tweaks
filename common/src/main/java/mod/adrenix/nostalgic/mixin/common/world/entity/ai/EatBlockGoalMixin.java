package mod.adrenix.nostalgic.mixin.common.world.entity.ai;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EatBlockGoal.class)
public abstract class EatBlockGoalMixin
{
    /**
     * Prevents sheep from eating grass.
     * Controlled by the disable sheep eat grass tweak.
     */
    @Inject(method = "start", at = @At("HEAD"), cancellable = true)
    private void NT$onStart(CallbackInfo callback)
    {
        if (ModConfig.Gameplay.disableSheepEatGrass())
            callback.cancel();
    }
}
