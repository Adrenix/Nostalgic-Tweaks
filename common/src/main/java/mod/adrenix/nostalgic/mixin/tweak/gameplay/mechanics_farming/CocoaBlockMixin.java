package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_farming;

import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CocoaBlock.class)
public abstract class CocoaBlockMixin
{
    /**
     * Immediately grows a cocoa block when using a bonemeal item.
     */
    @ModifyArg(
        method = "performBonemeal",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
        )
    )
    private BlockState nt_mechanics_farming$modifyCocoaBlockAge(BlockState blockState)
    {
        return GameplayTweak.INSTANT_BONEMEAL.get() ? blockState.setValue(CocoaBlock.AGE, CocoaBlock.MAX_AGE) : blockState;
    }
}
