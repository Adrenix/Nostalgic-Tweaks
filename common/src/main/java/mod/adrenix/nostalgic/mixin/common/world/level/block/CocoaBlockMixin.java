package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(CocoaBlock.class)
public abstract class CocoaBlockMixin
{
    /**
     * Immediately grows a cocoa block when using a bone meal item.
     * Controlled by the instant bone meal tweak.
     */
    @Inject(method = "performBonemeal", at = @At("HEAD"), cancellable = true)
    private void NT$onPerformBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state, CallbackInfo callback)
    {
        if (!ModConfig.Gameplay.instantBonemeal())
            return;

        level.setBlock(pos, state.setValue(CocoaBlock.AGE, CocoaBlock.MAX_AGE), 2);
        callback.cancel();
    }
}
