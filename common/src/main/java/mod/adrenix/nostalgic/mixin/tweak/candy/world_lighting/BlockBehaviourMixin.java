package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin
{
    /**
     * Changes the darkness of the shade that forms on the corner of blocks.
     */
    @ModifyExpressionValue(
        method = "getShadeBrightness",
        at = @At(
            value = "CONSTANT",
            args = "floatValue=0.2F"
        )
    )
    private float nt_world_lighting$modifyShadeDarkness(float shadeBrightness)
    {
        return CandyTweak.OLD_SMOOTH_LIGHTING.get() ? 0.0F : shadeBrightness;
    }

    /**
     * Changes the shade brightness of leaves blocks.
     */
    @ModifyReturnValue(
        method = "getShadeBrightness",
        at = @At("RETURN")
    )
    private float nt_world_lighting$modifyShadeBrightness(float shadeBrightness, BlockState blockState)
    {
        if (blockState.getBlock() instanceof LeavesBlock)
            return CandyTweak.OLD_LEAVES_LIGHTING.get() ? 1.0F : shadeBrightness;

        return shadeBrightness;
    }
}
