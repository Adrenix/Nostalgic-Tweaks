package mod.adrenix.nostalgic.mixin.common;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin
{
    /**
     * Overrides the light blocking to simulate old leaves from alpha/beta.
     * Controlled by the old lighting toggle.
     */

    @Inject(method = "getLightBlock", at = @At(value = "HEAD"), cancellable = true)
    protected void onGetLightBlock(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Integer> callback)
    {
        callback.setReturnValue(MixinConfig.Candy.oldLighting() ? 0 : 1);
    }

    /**
     * Overrides shade brightness to simulate old leaves shading from alpha/beta.
     * Controlled by the old lighting toggle.
     */

    @Unique
    @SuppressWarnings("unused")
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return MixinConfig.Candy.oldLighting() ? 1.0F : state.isCollisionShapeFullBlock(level, pos) ? 0.2F : 1.0F;
    }
}
