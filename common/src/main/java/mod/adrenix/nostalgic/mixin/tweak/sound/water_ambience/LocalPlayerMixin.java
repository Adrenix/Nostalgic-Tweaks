package mod.adrenix.nostalgic.mixin.tweak.sound.water_ambience;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.SoundTweak;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin
{
    /**
     * Prevents water enter/exit sounds and underwater ambient sounds from playing.
     */
    @ModifyArg(
        index = 5,
        method = "updateIsUnderwater",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
        )
    )
    private float nt_water_ambience$muteUnderwaterSounds(float volume)
    {
        return SoundTweak.DISABLE_WATER_AMBIENCE.get() ? 0.0F : volume;
    }

    /**
     * Prevents underwater ambient sounds from playing.
     */
    @WrapWithCondition(
        method = "updateIsUnderwater",
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
