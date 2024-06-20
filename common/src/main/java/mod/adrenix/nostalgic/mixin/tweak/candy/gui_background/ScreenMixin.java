package mod.adrenix.nostalgic.mixin.tweak.candy.gui_background;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.pipeline.RenderTarget;
import mod.adrenix.nostalgic.mixin.util.candy.ScreenMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin
{
    /* Shadows */

    @Shadow public int width;
    @Shadow public int height;

    /* Injections */

    /**
     * Changes the fill gradient background color for textured backgrounds.
     */
    @WrapWithCondition(
        method = "renderMenuBackgroundTexture",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIFFIIII)V"
        )
    )
    private static boolean nt_gui_background$shouldRenderTexturedBackground(GuiGraphics graphics, ResourceLocation texture, int x, int y, int blitOffset, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight)
    {
        if (ModTweak.ENABLED.get() && !ScreenMixinHelper.hasDirtBackground(texture))
            return ScreenMixinHelper.renderColoredBackground(graphics, width, height);

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
            return ScreenMixinHelper.renderColoredBackground(graphics, x2, y2);

        return true;
    }

    /**
     * Prevents the processing of the screen blur effect.
     */
    @WrapWithCondition(
        method = "renderBlurredBackground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GameRenderer;processBlurEffect(F)V"
        )
    )
    private boolean nt_gui_background$shouldProcessBlurEffect(GameRenderer gameRenderer, float partialTick)
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
    @Inject(
        method = "renderMenuBackgroundTexture",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIFFIIII)V"
        )
    )
    private static void nt_gui_background$preRenderMenuBackgroundTexture(GuiGraphics graphics, ResourceLocation texture, int x, int y, float uOffset, float vOffset, int width, int height, CallbackInfo callback)
    {
        if (ScreenMixinHelper.hasDirtBackground(texture))
            graphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
    }

    /**
     * Changes the background texture used by the screen.
     */
    @ModifyArg(
        method = "renderMenuBackgroundTexture",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIFFIIII)V"
        )
    )
    private static ResourceLocation nt_gui_background$shouldRenderMenuBackground(ResourceLocation texture)
    {
        if (ScreenMixinHelper.hasDirtBackground(texture))
            return TextureLocation.DIRT_BACKGROUND;

        return texture;
    }

    /**
     * Brightens the menu background after drawing the background.
     */
    @Inject(
        method = "renderMenuBackgroundTexture",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIFFIIII)V"
        )
    )
    private static void nt_gui_background$postRenderMenuBackgroundTexture(GuiGraphics graphics, ResourceLocation texture, int x, int y, float uOffset, float vOffset, int width, int height, CallbackInfo callback)
    {
        if (ModTweak.ENABLED.get())
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
