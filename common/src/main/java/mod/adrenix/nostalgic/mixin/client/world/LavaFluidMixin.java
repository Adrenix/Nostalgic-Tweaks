package mod.adrenix.nostalgic.mixin.client.world;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LavaFluid.class)
public abstract class LavaFluidMixin
{
    /**
     * Disables the lava pop sounds.
     * Controlled by the disabled lava pop tweak.
     */
    @ModifyArg(method = "animateTick", index = 5, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"))
    private float NT$onAnimateTickLavaPop(float volume)
    {
        return ModConfig.Sound.disableLavaPop() ? 0.0F : volume;
    }

    /**
     * Disables the lava ambience sounds.
     * Controlled by the disabled lava ambience tweak.
     */
    @ModifyArg(method = "animateTick", index = 5, at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"))
    private float NT$onAnimateTickLavaAmbience(float volume)
    {
        return ModConfig.Sound.disableLavaAmbience() ? 0.0F : volume;
    }

    /**
     * Disables the lava particles that pop out of lava blocks from appearing.
     * Controlled by the disabled lava particles tweak.
     */
    @Redirect(method = "animateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private void NT$onAnimateTickLavaParticle(Level level, ParticleOptions particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        if (!ModConfig.Candy.disableLavaParticles())
            level.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
    }
}
