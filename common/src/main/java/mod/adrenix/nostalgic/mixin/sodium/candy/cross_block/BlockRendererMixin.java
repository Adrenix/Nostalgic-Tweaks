package mod.adrenix.nostalgic.mixin.sodium.candy.cross_block;

import mod.adrenix.nostalgic.helper.candy.block.cross.CrossBlock;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin extends AbstractBlockRenderContext
{
    /**
     * Modifies the block state of cross blocks based on tweak context for Sodium. If there is a better approach to
     * globally modifying block states only for rendering, then that approach needs used. This is not a long term
     * solution since we're mixing into the API internals of the mod to apply this client-side trick.
     */
    @ModifyVariable(
        method = "renderModel",
        at = @At("HEAD"),
        argsOnly = true
    )
    private BlockState nt_sodium_cross_block$modifyStateOnRenderModel(BlockState blockState, BakedModel model, BlockState arg, BlockPos blockPos)
    {
        return CrossBlock.getState(this.level, blockState, blockPos);
    }
}
