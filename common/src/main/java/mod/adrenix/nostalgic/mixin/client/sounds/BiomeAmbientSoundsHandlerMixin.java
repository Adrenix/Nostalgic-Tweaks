package mod.adrenix.nostalgic.mixin.client.sounds;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(BiomeAmbientSoundsHandler.class)
public abstract class BiomeAmbientSoundsHandlerMixin
{
    /* Shadows */

    @Shadow @Final private LocalPlayer player;

    /* Injections */

    /**
     * Disables the ambient mood sounds that are played in nether biomes.
     * Controlled by the disabled nether ambience sound tweak.
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getAmbientMood()Ljava/util/Optional;"))
    private Optional<AmbientMoodSettings> NT$onTickMood(Biome biome)
    {
        if (ModConfig.Sound.disableNetherAmbience() && this.player.level().dimension() == Level.NETHER)
            return Optional.empty();

        return biome.getAmbientMood();
    }

    /**
     * Disables the ambient additional sounds that are played in nether biomes.
     * Controlled by the disabled nether ambience sound tweak.
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getAmbientAdditions()Ljava/util/Optional;"))
    private Optional<AmbientAdditionsSettings> NT$onTickAdditions(Biome biome)
    {
        if (ModConfig.Sound.disableNetherAmbience() && this.player.level().dimension() == Level.NETHER)
            return Optional.empty();

        return biome.getAmbientAdditions();
    }

    /**
     * Disables the ambient additional loop sounds that are played in nether biomes.
     * Controlled by the disabled nether ambience sound tweak.
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getAmbientLoop()Ljava/util/Optional;"))
    private Optional<Holder<SoundEvent>> NT$onTickLoop(Biome biome)
    {
        if (ModConfig.Sound.disableNetherAmbience() && this.player.level().dimension() == Level.NETHER)
            return Optional.empty();

        return biome.getAmbientLoop();
    }
}
