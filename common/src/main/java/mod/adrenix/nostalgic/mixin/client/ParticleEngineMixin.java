package mod.adrenix.nostalgic.mixin.client;

import mod.adrenix.nostalgic.common.config.MixinConfig;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin
{
    /**
     * Prevents the creation of particles on the client.
     * Controlled by various tweaks.
     */
    @Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
    private void NT$onCreateParticle(ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> callback)
    {
        ParticleType<?> particle = particleData.getType();
        if (MixinConfig.Candy.oldSweepParticles() && particle == ParticleTypes.SWEEP_ATTACK)
            callback.cancel();
        else if (MixinConfig.Candy.oldNoDamageParticles() && particle == ParticleTypes.DAMAGE_INDICATOR)
            callback.cancel();
        else if (MixinConfig.Candy.oldNoCriticalHitParticles() && particle == ParticleTypes.CRIT)
            callback.cancel();
        else if (MixinConfig.Candy.oldNoEnchantHitParticles() && particle == ParticleTypes.ENCHANTED_HIT)
            callback.cancel();
    }
}
