package mod.adrenix.nostalgic.mixin.tweak.sound.block_sound;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.SoundTweak;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BubbleColumnBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BubbleColumnBlock.class)
public abstract class BubbleColumnBlockMixin
{
    /**
     * Prevents bubble column blocks from playing ambient sounds based on tweak context.
     */
    @WrapWithCondition(
        method = "animateTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
        )
    )
    private boolean nt_block_sound$shouldPlayBubbleColumnAmbience(Level level, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, boolean distanceDelay)
    {
        return !SoundTweak.DISABLE_BUBBLE_COLUMN.get();
    }
}
