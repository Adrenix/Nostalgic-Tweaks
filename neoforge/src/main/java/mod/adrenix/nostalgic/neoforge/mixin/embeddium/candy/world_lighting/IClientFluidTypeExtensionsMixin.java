package mod.adrenix.nostalgic.neoforge.mixin.embeddium.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.world.BlockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IClientFluidTypeExtensions.class)
public interface IClientFluidTypeExtensionsMixin
{
    /**
     * Redirects the water fluid renderer to the vanilla liquid block renderer when Embeddium is installed.
     */
    @ModifyReturnValue(
        method = "renderFluid",
        at = @At("RETURN")
    )
    default boolean nt_embeddium_world_lighting$modifyRenderFluid(boolean skipRendering, FluidState fluidState, BlockAndTintGetter level, BlockPos blockPos, VertexConsumer vertexConsumer, BlockState blockState)
    {
        if (CandyTweak.OLD_WATER_LIGHTING.get() && BlockUtil.isWaterLike(blockState))
        {
            Minecraft.getInstance()
                .getBlockRenderer()
                .getLiquidBlockRenderer()
                .tesselate(level, blockPos, vertexConsumer, blockState, fluidState);

            return true;
        }

        return skipRendering;
    }
}
