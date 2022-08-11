package mod.adrenix.nostalgic.mixin.client;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleManager.class)
public abstract class ParticleEngineMixin
{
    /**
     * Prevents the creation of particles on the client.
     * Controlled by various tweaks.
     */
    @Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
    private void NT$onCreateParticle(ParticleEffect particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> callback)
    {
        ParticleType<?> particle = particleData.getType();
        if (ModConfig.Candy.oldSweepParticles() && particle == ParticleTypes.SWEEP_ATTACK)
            callback.cancel();
        else if (ModConfig.Candy.oldNoDamageParticles() && particle == ParticleTypes.DAMAGE_INDICATOR)
            callback.cancel();
        else if (ModConfig.Candy.oldNoCriticalHitParticles() && particle == ParticleTypes.CRIT)
            callback.cancel();
        else if (ModConfig.Candy.oldNoEnchantHitParticles() && particle == ParticleTypes.ENCHANTED_HIT)
            callback.cancel();
        else if (ModConfig.Candy.disableNetherParticles())
        {
            boolean isNether = particle == ParticleTypes.ASH ||
                particle == ParticleTypes.WHITE_ASH ||
                particle == ParticleTypes.WARPED_SPORE ||
                particle == ParticleTypes.CRIMSON_SPORE
            ;

            if (isNether)
                callback.cancel();
        }
    }
}
