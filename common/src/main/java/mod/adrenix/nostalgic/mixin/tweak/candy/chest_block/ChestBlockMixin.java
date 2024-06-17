package mod.adrenix.nostalgic.mixin.tweak.candy.chest_block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.mixin.util.candy.ChestMixinHelper;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin
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
        if (ChestMixinHelper.isOld(blockState))
            return RenderShape.MODEL;

        return renderShape;
    }
}
