package mod.adrenix.nostalgic.forge.mixin.client;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilForgeMixin
{
    /**
     * Draws the old semi-transparent black background for the tooltip. Forge uses a gradient alternative to drawing the
     * tooltip background. This changes the top part of the gradient. Controlled by the old tooltip tweak.
     */
    @ModifyArg(
        remap = false,
        method = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderTooltipBackground(Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil$BlitPainter;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIIIII)V",
        index = 8,
        at = @At(
            remap = false,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderRectangle(Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil$BlitPainter;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"
        )
    )
    private static int NT$onRenderRectangleTop(int vanilla)
    {
        return ModConfig.Candy.oldTooltips() ? 0xC0000000 : vanilla;
    }

    /**
     * Draws the old semi-transparent black background for the tooltip. Forge uses a gradient alternative to drawing the
     * tooltip background. This changes the bottom part of the gradient. Controlled by the old tooltip tweak.
     */
    @ModifyArg(
        remap = false,
        method = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderTooltipBackground(Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil$BlitPainter;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIIIII)V",
        index = 9,
        at = @At(
            remap = false,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderRectangle(Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil$BlitPainter;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"
        )
    )
    private static int NT$onRenderRectangleBottom(int vanilla)
    {
        return ModConfig.Candy.oldTooltips() ? 0xC0000000 : vanilla;
    }

    /**
     * Changes the background top argument provided by Forge. Controlled by the old tooltip tweak.
     */
    @ModifyVariable(
        remap = false,
        method = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderTooltipBackground(Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil$BlitPainter;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIIIII)V",
        ordinal = 5,
        at = @At("HEAD")
    )
    private static int NT$onRenderTooltipBackgroundTop(int vanilla)
    {
        return ModConfig.Candy.oldTooltips() ? 0 : vanilla;
    }

    /**
     * Changes the background bottom argument provided by Forge. Controlled by the old tooltip tweak.
     */
    @ModifyVariable(
        remap = false,
        method = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderTooltipBackground(Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil$BlitPainter;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIIIII)V",
        ordinal = 6,
        at = @At("HEAD")
    )
    private static int NT$onRenderTooltipBackgroundBottom(int vanilla)
    {
        return ModConfig.Candy.oldTooltips() ? 0 : vanilla;
    }

    /**
     * Changes the border top argument provided by Forge. Controlled by the old tooltip tweak.
     */
    @ModifyVariable(
        remap = false,
        method = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderTooltipBackground(Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil$BlitPainter;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIIIII)V",
        ordinal = 7,
        at = @At("HEAD")
    )
    private static int NT$onRenderTooltipBorderTop(int vanilla)
    {
        return ModConfig.Candy.oldTooltips() ? 0 : vanilla;
    }

    /**
     * Changes the border bottom argument provided by Forge. Controlled by the old tooltip tweak.
     */
    @ModifyVariable(
        remap = false,
        method = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderTooltipBackground(Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil$BlitPainter;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIIIII)V",
        ordinal = 8,
        at = @At("HEAD")
    )
    private static int NT$onRenderTooltipBorderBottom(int vanilla)
    {
        return ModConfig.Candy.oldTooltips() ? 0 : vanilla;
    }
}