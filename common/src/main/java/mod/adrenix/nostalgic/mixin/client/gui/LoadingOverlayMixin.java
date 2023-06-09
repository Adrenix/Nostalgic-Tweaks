package mod.adrenix.nostalgic.mixin.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin
{
    /* Shadows */

    @Shadow @Final static ResourceLocation MOJANG_STUDIOS_LOGO_LOCATION;
    @Shadow @Final private Minecraft minecraft;
    @Shadow private long fadeOutStart;

    /**
     * @return Will be truthful as long as the current old overlay is not set to modern.
     */
    private static boolean isModified()
    {
        return ModConfig.Candy.getLoadingOverlay() != TweakVersion.Overlay.MODERN;
    }

    /* Injections */

    /**
     * Prevents the title screen from rendering until the fade out is finished.
     * Controlled by the old overlay tweak.
     */
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
    private void NT$onRenderTitleScreen(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        float fade = this.fadeOutStart > -1L ? (float) (Util.getMillis() - this.fadeOutStart) / 1000.0F : -1.0F;

        if (!isModified() || fade >= 2.0F)
            screen.render(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Changes the shader color to be fully transparent before the modern overlay logo renders.
     * Controlled by the old overlay tweak.
     */
    @ModifyArg(method = "render", index = 3, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;setColor(FFFF)V"))
    private float NT$onSetColor(float vanilla)
    {
        return isModified() ? 0.0F : vanilla;
    }

    /**
     * Renders an old Mojang logo before the progress bar is rendered.
     * Controlled by the old overlay tweak.
     */
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableBlend()V"))
    private void NT$onRenderLogo(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo callback)
    {
        if (!isModified())
            return;

        int width = this.minecraft.getWindow().getGuiScaledWidth();
        int height = this.minecraft.getWindow().getGuiScaledHeight();

        ResourceLocation background = switch (ModConfig.Candy.getLoadingOverlay())
        {
            case ALPHA -> TextureLocation.MOJANG_ALPHA;
            case BETA -> TextureLocation.MOJANG_BETA;
            case RELEASE_ORANGE -> TextureLocation.MOJANG_RELEASE_ORANGE;
            case RELEASE_BLACK -> TextureLocation.MOJANG_RELEASE_BLACK;
            default -> MOJANG_STUDIOS_LOGO_LOCATION;
        };

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        graphics.blit(background, 0, 0, width, height, 0, 0, 1, 1, 128, 128);

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.scale(2.0F, 2.0F, 2.0F);

        int x = (int) ((width / 4.0) - (128 / 2));
        int y = (int) ((height / 4.0) - (128 / 2));

        graphics.blit(background, x, y, 0, 0, 128, 128, 128, 128);

        poseStack.popPose();
    }

    /**
     * This keeps the progress bar rendered until the fade out is done.
     * Controlled by the old overlay tweak.
     */
    @ModifyConstant
    (
        method = "render",
        constant = @Constant(floatValue = 1.0F, ordinal = 1),
        slice = @Slice
        (
            from = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ReloadInstance;getActualProgress()F"),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/LoadingOverlay;drawProgressBar(Lnet/minecraft/client/gui/GuiGraphics;IIIIF)V")
        )
    )
    private float NT$onCheckAlphaForProgressBar(float vanilla)
    {
        return isModified() ? 3.0F : vanilla;
    }

    /**
     * Prevents the progress bar from rendering if the user has opted-in.
     * Controlled by the remove loading bar tweak.
     */
    @Inject(method = "drawProgressBar", at = @At("HEAD"), cancellable = true)
    private void NT$onDrawProgressBar(GuiGraphics graphics, int minX, int minY, int maxX, int maxY, float partialTick, CallbackInfo callback)
    {
        if (ModConfig.Candy.removeLoadingBar())
            callback.cancel();
    }

    /**
     * Changes the progress bar height based on the current old overlay.
     * @param height The vanilla height.
     * @return A new height value that is always different from the given height.
     */
    private static int getBarHeight(int height)
    {
        return switch (ModConfig.Candy.getLoadingOverlay())
        {
            case ALPHA -> (int) (height * 0.85);
            case BETA -> (int) (height * 0.95);
            default -> (int) (height * 0.69);
        };
    }

    /**
     * Changes the bottom-y position of the progress bar.
     * Controlled by the old overlay tweak.
     */
    @ModifyVariable(method = "drawProgressBar", argsOnly = true, ordinal = 1, at = @At("HEAD"))
    private int NT$onProgressBarMinY(int vanilla)
    {
        return isModified() ? getBarHeight(this.minecraft.getWindow().getGuiScaledHeight()) - 5 : vanilla;
    }

    /**
     * Changes the top-y position of the progress bar.
     * Controlled by the old overlay tweak.
     */
    @ModifyVariable(method = "drawProgressBar", argsOnly = true, ordinal = 3, at = @At("HEAD"))
    private int NT$onProgressBarMaxY(int vanilla)
    {
        return isModified() ? getBarHeight(this.minecraft.getWindow().getGuiScaledHeight()) + 5 : vanilla;
    }

    /**
     * Changes the color of the entire progress bar.
     * Inner rectangle is color is changed below this redirect.
     *
     * Controlled by various states of the old overlay tweak.
     */
    @Redirect(method = "drawProgressBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FastColor$ARGB32;color(IIII)I"))
    private int NT$onSetProgressBarColor(int alpha, int red, int green, int blue)
    {
        TweakVersion.Overlay overlay = ModConfig.Candy.getLoadingOverlay();

        if (ModTracker.OPTIFINE.isInstalled())
        {
            return switch (overlay)
            {
                case ALPHA -> FastColor.ARGB32.color(255, 255, 255, 255);
                case RELEASE_BLACK -> FastColor.ARGB32.color(255, 221, 31, 42);
                case BETA, RELEASE_ORANGE -> FastColor.ARGB32.color(255, 221, 79, 59);
                case MODERN -> FastColor.ARGB32.color(255, red, green, blue);
            };
        }

        return switch(overlay)
        {
            case ALPHA -> FastColor.ARGB32.color(255, 142, 132, 255);
            case RELEASE_BLACK -> FastColor.ARGB32.color(255, 4, 7, 7);
            case BETA, RELEASE_ORANGE -> FastColor.ARGB32.color(255, 221, 79, 59);
            case MODERN -> FastColor.ARGB32.color(255, red, green, blue);
        };
    }

    /**
     * Changes the color of the inner rectangle inside the progress bar.
     * Controlled by various states of the old overlay tweak.
     */
    @ModifyArg
    (
        method = "drawProgressBar",
        index = 4,
        at = @At
        (
            ordinal = 0,
            value = "INVOKE",
            target = "net/minecraft/client/gui/GuiGraphics.fill(IIIII)V"
        )
    )
    private int NT$onFillInnerProgressBar(int vanilla)
    {
        return switch (ModConfig.Candy.getLoadingOverlay())
        {
            case ALPHA, MODERN -> FastColor.ARGB32.color(255, 255, 255, 255);
            case BETA, RELEASE_ORANGE -> FastColor.ARGB32.color(255, 246, 136, 62);
            case RELEASE_BLACK -> FastColor.ARGB32.color(255, 221, 31, 42);
        };
    }
}
