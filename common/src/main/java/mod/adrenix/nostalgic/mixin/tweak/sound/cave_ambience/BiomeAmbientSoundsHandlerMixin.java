package mod.adrenix.nostalgic.mixin.tweak.sound.cave_ambience;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import mod.adrenix.nostalgic.mixin.util.sound.CaveSoundManager;
import mod.adrenix.nostalgic.tweak.config.SoundTweak;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.BiomeManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeAmbientSoundsHandler.class)
public abstract class BiomeAmbientSoundsHandlerMixin
{
    /* Shadows */

    @Shadow @Final private RandomSource random;
    @Shadow private float moodiness;

    /* Unique */

    @Unique @Nullable private CaveSoundManager nt$caveSoundManager = null;

    /* Injections */

    /**
     * Creates a new {@link CaveSoundManager} for the biome ambient sound handler.
     */
    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void nt_cave_ambience$whenConstructed(LocalPlayer player, SoundManager soundManager, BiomeManager biomeManager, CallbackInfo callback)
    {
        this.nt$caveSoundManager = new CaveSoundManager(player, soundManager);
    }

    /**
     * Ticks and plays an ambient cave sound, if possible, at the given random block position. The handler's moodiness
     * is reset to prevent vanilla cave sound logic.
     */
    @Inject(
        method = "method_26271",
        at = @At(
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getBrightness(Lnet/minecraft/world/level/LightLayer;Lnet/minecraft/core/BlockPos;)I"
        )
    )
    private void nt_cave_ambience$onMoodInspection(CallbackInfo callback, @Local BlockPos blockPos)
    {
        if (SoundTweak.OLD_CAVE_AMBIENCE.get() && this.nt$caveSoundManager != null)
        {
            this.nt$caveSoundManager.tickAndPlayIfPossible(blockPos);
            this.moodiness = 0.0F;
        }
    }

    /**
     * Replaces the modern cave sounds with an old-style cave sound with a randomized pitch.
     */
    @ModifyExpressionValue(
        method = "method_26271",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;forAmbientMood(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/util/RandomSource;DDD)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"
        )
    )
    private SimpleSoundInstance nt_cave_ambience$modifyAmbientCaveSound(SimpleSoundInstance sound)
    {
        if (SoundTweak.OLD_CAVE_SOUNDS.get())
            return CaveSoundManager.getSound(this.random, sound.getX(), sound.getY(), sound.getZ());

        return sound;
    }
}
