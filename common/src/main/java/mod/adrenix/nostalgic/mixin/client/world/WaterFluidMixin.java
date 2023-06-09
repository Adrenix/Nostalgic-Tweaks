package mod.adrenix.nostalgic.mixin.client.world;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WaterFluid.class)
public abstract class WaterFluidMixin
{
    /**
     * Prevents the spawning of underwater particles.
     * Controlled by the disabled underwater particles tweak.
     */
    @Redirect(method = "animateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private void NT$onAnimateTick(Level level, ParticleOptions particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        if (!ModConfig.Candy.disableUnderwaterParticles())
            level.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
    }
}
