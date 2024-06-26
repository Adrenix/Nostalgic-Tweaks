package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.mixin.util.candy.lighting.LightingMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.world.BlockUtil;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LiquidBlockRenderer.class)
public abstract class LiquidBlockRendererMixin
{
    /**
     * Simulates old water rendering with old water lighting by making block as bright as its surroundings.
     */
    @ModifyReturnValue(
        method = "getLightColor",
        at = @At("RETURN")
    )
    private int nt_world_lighting$modifyLightColor(int color, BlockAndTintGetter level, BlockPos blockPos)
    {
        if (CandyTweak.OLD_WATER_LIGHTING.get() && BlockUtil.isWaterLike(level.getBlockState(blockPos)))
            return LightingMixinHelper.getWaterLight(level, blockPos);

        return color;
    }
}
