package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeverBlock.class)
public abstract class LeverBlockMixin
{
    /**
     * Disables the redstone particles emitted by levers when activated.
     *
     * Since many users of N.T. will be using the colormatic mod, this injection will cancel at the HEAD position.
     * Modifying the alpha argument in the particle options won't work since colormatic redirects this method.
     *
     * Controlled by the disable lever particles tweak.
     */
    @Inject(method = "makeParticle", at = @At("HEAD"), cancellable = true)
    private static void NT$onMakeParticle(BlockState state, LevelAccessor level, BlockPos pos, float alpha, CallbackInfo callback)
    {
        if (ModConfig.Candy.disableLeverParticles())
            callback.cancel();
    }
}
