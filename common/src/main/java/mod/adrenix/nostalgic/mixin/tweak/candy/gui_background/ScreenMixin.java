package mod.adrenix.nostalgic.mixin.tweak.candy.gui_background;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderTarget;
import mod.adrenix.nostalgic.helper.candy.screen.ScreenHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(Screen.class)
public abstract class ScreenMixin
{
    /* Shadows */

    @Shadow public int width;
    @Shadow public int height;

    /* Injections */

    @Shadow public Component title;

    /**
     * Changes the fill gradient background color for textured backgrounds.
     */
    @WrapWithCondition(
        method = "renderMenuBackgroundTexture",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"
        )
    )
    private static boolean nt_gui_background$shouldRenderTexturedBackground(GuiGraphics graphics, Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation texture, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight)
    {
        if (ModTweak.ENABLED.get() && !ScreenHelper.hasDirtBackground(texture))
            return ScreenHelper.renderColoredBackground(graphics, width, height);

        return true;
    }

    /**
     * Change the fill gradient background color for the transparent background.
     */
    @WrapWithCondition(
        method = "renderTransparentBackground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V"
        )
    )
    private boolean nt_gui_background$shouldRenderTransparentBackground(GuiGraphics graphics, int x1, int y1, int x2, int y2, int colorFrom, int colorTo)
    {
        if (ModTweak.ENABLED.get())
            return ScreenHelper.renderColoredBackground(graphics, x2, y2);

        return true;
    }

    /**
     * Prevents the processing of the screen blur effect.
     */
    @WrapWithCondition(
        method = "renderBlurredBackground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GameRenderer;processBlurEffect()V"
        )
    )
    private boolean nt_gui_background$shouldProcessBlurEffect(GameRenderer gameRenderer)
    {
        return !CandyTweak.REMOVE_SCREEN_BLUR.get();
    }

    /**
     * Prevents writing the blur effect to the main render target.
     */
    @WrapWithCondition(
        method = "renderBlurredBackground",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V"
        )
    )
    private boolean nt_gui_background$shouldBindWriteBlurEffect(RenderTarget renderTarget, boolean setViewport)
    {
        return !CandyTweak.REMOVE_SCREEN_BLUR.get();
    }

    /**
     * Darkens the menu background if using the dirt texture.
     */
    @WrapOperation(
        method = "renderMenuBackgroundTexture",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"
        )
    )
    private static void nt_gui_background$preRenderMenuBackgroundTexture(GuiGraphics graphics, Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation texture, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight, Operation<Void> original)
    {
        if (ScreenHelper.hasDirtBackground(texture))
            graphics.blit(renderTypeGetter, texture, x, y, uOffset, vOffset, width, height, textureWidth, textureHeight, 0xFF404040);
        else
            original.call(graphics, renderTypeGetter, texture, x, y, uOffset, vOffset, width, height, textureWidth, textureHeight);
    }

    /**
     * Changes the background texture used by the screen.
     */
    @ModifyArg(
        method = "renderMenuBackgroundTexture",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"
        )
    )
    private static ResourceLocation nt_gui_background$shouldRenderMenuBackground(ResourceLocation texture)
    {
        if (ScreenHelper.hasDirtBackground(texture))
            return TextureLocation.DIRT_BACKGROUND;

        return texture;
    }
}
