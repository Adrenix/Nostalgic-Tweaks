package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.CandyTweak;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import mod.adrenix.nostalgic.util.client.NetClientUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TrappedChestBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
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
    private void NT$onGetRenderShape(BlockState state, CallbackInfoReturnable<BlockRenderType> callback)
    {
        if (NostalgicTweaks.isServer())
            return;

        if (ModClientUtil.Block.isBlockOldChest(state.getBlock()))
            callback.setReturnValue(BlockRenderType.MODEL);
    }

    /**
     * Multiplayer:
     *
     * Changes the voxel shape of the chest to be a full block.
     * Controlled by the old chest voxel tweak.
     */
    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void NT$onGetShape(BlockState state, BlockView level, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> callback)
    {
        TweakServerCache<Boolean> cache = TweakServerCache.get(CandyTweak.CHEST_VOXEL);
        VoxelShape shape = VoxelShapes.fullCube();

        if (NostalgicTweaks.isServer() && ModConfig.Candy.oldChestVoxel())
            callback.setReturnValue(shape);
        else if (NostalgicTweaks.isClient())
        {
            boolean isServerVoxel = cache != null && cache.getServerCache();

            if (NostalgicTweaks.isNetworkVerified() && NetClientUtil.isMultiplayer() && isServerVoxel)
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
