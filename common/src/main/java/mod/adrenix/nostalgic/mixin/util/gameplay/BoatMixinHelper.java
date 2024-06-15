package mod.adrenix.nostalgic.mixin.util.gameplay;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

/**
 * This utility class is used by both the client and server.
 */
public abstract class BoatMixinHelper
{
    /**
     * Check if the given boat has a water lift status.
     *
     * @param status The {@link Boat.Status} value.
     * @return Whether the boat is in a water lift.
     */
    public static boolean isWaterLift(Boat.Status status)
    {
        return status == Boat.Status.UNDER_WATER || status == Boat.Status.UNDER_FLOWING_WATER;
    }

    /**
     * Get the amount of gravity applied to the boat.
     *
     * @param boat   The {@link Boat} to check.
     * @param status The {@link Boat.Status} value.
     * @return The amount of gravity to apply to the boat.
     */
    public static double getGravityAmount(Boat boat, Boat.Status status)
    {
        return boat.getDeltaMovement().y + (boat.isNoGravity() || isWaterLift(status) ? 0.0D : -0.04D);
    }

    /**
     * Get the amount of vertical lift to apply to a boat.
     *
     * @param y The y-value to add lift amount to.
     * @return The amount of lift to apply to vertical delta movement.
     */
    public static double getLiftAmount(double y)
    {
        return Mth.clamp(y + 0.025D, y, 0.5D);
    }

    /**
     * Add splash particles under the boat if it is moving fast enough.
     *
     * @param boat The {@link Boat} to apply particles to.
     */
    public static void applyParticles(Boat boat)
    {
        Vec3 movement = boat.getDeltaMovement();
        double motionX = movement.x;
        double motionY = movement.y;
        double motionZ = movement.z;
        double prevDistance = Math.sqrt(motionX * motionX + motionZ * motionZ);

        if (prevDistance > 0.2625D)
        {
            RandomSource randomSource = RandomSource.create();

            Vec3 position = boat.position();
            double posX = position.x;
            double posY = position.y;
            double posZ = position.z;
            double rotationYaw = boat.getYRot();

            double x = Math.cos((rotationYaw * Math.PI) / 180.0D);
            double y = Math.sin((rotationYaw * Math.PI) / 180.0D);

            for (int i = 0; (double) i < 1.0D + prevDistance * 60.0D; i++)
            {
                double randX = randomSource.nextFloat() * 2.0F - 1.0F;
                double randY = (double) (randomSource.nextInt(2) * 2 - 1) * 0.7D;

                if (randomSource.nextBoolean())
                {
                    double px = (posX - x * randX * 0.8D) + y * randY;
                    double py = posY + 0.5D;
                    double pz = posZ - y * randX * 0.8D - x * randY;

                    boat.level().addParticle(ParticleTypes.SPLASH, px, py, pz, motionX, motionY, motionZ);
                }
            }
        }
    }
}
