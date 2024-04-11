package mod.adrenix.nostalgic.mixin.tweak.candy.player_particles;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    /**
     * Disables the spawning of sprint particles.
     */
    @WrapWithCondition(
        method = "spawnSprintParticle",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
        )
    )
    private boolean nt_player_particles$shouldSpawnSprintParticles(Level level, ParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        return !CandyTweak.DISABLE_SPRINTING_PARTICLES.get();
    }
}
