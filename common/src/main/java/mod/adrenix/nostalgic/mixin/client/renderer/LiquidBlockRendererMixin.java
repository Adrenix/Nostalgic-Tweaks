package mod.adrenix.nostalgic.mixin.client.renderer;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.WorldClientUtil;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlockRenderer.class)
public abstract class LiquidBlockRendererMixin
{
    /**
     * Bypasses the modern water lighting to simulate old water rendering. This is done by checking for the largest
     * light level anywhere around the water block. The largest value is used during liquid rendering.
     *
     * Controlled by the old water lighting tweak.
     */
    @Inject(method = "getLightColor", at = @At("HEAD"), cancellable = true)
    private void NT$onGetLightColor(BlockAndTintGetter level, BlockPos source, CallbackInfoReturnable<Integer> callback)
    {
        if (ModConfig.Candy.oldWaterLighting())
            callback.setReturnValue(WorldClientUtil.getWaterLight(level, source));
    }
}
