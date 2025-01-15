package mod.adrenix.nostalgic.mixin.tweak.sound.nether_ambience;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.SoundTweak;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mixin(BiomeAmbientSoundsHandler.class)
public abstract class BiomeAmbientSoundsHandlerMixin
{
    /* Shadows */

    @Shadow @Final private LocalPlayer player;

    /* Injections */

    /**
     * Disables the ambient mood sounds that are played in nether biomes.
     */
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/biome/Biome;getAmbientMood()Ljava/util/Optional;"
        )
    )
    private Optional<AmbientMoodSettings> nt_nether_ambience$muteNetherMood(Optional<AmbientMoodSettings> settings)
    {
        if (SoundTweak.DISABLE_NETHER_AMBIENCE.get() && this.player.clientLevel.dimension() == Level.NETHER)
            return Optional.empty();

        return settings;
    }

    /**
     * Disables the ambient additional sounds that are played in nether biomes.
     */
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/biome/Biome;getAmbientAdditions()Ljava/util/Optional;"
        )
    )
    private Optional<AmbientAdditionsSettings> nt_nether_ambience$muteNetherAdditions(Optional<AmbientAdditionsSettings> settings)
    {
        if (SoundTweak.DISABLE_NETHER_AMBIENCE.get() && this.player.clientLevel.dimension() == Level.NETHER)
            return Optional.empty();

        return settings;
    }

    /**
     * Disables the ambient additional loop sounds that are played in nether biomes.
     */
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/biome/Biome;getAmbientLoop()Ljava/util/Optional;"
        )
    )
    private Optional<Holder<SoundEvent>> nt_nether_ambience$muteNetherAmbientLoop(Optional<Holder<SoundEvent>> sound)
    {
        if (SoundTweak.DISABLE_NETHER_AMBIENCE.get() && this.player.clientLevel.dimension() == Level.NETHER)
            return Optional.empty();

        return sound;
    }
}
