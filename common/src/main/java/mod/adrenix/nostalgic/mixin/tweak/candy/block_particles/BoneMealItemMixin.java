package mod.adrenix.nostalgic.mixin.tweak.candy.block_particles;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BoneMealItem.class)
public abstract class BoneMealItemMixin
{
    /**
     * Prevents the spawning of growth particles when using the bone meal item.
     */
    @WrapWithCondition(
        method = "addGrowthParticles",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/LevelAccessor;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
        )
    )
    private static boolean nt_block_particles$shouldAddGrowthParticles(LevelAccessor level, ParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        return !CandyTweak.DISABLE_GROWTH_PARTICLES.get();
    }
}
