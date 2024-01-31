package mod.adrenix.nostalgic.mixin.tweak.candy.chest_block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderChestBlock.class)
public abstract class EnderChestBlockMixin
{
    /**
     * Changes the render shape to be that of a model. The JSON models defined in a resource pack will define how the
     * chest appears in the world.
     */
    @ModifyReturnValue(
        method = "getRenderShape",
        at = @At("RETURN")
    )
    private RenderShape nt_chest_block$modifyRenderShape(RenderShape renderShape, BlockState blockState)
    {
        if (CandyTweak.OLD_ENDER_CHEST.get())
            return RenderShape.MODEL;

        return renderShape;
    }

    /**
     * Prevents the addition of portal particles around an ender chest.
     */
    @WrapWithCondition(
        method = "animateTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
        )
    )
    private boolean nt_chest_block$wrapParticles(Level level, ParticleOptions particleOptions, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        return !CandyTweak.DISABLE_ENDER_CHEST_PARTICLES.get();
    }
}
