package mod.adrenix.nostalgic.mixin.tweak.sound.game_music;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.SoundTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /**
     * Prevents the sound manager from pausing.
     */
    @WrapWithCondition(
        method = "pauseGame",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/SoundManager;pause()V"
        )
    )
    private boolean nt_game_music$shouldPauseSound(SoundManager manager)
    {
        return !SoundTweak.PLAY_MUSIC_WHEN_PAUSED.get();
    }
}
