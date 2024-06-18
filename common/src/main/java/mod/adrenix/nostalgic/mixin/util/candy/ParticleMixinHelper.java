package mod.adrenix.nostalgic.mixin.util.candy;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.CameraUtil;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * This utility class is used only by the client.
 */
public abstract class ParticleMixinHelper
{
    /**
     * Brings back the old block destruction particles. This disables a block's model from influencing particle
     * creation.
     *
     * @param add         A {@link Consumer} that accepts a new {@link Particle} instance.
     * @param clientLevel The {@link ClientLevel} instance.
     * @param blockPos    The {@link BlockPos} instance to spawn particles at.
     * @param blockState  The {@link BlockState} instance to get block context from.
     */
    public static void destroy(Consumer<Particle> add, ClientLevel clientLevel, BlockPos blockPos, BlockState blockState)
    {
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

                    add.accept(new TerrainParticle(clientLevel, dx, dy, dz, speedX, speedY, speedZ, blockState, blockPos));
                }
            }
        }
    }

    /**
     * Get a particle to use based on tweak context.
     *
     * @param particle The original {@link Particle} instance.
     * @param type     The {@link ParticleType} of the given particle.
     * @param x        The x-coordinate of the particle.
     * @param y        The y-coordinate of the particle.
     * @param z        The z-coordinate of the particle.
     * @return A {@code nullable} {@link Particle} to use.
     */
    @Nullable
    public static Particle getParticle(Particle particle, ParticleType<?> type, double x, double y, double z)
    {
        if (CandyTweak.OLD_SWEEP_PARTICLES.get() && type == ParticleTypes.SWEEP_ATTACK)
            return null;
        else if (CandyTweak.OLD_NO_DAMAGE_PARTICLES.get() && type == ParticleTypes.DAMAGE_INDICATOR)
            return null;
        else if (CandyTweak.OLD_NO_CRIT_PARTICLES.get() && type == ParticleTypes.CRIT)
            return null;
        else if (CandyTweak.OLD_NO_MAGIC_HIT_PARTICLES.get() && type == ParticleTypes.ENCHANTED_HIT)
            return null;
        else if (CandyTweak.DISABLE_UNDERWATER_PARTICLES.get() && type == ParticleTypes.UNDERWATER)
            return null;
        else if (CandyTweak.DISABLE_WATER_DRIP_PARTICLES.get() && type == ParticleTypes.DRIPPING_WATER)
            return null;
        else if (CandyTweak.DISABLE_LAVA_PARTICLES.get() && type == ParticleTypes.LAVA)
            return null;
        else if (CandyTweak.DISABLE_LAVA_DRIP_PARTICLES.get() && type == ParticleTypes.DRIPPING_LAVA)
            return null;
        else if (CandyTweak.DISABLE_NETHER_PARTICLES.get() && GameUtil.isInNether())
        {
            if (typeEqualTo(type, ParticleTypes.ASH, ParticleTypes.WHITE_ASH, ParticleTypes.WARPED_SPORE, ParticleTypes.CRIMSON_SPORE))
                return null;
        }

        if (getPlayerParticle(particle, type, x, y, z) == null)
            return null;

        ResourceLocation key = BuiltInRegistries.PARTICLE_TYPE.getKey(type);

        if (key != null && CandyTweak.DISABLED_PARTICLES.get().contains(key.toString()))
            return null;

        return particle;
    }

    /**
     * Get a player particle to use based on tweak context.
     *
     * @param particle The original {@link Particle} instance.
     * @param type     The {@link ParticleType} of the given particle.
     * @param x        The x-coordinate of the particle.
     * @param y        The y-coordinate of the particle.
     * @param z        The z-coordinate of the particle.
     * @return A {@code nullable} {@link Particle} to use.
     */
    @Nullable
    private static Particle getPlayerParticle(Particle particle, ParticleType<?> type, double x, double y, double z)
    {
        boolean isEffectParticle = typeEqualTo(type, ParticleTypes.ENTITY_EFFECT);

        if (!CandyTweak.HIDE_FIRST_PERSON_MAGIC_PARTICLES.get() || !CameraUtil.isFirstPerson() || !isEffectParticle)
            return particle;

        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null)
        {
            double playerX = player.getX();
            double playerY = player.getY();
            double playerZ = player.getZ();

            boolean isNearX = MathUtil.tolerance(x, playerX, 2.0D);
            boolean isNearY = MathUtil.tolerance(y, playerY, 2.0D);
            boolean isNearZ = MathUtil.tolerance(z, playerZ, 2.0D);

            if (isNearX && isNearY && isNearZ)
                return null;
        }

        return particle;
    }

    /**
     * Check if the given type matches any of the given varargs types.
     *
     * @param check The {@link ParticleType} instance to check.
     * @param types A varargs of {@link ParticleType} instances to check against.
     * @return Whether the given type matched one of the given types.
     */
    private static boolean typeEqualTo(ParticleType<?> check, ParticleType<?>... types)
    {
        for (ParticleType<?> type : types)
        {
            if (type == check)
                return true;
        }

        return false;
    }
}
