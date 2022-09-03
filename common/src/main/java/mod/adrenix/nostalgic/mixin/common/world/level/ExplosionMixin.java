package mod.adrenix.nostalgic.mixin.common.world.level;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.Util;
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

    @Shadow public float radius;
    @Shadow @Final private Level level;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;

    /* Unique Helpers */

    private void NT$setExplosionParticles(BlockPos blockPos)
    {
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

    /**
     * Client:
     *
     * Prevent the creation of modern explosion particles.
     * Controlled by old explosion particles tweaks.
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
        if (ModConfig.Candy.oldExplosionParticles() && !ModConfig.Candy.oldMixedExplosionParticles())
            return;
        instance.addParticle(particleData, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    /**
     * Client:
     *
     * Unoptimized explosion particles are created by spawning particles based on radius instead of present blocks.
     * This will bring back the "fuller" explosions but at the expense of losing FPS when there are larger explosions.
     *
     * Controlled by the unoptimized explosions tweak.
     */
    @Inject(method = "finalizeExplosion", at = @At("HEAD"))
    private void NT$onSpawnInitParticles(boolean spawnParticles, CallbackInfo callback)
    {
        if (!ModConfig.Candy.unoptimizedExplosionParticles())
            return;

        ObjectArrayList<BlockPos> blocks = new ObjectArrayList<>();

        for (int x = 0; x < 16; x++)
        {
            for (int y = 0; y < 16; y++)
            {
                for (int z = 0; z < 16; z++)
                {
                    if (x != 0 && x != 15 && y != 0 && y != 15 && z != 0 && z != 15)
                        continue;

                    double dx = (float) x / 15.0F * 2.0F - 1.0F;
                    double dy = (float) y / 15.0F * 2.0F - 1.0F;
                    double dz = (float) z / 15.0F * 2.0F - 1.0F;
                    double magnitude = Mth.length(dx, dy, dz);

                    dx /= magnitude;
                    dy /= magnitude;
                    dz /= magnitude;

                    double posX = this.x;
                    double posY = this.y;
                    double posZ = this.z;
                    float border = this.radius * (0.7F + this.level.random.nextFloat() * 0.6F);

                    for (float i = border; i > 0.0F; i -= 0.225F)
                    {
                        if (Math.random() > 0.96)
                            blocks.add(new BlockPos(posX, posY, posZ));

                        posX += dx * (double) 0.3F;
                        posY += dy * (double) 0.3F;
                        posZ += dz * (double) 0.3F;
                    }
                }
            }
        }

        Util.shuffle(blocks, this.level.random);

        for (BlockPos blockPos : blocks)
            this.NT$setExplosionParticles(blockPos);
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
        if (ModConfig.Candy.unoptimizedExplosionParticles() || !ModConfig.Candy.oldExplosionParticles() || !spawnParticles)
            return;

        this.NT$setExplosionParticles(blockPos);
    }
}
