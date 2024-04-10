package mod.adrenix.nostalgic.mixin.tweak.candy.particle_engine;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.mixin.util.candy.ParticleMixinHelper;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin
{
    /**
     * Disables certain particles based on tweak context.
     */
    @ModifyExpressionValue(
        method = "createParticle",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/particle/ParticleEngine;makeParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;"
        )
    )
    private Particle nt_particle_engine$modifyMakeParticle(Particle particle, ParticleOptions options)
    {
        if (ModTweak.ENABLED.get() && particle != null)
            return ParticleMixinHelper.getParticle(particle, options.getType());

        return particle;
    }
}
