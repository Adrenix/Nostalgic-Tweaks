package mod.adrenix.nostalgic.mixin.client.sounds;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UnderwaterAmbientSoundHandler.class)
public abstract class UnderwaterAmbientSoundHandlerMixin
{
    /**
     * Disables the additional ambient looping water sounds while the player is underwater.
     * Controlled by the disabled water ambience sound tweak.
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void NT$onTick(CallbackInfo callback)
    {
        if (ModConfig.Sound.disableWaterAmbience())
            callback.cancel();
    }
}
