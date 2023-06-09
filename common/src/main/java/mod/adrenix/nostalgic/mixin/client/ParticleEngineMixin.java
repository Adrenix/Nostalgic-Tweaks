package mod.adrenix.nostalgic.mixin.client;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin
{
    /* Shadows */

    @Shadow public abstract void add(Particle effect);
    @Shadow protected ClientLevel level;

    /* Injections */

    /**
     * Prevents the creation of particles on the client.
     * Controlled by various tweaks.
     */
    @Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
    private void NT$onCreateParticle(ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> callback)
    {
        ParticleType<?> particle = particleData.getType();

        if (ModConfig.Candy.oldSweepParticles() && particle == ParticleTypes.SWEEP_ATTACK)
            callback.setReturnValue(null);
        else if (ModConfig.Candy.oldNoDamageParticles() && particle == ParticleTypes.DAMAGE_INDICATOR)
            callback.setReturnValue(null);
        else if (ModConfig.Candy.oldNoCriticalHitParticles() && particle == ParticleTypes.CRIT)
            callback.setReturnValue(null);
        else if (ModConfig.Candy.oldNoEnchantHitParticles() && particle == ParticleTypes.ENCHANTED_HIT)
            callback.setReturnValue(null);
        else if (ModConfig.Candy.disableNetherParticles())
        {
            boolean isNether = particle == ParticleTypes.ASH ||
                particle == ParticleTypes.WHITE_ASH ||
                particle == ParticleTypes.WARPED_SPORE ||
                particle == ParticleTypes.CRIMSON_SPORE
            ;

            if (isNether)
                callback.setReturnValue(null);
        }
    }

    /**
     * Brings back the old block destruction particles. This disables model influenced particle creation.
     * Controlled by the disable model based destruction particles.
     */
    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true)
    private void NT$onDestroy(BlockPos blockPos, BlockState state, CallbackInfo callback)
    {
        if (!ModConfig.Candy.disableModelDestructionParticles() || state.isAir())
            return;

        int posX = blockPos.getX();
        int posY = blockPos.getY();
        int posZ = blockPos.getZ();

        for (int x = 0; x < 4; x++)
        {
            for (int y = 0; y < 4; y++)
            {
                for (int z = 0; z < 4; z++)
                {
                    double dx = (double) posX + ((double) x + 0.5D) / 4.0D;
                    double dy = (double) posY + ((double) y + 0.5D) / 4.0D;
                    double dz = (double) posZ + ((double) z + 0.5D) / 4.0D;

                    double speedX = dx - posX - 0.5D;
                    double speedY = dy - posY - 0.5D;
                    double speedZ = dz - posZ - 0.5D;

                    this.add(new TerrainParticle(this.level, dx, dy, dz, speedX, speedY, speedZ, state, blockPos));
                }
            }
        }

        callback.cancel();
    }
}
