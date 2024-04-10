package mod.adrenix.nostalgic.mixin.util.candy;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import org.jetbrains.annotations.Nullable;

/**
 * This utility class is used only by the client.
 */
public abstract class ParticleMixinHelper
{
    /**
     * Get a particle to use based on tweak context.
     *
     * @param particle The original {@link Particle} instance.
     * @param type     The {@link ParticleType} of the given particle.
     * @return A {@code nullable} {@link Particle} to use.
     */
    @Nullable
    public static Particle getParticle(Particle particle, ParticleType<?> type)
    {
        if (CandyTweak.OLD_SWEEP_PARTICLES.get() && type == ParticleTypes.SWEEP_ATTACK)
            return null;
        else if (CandyTweak.OLD_NO_DAMAGE_PARTICLES.get() && type == ParticleTypes.DAMAGE_INDICATOR)
            return null;
        else if (CandyTweak.OLD_NO_CRIT_PARTICLES.get() && type == ParticleTypes.CRIT)
            return null;
        else if (CandyTweak.OLD_NO_MAGIC_HIT_PARTICLES.get() && type == ParticleTypes.ENCHANTED_HIT)
            return null;
        else if (CandyTweak.DISABLE_NETHER_PARTICLES.get())
        {
            if (typeEqualTo(type, ParticleTypes.ASH, ParticleTypes.WHITE_ASH, ParticleTypes.WARPED_SPORE, ParticleTypes.CRIMSON_SPORE))
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
