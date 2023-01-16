package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(StemBlock.class)
public abstract class StemBlockMixin
{
    /* Shadows */

    @Shadow @Final private StemGrownBlock fruit;

    /* Injections */

    /**
     * Immediately grows a stem block and places a fruit block nearby if possible.
     * Controlled by the instant bone meal tweak.
     */
    @Inject(method = "performBonemeal", at = @At("HEAD"), cancellable = true)
    private void NT$onPerformBonemeal(ServerLevel level, Random random, BlockPos sourcePos, BlockState sourceState, CallbackInfo callback)
    {
        if (!ModConfig.Gameplay.instantBonemeal())
            return;

        level.setBlock(sourcePos, sourceState.setValue(StemBlock.AGE, StemBlock.MAX_AGE), 2);

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos relativePos = sourcePos.relative(direction);
            BlockState relativeState = level.getBlockState(relativePos.below());

            if (level.getBlockState(relativePos).isAir() && (relativeState.is(Blocks.FARMLAND) || relativeState.is(BlockTags.DIRT)))
            {
                level.setBlockAndUpdate(relativePos, this.fruit.defaultBlockState());
                level.setBlockAndUpdate(sourcePos, this.fruit.getAttachedStem().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, direction));

                callback.cancel();
                return;
            }
        }

        callback.cancel();
    }
}
