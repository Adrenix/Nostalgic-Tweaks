package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_fishing;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FishingRodItem.class)
public abstract class FishingRodItemMixin
{
    /**
     * Prevents the retrieve sound of a fishing bobber.
     */
    @WrapWithCondition(
        method = "use",
        at = @At(
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
        )
    )
    public boolean nt_mechanics_fishing$modifyBobberRetrieveSound(Level level, Player player, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch)
    {
        return !GameplayTweak.OLD_FISHING_CASTING.get();
    }

    /**
     * Changes the fishing bobber throw sound to the old generic throw sound.
     */
    @ModifyArg(
        method = "use",
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
        )
    )
    public SoundEvent nt_mechanics_fishing$modifyBobberThrowSound(SoundEvent soundEvent)
    {
        if (GameplayTweak.OLD_FISHING_CASTING.get())
            return SoundEvents.EGG_THROW;

        return soundEvent;
    }
}
