package mod.adrenix.nostalgic.mixin.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin
{
    /* Shadows */

    @Shadow @Final static ResourceLocation MOJANG_STUDIOS_LOGO_LOCATION;
    @Shadow @Final private ReloadInstance reload;
    @Shadow @Final private Minecraft minecraft;
    @Shadow private float currentProgress;
    @Shadow protected abstract void drawProgressBar(PoseStack poseStack, int minX, int minY, int maxX, int maxY, float alpha);

    /* Injections */

    /**
     * Overrides the overlay renderer, so we can display a retro loading screen.
     * Controlled by various interface tweaks.
     */
    @Inject(method = "render", at = @At("RETURN"))
    private void NT$onRender(PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo callback)
    {
        TweakVersion.Overlay overlay = ModConfig.Candy.getLoadingOverlay();

        if (overlay == TweakVersion.Overlay.MODERN)
            return;

        int width = this.minecraft.getWindow().getGuiScaledWidth();
        int height = this.minecraft.getWindow().getGuiScaledHeight();

        int color = overlay == TweakVersion.Overlay.ALPHA ?
            FastColor.ARGB32.color(255, 55, 51, 99) :
            FastColor.ARGB32.color(255, 255, 255, 255)
        ;

        final ResourceLocation BACKGROUND = switch (overlay)
        {
            case ALPHA -> TextureLocation.MOJANG_ALPHA;
            case BETA -> TextureLocation.MOJANG_BETA;
            case RELEASE_ORANGE -> TextureLocation.MOJANG_RELEASE_ORANGE;
            case RELEASE_BLACK -> TextureLocation.MOJANG_RELEASE_BLACK;
            default -> MOJANG_STUDIOS_LOGO_LOCATION;
        };

        float r = (float) (color >> 16 & 0xFF) / 255.0F;
        float g = (float) (color >> 8 & 0xFF) / 255.0F;
        float b = (float) (color & 0xFF) / 255.0F;

        LoadingOverlay.fill(poseStack, 0, 0, width, height, color);
        RenderSystem.clearColor(r, g, b, 1.0F);
        RenderSystem.clear(16384, Minecraft.ON_OSX);

        double longest = Math.min(width * 0.75, height) * 0.25;
        int scaleW = (int) ((longest * 4.0) * 0.5);

        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.pushPose();
        poseStack.scale(2.0F, 2.0F, 2.0F);
        LoadingOverlay.blit(poseStack, (int) ((width / 4.0) - (128 / 2)), (int) ((height / 4.0) - (128 / 2)), 0, 0, 128, 128, 128, 128);
        poseStack.popPose();

        int barHeight = switch (overlay)
        {
            case ALPHA -> (int) (height * 0.85);
            case BETA -> (int) (height * 0.95);
            default -> (int) (height * 0.69);
        };

        float actualProgress = this.reload.getActualProgress();
        this.currentProgress = Mth.clamp(this.currentProgress * 0.95F + actualProgress * 0.050000012F, 0.0F, 1.0F);

        if (!ModConfig.Candy.removeLoadingBar())
            this.drawProgressBar(poseStack, width / 2 - scaleW, barHeight - 5, width / 2 + scaleW, barHeight + 5, 1.0F);

        if (this.reload.isDone())
            this.minecraft.setOverlay(null);
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
        return switch(ModConfig.Candy.getLoadingOverlay())
        {
            case ALPHA -> FastColor.ARGB32.color(255, 142, 132, 255);
            case BETA, RELEASE_ORANGE -> FastColor.ARGB32.color(255, 221, 79, 59);
            case RELEASE_BLACK -> FastColor.ARGB32.color(255, 4, 7, 7);
            case MODERN -> FastColor.ARGB32.color(255, 255, 255, 255);
        };
    }

    /**
     * Changes the color of the inner rectangle inside the progress bar.
     * Controlled by various states of the old overlay tweak.
     */
    @ModifyArg
    (
        method = "drawProgressBar",
        index = 5,
        at = @At
        (
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/LoadingOverlay;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"
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
