package mod.adrenix.nostalgic.fabric.mixin.client;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilFabricMixin
{
    /**
     * Draws the old semi-transparent black background for the tooltip. Controlled by the old tooltip tweak.
     */
    @ModifyArg(
        method = "renderTooltipBackground",
        index = 8,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderRectangle(Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil$BlitPainter;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIII)V"
        )
    )
    private static int NT$onRenderRectangle(int vanilla)
    {
        return ModConfig.Candy.oldTooltips() ? 0xC0000000 : vanilla;
    }

    /**
     * Makes the horizontal purple lines invisible. Controlled by the old tooltip tweak.
     */
    @ModifyArg(
        method = "renderTooltipBackground",
        index = 7,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderHorizontalLine(Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil$BlitPainter;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIII)V"
        )
    )
    private static int NT$onRenderHorizontalLines(int vanilla)
    {
        return ModConfig.Candy.oldTooltips() ? 0 : vanilla;
    }

    /**
     * Makes the vertical purple lines invisible. Controlled by the old tooltip tweak.
     */
    @ModifyArg(
        method = "renderTooltipBackground",
        index = 7,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderVerticalLine(Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil$BlitPainter;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIII)V"
        )
    )
    private static int NT$onRenderVerticalLines(int vanilla)
    {
        return ModConfig.Candy.oldTooltips() ? 0 : vanilla;
    }

    /**
     * Makes the top horizontal frame color gradient invisible. Controlled by the old tooltip tweak.
     */
    @ModifyArg(
        method = "renderTooltipBackground",
        index = 8,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderFrameGradient(Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil$BlitPainter;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"
        )
    )
    private static int NT$onRenderFrameGradientTop(int vanilla)
    {
        return ModConfig.Candy.oldTooltips() ? 0 : vanilla;
    }

    /**
     * Makes the bottom horizontal frame color gradient invisible. Controlled by the old tooltip tweak.
     */
    @ModifyArg(
        method = "renderTooltipBackground",
        index = 9,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderFrameGradient(Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil$BlitPainter;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"
        )
    )
    private static int NT$onRenderFrameGradientBottom(int vanilla)
    {
        return ModConfig.Candy.oldTooltips() ? 0 : vanilla;
    }
}