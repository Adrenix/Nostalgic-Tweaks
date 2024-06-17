package mod.adrenix.nostalgic.mixin.tweak.candy.block_particles;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeverBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LeverBlock.class)
public abstract class LeverBlockMixin
{
    /**
     * Prevents the redstone particles being emitted by levers when they are activated.
     */
    @WrapWithCondition(
        method = "makeParticle",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/LevelAccessor;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
        )
    )
    private static boolean nt_block_particles$showLeverParticles(LevelAccessor level, ParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        return !CandyTweak.DISABLE_LEVER_PARTICLES.get();
    }
}
