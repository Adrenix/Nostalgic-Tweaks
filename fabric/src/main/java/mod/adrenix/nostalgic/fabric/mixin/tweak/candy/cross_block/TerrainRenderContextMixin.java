package mod.adrenix.nostalgic.fabric.mixin.tweak.candy.cross_block;

import mod.adrenix.nostalgic.helper.candy.block.cross.CrossBlock;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings("UnstableApiUsage")
@Mixin(TerrainRenderContext.class)
public abstract class TerrainRenderContextMixin extends AbstractBlockRenderContext
{
    /**
     * Modifies the block state of cross blocks based on tweak context for the Fabric rendering API. If there is a
     * better approach to modifying block states that is only for rendering, then that approach needs used. This is not
     * a long term solution since we're mixing into the API internals to apply this client-side trick.
     */
    @ModifyVariable(
        method = "tessellateBlock",
        at = @At("HEAD"),
        argsOnly = true
    )
    private BlockState nt_fabric_cross_block$modifyStateOnTessellate(BlockState blockState, BlockState arg, BlockPos blockPos)
    {
        return CrossBlock.getState(this.blockInfo.blockView, blockState, blockPos);
    }
}
