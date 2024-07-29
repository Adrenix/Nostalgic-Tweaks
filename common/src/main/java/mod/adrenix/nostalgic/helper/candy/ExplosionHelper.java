package mod.adrenix.nostalgic.helper.candy;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/**
 * This utility class is used only by the client.
 */
public abstract class ExplosionHelper
{
    /**
     * Adds the old explosion particles to the world.
     *
     * @param level    The {@link Level} instance.
     * @param blockPos The {@link BlockPos} of where to add particles.
     * @param radius   The radius of the explosion.
     * @param x        The x-coordinate of the explosion.
     * @param y        The y-coordinate of the explosion.
     * @param z        The z-coordinate of the explosion.
     */
    public static void addExplosionParticles(Level level, BlockPos blockPos, float radius, double x, double y, double z)
    {
        Supplier<Float> random = level.getRandom()::nextFloat;
        double smokeX = (float) blockPos.getX() + random.get();
        double smokeY = (float) blockPos.getY() + random.get();
        double smokeZ = (float) blockPos.getZ() + random.get();
        double dx = smokeX - x;
        double dy = smokeY - y;
        double dz = smokeZ - z;

        double magnitude = Mth.length(dx, dy, dz);
        dx /= magnitude;
        dy /= magnitude;
        dz /= magnitude;

        double rand = (0.5D / (magnitude / (double) radius + 0.1D)) * (double) random.get() * random.get() + 0.3F;
        dx *= rand;
        dy *= rand;
        dz *= rand;

        double poofX = (smokeX + x) / 2.0D;
        double poofY = (smokeY + y) / 2.0D;
        double poofZ = (smokeZ + z) / 2.0D;

        level.addParticle(ParticleTypes.POOF, poofX, poofY, poofZ, dx, dy, dz);
        level.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, dx, dy, dz);
    }

    /**
     * Add unoptimized explosion particles to the world.
     *
     * @param level  The {@link Level} instance.
     * @param radius The radius of the explosion.
     * @param x      The x-coordinate of the explosion.
     * @param y      The y-coordinate of the explosion.
     * @param z      The z-coordinate of the explosion.
     */
    public static void addUnoptimizedExplosionParticles(Level level, float radius, double x, double y, double z)
    {
        ObjectArrayList<BlockPos> blocks = new ObjectArrayList<>();

        for (int offsetX = 0; offsetX < 16; offsetX++)
        {
            for (int offsetY = 0; offsetY < 16; offsetY++)
            {
                for (int offsetZ = 0; offsetZ < 16; offsetZ++)
                {
                    if (offsetX != 0 && offsetX != 15 && offsetY != 0 && offsetY != 15 && offsetZ != 0 && offsetZ != 15)
                        continue;

                    double dx = (float) offsetX / 15.0F * 2.0F - 1.0F;
                    double dy = (float) offsetY / 15.0F * 2.0F - 1.0F;
                    double dz = (float) offsetZ / 15.0F * 2.0F - 1.0F;
                    double magnitude = Mth.length(dx, dy, dz);

                    dx /= magnitude;
                    dy /= magnitude;
                    dz /= magnitude;

                    double posX = x;
                    double posY = y;
                    double posZ = z;
                    float border = radius * (0.7F + level.random.nextFloat() * 0.6F);

                    for (float i = border; i > 0.0F; i -= 0.225F)
                    {
                        if (Math.random() > 0.96D)
                            blocks.add(BlockPos.containing(posX, posY, posZ));

                        posX += dx * (double) 0.3F;
                        posY += dy * (double) 0.3F;
                        posZ += dz * (double) 0.3F;
                    }
                }
            }
        }

        Util.shuffle(blocks, level.random);

        for (BlockPos blockPos : blocks)
            addExplosionParticles(level, blockPos, radius, x, y, z);
    }
}
