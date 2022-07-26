package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.client.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderChestBlock.class)
public abstract class EnderChestBlockMixin
{
    /**
     * Changes the render shape to be that of a model. The JSON models will instruct the vanilla renderer to show a
     * full ender chest block.
     */
    @Inject(method = "getRenderShape", at = @At("HEAD"), cancellable = true)
    private void NT$onGetRenderShape(BlockState state, CallbackInfoReturnable<RenderShape> callback)
    {
        if (ModConfig.Candy.oldEnderChest())
            callback.setReturnValue(RenderShape.MODEL);
    }

    /**
     * Prevents the addition of portal particles around an ender chest.
     * Controlled by the old ender chest tweak.
     */
    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void NT$onAnimateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldEnderChest())
            callback.cancel();
    }

    /**
     * Changes the voxel shape of the ender chest to be a full block.
     * Controlled by the old chest voxel tweak.
     */
    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void NT$onGetShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> callback)
    {
        if (!ModConfig.Candy.oldChestVoxel() || !ModConfig.Candy.oldEnderChest())
            return;
        callback.setReturnValue(Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0));
    }
}
