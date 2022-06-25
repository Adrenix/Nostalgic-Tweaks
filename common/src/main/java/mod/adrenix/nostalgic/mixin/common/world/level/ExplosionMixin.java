package mod.adrenix.nostalgic.mixin.common.world.level;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import mod.adrenix.nostalgic.common.config.MixinConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Explosion.class)
public abstract class ExplosionMixin
{
    /* Shadows */

    @Shadow @Final private Level level;
    @Shadow @Final private float radius;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;

    /**
     * Client:
     *
     * Prevent the creation of modern explosion particles.
     * Controlled by the old explosion particles tweak.
     */
    @Redirect
    (
        method = "finalizeExplosion",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
        )
    )
    private void NT$onFinalizeExplosion(Level instance, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        if (MixinConfig.Candy.oldExplosionParticles() && !MixinConfig.Candy.oldMixedExplosionParticles())
            return;
        instance.addParticle(particleData, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    /**
     * Client:
     *
     * Brings back the classic generic explosion particles.
     * Controlled by the old explosion particles tweak.
     */
    @Inject
    (
        method = "finalizeExplosion",
        locals = LocalCapture.CAPTURE_FAILSOFT,
        at = @At
        (
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"
        )
    )
    private void NT$onSpawnParticles(boolean spawnParticles, CallbackInfo callback, boolean damageTerrain, ObjectArrayList<?> list, boolean isSourcePlayer, ObjectListIterator<?> iterator, BlockPos blockPos)
    {
        if (!MixinConfig.Candy.oldExplosionParticles() || !spawnParticles)
            return;

        double randX = (float) blockPos.getX() + this.level.getRandom().nextFloat();
        double randY = (float) blockPos.getY() + this.level.getRandom().nextFloat();
        double randZ = (float) blockPos.getZ() + this.level.getRandom().nextFloat();
        double dx = randX - this.x;
        double dy = randY - this.y;
        double dz = randZ - this.z;

        double magnitude = Mth.length(dx, dy, dz);
        dx /= magnitude;
        dy /= magnitude;
        dz /= magnitude;

        double rand = (0.5D / (magnitude / (double) this.radius + 0.1D)) * (double) this.level.getRandom().nextFloat() * this.level.getRandom().nextFloat() + 0.3F;
        dx *= rand;
        dy *= rand;
        dz *= rand;

        this.level.addParticle(ParticleTypes.POOF, (randX + this.x) / 2.0D, (randY + this.y) / 2.0D, (randZ + this.z) / 2.0D, dx, dy, dz);
        this.level.addParticle(ParticleTypes.SMOKE, randX, randY, randZ, dx, dy, dz);
    }
}
