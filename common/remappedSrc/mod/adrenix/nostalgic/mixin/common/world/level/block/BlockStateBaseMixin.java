package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.CandyTweak;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
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
    private void NT$onGetLightBlock(BlockView level, BlockPos pos, CallbackInfoReturnable<Integer> callback)
    {
        if (!ModConfig.Candy.oldWaterLighting())
            return;

        if (NostalgicTweaks.isClient())
        {
            TweakServerCache<Boolean> cache = TweakServerCache.get(CandyTweak.WATER_LIGHTING);
            boolean isVanilla = !NostalgicTweaks.isNetworkVerified();
            boolean isDisabled = cache == null || cache.getServerCache();

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
     * Multiplayer:
     *
     * Occlusion and solid rendering needs to be enabled to prevent rendering issues such as light coming through chests.
     * Controlled by the old chest voxel tweak since this will change block behavior.
     */

    @Inject(method = "isSolidRender", at = @At("HEAD"), cancellable = true)
    private void NT$onIsSolidRender(CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Candy.oldChestVoxel() && ModClientUtil.Block.isBlockOldChest(this.getBlock()))
            callback.setReturnValue(true);
    }

    @Inject(method = "canOcclude", at = @At("HEAD"), cancellable = true)
    private void NT$onCanOcclude(CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Candy.oldChestVoxel() && ModClientUtil.Block.isBlockOldChest(this.getBlock()))
            callback.setReturnValue(true);
    }
}
