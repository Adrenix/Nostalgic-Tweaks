package mod.adrenix.nostalgic.mixin.tweak.candy.block_particles;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BoneMealItem.class)
public abstract class BoneMealItemMixin
{
    /**
     * Prevents the spawning of in-block growth particles when using the bone meal item.
     */
    @WrapWithCondition(
        method = "addGrowthParticles",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/ParticleUtils;spawnParticleInBlock(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;ILnet/minecraft/core/particles/ParticleOptions;)V"
        )
    )
    private static boolean nt_block_particles$shouldAddGrowthParticlesInBlock(LevelAccessor level, BlockPos blockPos, int count, ParticleOptions particle)
    {
        return !CandyTweak.DISABLE_GROWTH_PARTICLES.get();
    }

    /**
     * Prevents the spawning of neighboring growth particles when using the bone meal item.
     */
    @WrapWithCondition(
        method = "addGrowthParticles",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/ParticleUtils;spawnParticles(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;IDDZLnet/minecraft/core/particles/ParticleOptions;)V"
        )
    )
    private static boolean nt_block_particles$shouldAddNeighborGrowthParticles(LevelAccessor level, BlockPos blockPos, int count, double xzSpread, double ySpread, boolean allowInAir, ParticleOptions particle)
    {
        return !CandyTweak.DISABLE_GROWTH_PARTICLES.get();
    }
}
