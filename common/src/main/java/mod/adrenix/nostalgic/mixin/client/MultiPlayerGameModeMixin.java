package mod.adrenix.nostalgic.mixin.client;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin
{
    /**
     * Informs the client whether the current game-mode is using the experience system.
     * Controlled by the old experience tweak.
     */
    @Inject(method = "hasExperience", at = @At("HEAD"), cancellable = true)
    private void NT$onHasExperience(CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Gameplay.disableExperienceBar())
            callback.setReturnValue(false);
    }
}
