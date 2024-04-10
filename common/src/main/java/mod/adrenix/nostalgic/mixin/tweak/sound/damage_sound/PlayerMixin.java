package mod.adrenix.nostalgic.mixin.tweak.sound.damage_sound;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.client.ClientSound;
import mod.adrenix.nostalgic.tweak.config.SoundTweak;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    /**
     * Brings back the old "oof" sound. Changing the sound here ensures only the player's hurt sound is changed. This
     * allows for the vanilla player hurt sound to be played in situations where it would be preferred. Such as when the
     * Ender Dragon dies.
     */
    @ModifyReturnValue(
        method = "getHurtSound",
        at = @At("RETURN")
    )
    private SoundEvent nt_damage_sound$modifyHurtSound(SoundEvent sound)
    {
        return SoundTweak.OLD_HURT.get() ? ClientSound.PLAYER_HURT.get() : sound;
    }

    /**
     * Disables the death sound when the player dies to prevent duplicate "off" sounds.
     */
    @ModifyReturnValue(
        method = "getDeathSound",
        at = @At("RETURN")
    )
    private SoundEvent nt_damage_sound$modifyDeathSound(SoundEvent sound)
    {
        return SoundTweak.OLD_HURT.get() ? ClientSound.BLANK.get() : sound;
    }
}
