package mod.adrenix.nostalgic.mixin.tweak.animation.held_item;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    /**
     * Prevents the item break sound from playing.
     */
    @WrapWithCondition(
        method = "breakItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
        )
    )
    private boolean nt_held_item$shouldPlayBreakItemSound(Level level, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay)
    {
        return !AnimationTweak.OLD_TOOL_EXPLOSION.get();
    }

    /**
     * Prevents the item break particles from showing.
     */
    @WrapWithCondition(
        method = "breakItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;spawnItemParticles(Lnet/minecraft/world/item/ItemStack;I)V"
        )
    )
    private boolean nt_held_item$shouldShowBreakItemParticles(LivingEntity entity, ItemStack itemStack, int amount)
    {
        return !AnimationTweak.OLD_TOOL_EXPLOSION.get();
    }
}
