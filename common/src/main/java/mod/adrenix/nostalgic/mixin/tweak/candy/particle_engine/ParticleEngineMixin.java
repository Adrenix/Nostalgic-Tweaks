package mod.adrenix.nostalgic.mixin.tweak.candy.particle_engine;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.mixin.util.candy.ParticleMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin
{
    /* Shadows */

    @Shadow protected ClientLevel level;

    @Shadow
    public abstract void add(Particle effect);

    /* Injections */

    /**
     * Disables certain particles based on tweak context.
     */
    @ModifyExpressionValue(
        method = "createParticle",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/particle/ParticleEngine;makeParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;"
        )
    )
    private Particle nt_particle_engine$modifyMakeParticle(Particle particle, ParticleOptions options, double x, double y, double z)
    {
        if (ModTweak.ENABLED.get() && particle != null)
            return ParticleMixinHelper.getParticle(particle, options.getType(), x, y, z);

        return particle;
    }

    /**
     * Prevents the block's model from influencing the number of destruction particles.
     */
    @WrapWithCondition(
        method = "destroy",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/shapes/VoxelShape;forAllBoxes(Lnet/minecraft/world/phys/shapes/Shapes$DoubleLineConsumer;)V"
        )
    )
    private boolean nt_particle_engine$shouldUseModelForDestruction(VoxelShape shape, Shapes.DoubleLineConsumer consumer, BlockPos blockPos, BlockState blockState)
    {
        if (!CandyTweak.DISABLE_MODEL_DESTRUCTION_PARTICLES.get())
            return true;

        ParticleMixinHelper.destroy(this::add, this.level, blockPos, blockState);

        return false;
    }
}
