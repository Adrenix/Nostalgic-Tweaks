package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
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
        if (!MixinConfig.Candy.oldWaterLighting() || !Minecraft.getInstance().hasSingleplayerServer())
            return;

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
}
