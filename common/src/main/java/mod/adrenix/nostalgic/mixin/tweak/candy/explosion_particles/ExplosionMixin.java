package mod.adrenix.nostalgic.mixin.tweak.candy.explosion_particles;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import mod.adrenix.nostalgic.helper.candy.ExplosionHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public abstract class ExplosionMixin
{
    /* Shadows */

    @Shadow public float radius;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;
    @Shadow @Final private Level level;

    /* Injections */

    /**
     * Prevents the creation of the modern explosion particles.
     */
    @WrapWithCondition(
        method = "finalizeExplosion",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
        )
    )
    private boolean nt_explosion_particles$shouldAddParticles(Level level, ParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        return !CandyTweak.OLD_EXPLOSION_PARTICLES.get() || CandyTweak.OLD_MIXED_EXPLOSION_PARTICLES.get();
    }

    /**
     * Adds unoptimized explosion particles by spawning particles based on a radius instead of spawning particles based
     * on the number of blocks that were destroyed.
     */
    @Inject(
        method = "finalizeExplosion",
        at = @At("HEAD")
    )
    private void nt_explosion_particles$onFinalizeExplosion(CallbackInfo callback)
    {
        if (!CandyTweak.UNOPTIMIZED_EXPLOSION_PARTICLES.get())
            return;

        ExplosionHelper.addUnoptimizedExplosionParticles(this.level, this.radius, this.x, this.y, this.z);
    }

    /**
     * Brings back the classic generic explosion particles.
     */
    @Inject(
        method = "finalizeExplosion",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/Util;shuffle(Ljava/util/List;Lnet/minecraft/util/RandomSource;)V"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private void nt_explosion_particles$onSpawnExplosionParticles(boolean spawnParticles, CallbackInfo callback, @Local(ordinal = 0) BlockPos blockPos)
    {
        if (CandyTweak.UNOPTIMIZED_EXPLOSION_PARTICLES.get() || !CandyTweak.OLD_EXPLOSION_PARTICLES.get() || !spawnParticles)
            return;

        ExplosionHelper.addExplosionParticles(this.level, blockPos, this.radius, this.x, this.y, this.z);
    }
}
