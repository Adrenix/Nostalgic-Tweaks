package mod.adrenix.nostalgic.mixin.tweak.sound.damage_sound;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.client.ClientSound;
import mod.adrenix.nostalgic.tweak.config.SoundTweak;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    /**
     * Changes the fall damage sounds to a blank sound.
     */
    @ModifyReturnValue(
        method = "getFallDamageSound",
        at = @At("RETURN")
    )
    private SoundEvent nt_damage_sound$modifyFallDamageSound(SoundEvent sound)
    {
        return SoundTweak.OLD_FALL.get() ? ClientSound.BLANK.get() : sound;
    }
}
