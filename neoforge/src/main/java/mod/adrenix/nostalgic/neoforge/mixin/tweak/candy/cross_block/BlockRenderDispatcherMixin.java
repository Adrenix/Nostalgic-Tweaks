package mod.adrenix.nostalgic.neoforge.mixin.tweak.candy.cross_block;

import mod.adrenix.nostalgic.helper.candy.block.cross.CrossBlock;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BlockRenderDispatcher.class)
public abstract class BlockRenderDispatcherMixin
{
    /**
     * Modifies the block state of cross blocks based on tweak context for NeoForge. If there is a better approach to
     * globally modifying block states only for rendering, then that approach needs used.
     */
    @ModifyVariable(
        method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
        at = @At("HEAD"),
        argsOnly = true
    )
    private BlockState nt_neoforge_cross_block$modifyStateOnRenderBatched(BlockState blockState, BlockState arg, BlockPos blockPos, BlockAndTintGetter level)
    {
        return CrossBlock.getState(level, blockState, blockPos);
    }
}
