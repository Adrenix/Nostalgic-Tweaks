package mod.adrenix.nostalgic.mixin.tweak.sound.experience_level;

import mod.adrenix.nostalgic.tweak.config.SoundTweak;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    /**
     * Mutes the experience level up sound.
     */
    @ModifyArg(
        index = 6,
        method = "giveExperienceLevels",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
        )
    )
    private float nt_experience_level$modifyExperienceLevelVolume(float volume)
    {
        return SoundTweak.DISABLE_XP_LEVEL.get() ? 0.0F : volume;
    }
}
