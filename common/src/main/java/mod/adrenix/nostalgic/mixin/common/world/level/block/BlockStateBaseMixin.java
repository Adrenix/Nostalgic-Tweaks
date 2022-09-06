package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.CandyTweak;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.common.BlockCommonUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin
{
    /* Shadows */

    @Shadow public abstract Block getBlock();

    /**
     * Multiplayer:
     *
     * Changes the amount of light blocked by water related blocks from 1 level to 3.
     * Controlled by the old water lighting tweak.
     *
     * This tweak makes changes to the world. When this tweak is toggled the vanilla
     * optimized world feature in the world edit menu will need to be used.
     *
     * Optimization will only work if the cached world data is erased.
     */

    @Inject(method = "getLightBlock", at = @At("HEAD"), cancellable = true)
    private void NT$onGetLightBlock(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Integer> callback)
    {
        if (!ModConfig.Candy.oldWaterLighting())
            return;

        if (NostalgicTweaks.isClient())
        {
            TweakServerCache<Boolean> cache = TweakServerCache.get(CandyTweak.WATER_LIGHTING);
            boolean isVanilla = !NostalgicTweaks.isNetworkVerified();
            boolean isDisabled = cache == null || !cache.getServerCache();

            if (isVanilla || isDisabled)
                return;
        }

        Block block = this.getBlock();

        boolean isBlockWaterRelated = block == Blocks.WATER ||
            block == Blocks.ICE ||
            block == Blocks.FROSTED_ICE ||
            block == Blocks.BLUE_ICE ||
            block == Blocks.PACKED_ICE
        ;

        if (isBlockWaterRelated)
            callback.setReturnValue(3);
    }

    /**
     * Client:
     *
     * The following injections only impact the rendering aspects of chests. Therefore, there is no need to restrict
     * these injections to server-side only. The purpose of these injections is to improve performance when chests are
     * shaped like blocks.
     *
     * Controlled by various old chest tweaks.
     */

    @Inject(method = "canOcclude", at = @At("HEAD"), cancellable = true)
    private void NT$onCanOcclude(CallbackInfoReturnable<Boolean> callback)
    {
        if (NostalgicTweaks.isClient() && BlockCommonUtil.isOldChest(this.getBlock()))
            callback.setReturnValue(false);
    }

    @Inject(method = "getFaceOcclusionShape", at = @At("HEAD") , cancellable = true)
    private void NT$onGetFaceOcclusionShape(BlockGetter level, BlockPos pos, Direction direction, CallbackInfoReturnable<VoxelShape> callback)
    {
        if (NostalgicTweaks.isClient() && BlockCommonUtil.isOldChest(this.getBlock()))
            callback.setReturnValue(Shapes.block());
    }

    /**
     * Multiplayer:
     *
     * Setting the shape to a full block prevents light from coming through chests. Because chests are being changed to
     * a full-block voxel shape, this should be restricted by a server-only tweak to prevent vanilla multiplayer issues.
     *
     * Controlled by the old chest voxel tweak and various old chest tweaks.
     */

    @Inject(method = "isSolidRender", at = @At("HEAD"), cancellable = true)
    private void NT$onIsSolidRender(CallbackInfoReturnable<Boolean> callback)
    {
        if (NostalgicTweaks.isClient() && ModConfig.Candy.oldChestVoxel() && BlockCommonUtil.isOldChest(this.getBlock()))
            callback.setReturnValue(true);
    }

    @Inject
    (
        cancellable = true,
        method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
        at = @At("HEAD")
    )
    private void NT$onGetShape(BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> callback)
    {
        if (NostalgicTweaks.isClient() && ModConfig.Candy.oldChestVoxel() && BlockCommonUtil.isOldChest(this.getBlock()))
            callback.setReturnValue(Shapes.block());
    }
}
