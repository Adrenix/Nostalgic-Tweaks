package mod.adrenix.nostalgic.mixin.tweak.sound.water_ambience;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.SoundTweak;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundHandler;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(UnderwaterAmbientSoundHandler.class)
public abstract class UnderwaterAmbientSoundHandlerMixin
{
    /**
     * Mutes the additional ambient looping water sounds while the player is underwater.
     */
    @WrapWithCondition(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"
        )
    )
    private boolean nt_water_ambience$muteUnderwaterAmbience(SoundManager manager, SoundInstance sound)
    {
        return !SoundTweak.DISABLE_WATER_AMBIENCE.get();
    }
}
