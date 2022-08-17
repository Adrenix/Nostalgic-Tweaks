package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.CandyTweak;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.client.BlockClientUtil;
import mod.adrenix.nostalgic.util.client.NetUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin
{
    /**
     * Client:
     *
     * Changes the render shape to be that of a model. The JSON models will instruct the vanilla renderer to show a
     * full chest block.
     */
    @Inject(method = "getRenderShape", at = @At("HEAD"), cancellable = true)
    private void NT$onGetRenderShape(BlockState state, CallbackInfoReturnable<RenderShape> callback)
    {
        if (NostalgicTweaks.isServer())
            return;

        if (BlockClientUtil.isOldChest(state.getBlock()))
            callback.setReturnValue(RenderShape.MODEL);
    }

    /**
     * Multiplayer:
     *
     * Changes the voxel shape of the chest to be a full block.
     * Controlled by the old chest voxel tweak.
     */
    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void NT$onGetShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> callback)
    {
        TweakServerCache<Boolean> cache = TweakServerCache.get(CandyTweak.CHEST_VOXEL);
        VoxelShape shape = Shapes.block();

        if (NostalgicTweaks.isServer() && ModConfig.Candy.oldChestVoxel())
            callback.setReturnValue(shape);
        else if (NostalgicTweaks.isClient())
        {
            boolean isServerVoxel = cache != null && cache.getServerCache();

            if (NostalgicTweaks.isNetworkVerified() && NetUtil.isMultiplayer() && isServerVoxel)
            {
                callback.setReturnValue(shape);
                return;
            }

            if (!ModConfig.Candy.oldChestVoxel())
                return;
            else if (!ModConfig.Candy.oldChest() && state.getBlock().getClass().equals(ChestBlock.class))
                return;
            else if (!ModConfig.Candy.oldTrappedChest() && state.getBlock().getClass().equals(TrappedChestBlock.class))
                return;
            callback.setReturnValue(shape);
        }
    }
}
