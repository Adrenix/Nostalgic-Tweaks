package mod.adrenix.nostalgic.mixin.tweak.sound.position_sound;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.mixin.util.sound.PositionSoundHandler;
import mod.adrenix.nostalgic.mixin.util.sound.SoundMixinHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin
{
    /**
     * Disables or plays various sounds based on the config.
     */
    @ModifyExpressionValue(
        method = "playSound",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/util/RandomSource;DDD)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"
        )
    )
    private SimpleSoundInstance nt_position_sound$modifyPlaySound(SimpleSoundInstance original, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay, long seed)
    {
        PositionSoundHandler handler = SoundMixinHelper.getHandlerAt((ClientLevel) (Object) this, x, y, z, sound, source, volume, pitch);

        if (handler == null)
            return original;

        return new SimpleSoundInstance(handler.getSound(), handler.getSource(), handler.getVolume(), handler.getPitch(), RandomSource.create(seed), x, y, z);
    }
}
