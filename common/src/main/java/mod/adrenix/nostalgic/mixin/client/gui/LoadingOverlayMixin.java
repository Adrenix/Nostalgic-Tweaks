package mod.adrenix.nostalgic.mixin.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.ModConfig;
import mod.adrenix.nostalgic.client.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.Util;
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

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin
{
    /* Shadows */

    @Shadow @Final static ResourceLocation MOJANG_STUDIOS_LOGO_LOCATION;
    @Shadow @Final private Consumer<Optional<Throwable>> onFinish;
    @Shadow @Final private ReloadInstance reload;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private boolean fadeIn;
    @Shadow private float currentProgress;
    @Shadow private long fadeInStart;
    @Shadow private long fadeOutStart;

    @Shadow protected abstract void drawProgressBar(PoseStack poseStack, int minX, int minY, int maxX, int maxY, float alpha);

    /**
     * Overrides the overlay renderer, so we can display a retro loading screen.
     * Controlled by various interface tweaks.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void NT$onRender(PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo callback)
    {
        TweakVersion.Overlay overlay = ModConfig.Candy.getLoadingOverlay();
        if (overlay == TweakVersion.Overlay.MODERN)
            return;

        long millis = Util.getMillis();
        int width = this.minecraft.getWindow().getGuiScaledWidth();
        int height = this.minecraft.getWindow().getGuiScaledHeight();

        if (this.fadeIn && this.fadeInStart == -1L)
            this.fadeInStart = millis;

        float fadeOut = this.fadeOutStart > -1L ? (float)(millis - this.fadeOutStart) / 1000.0F : -1.0F;
        float fadeIn = this.fadeInStart > -1L ? (float)(millis - this.fadeInStart) / 500.0F : -1.0F;
        int color = overlay == TweakVersion.Overlay.ALPHA ?
            FastColor.ARGB32.color(255, 55, 51, 99) :
            FastColor.ARGB32.color(255, 255, 255, 255)
        ;

        final ResourceLocation BACKGROUND = switch (overlay)
        {
            case ALPHA -> NostalgicUtil.Resource.MOJANG_ALPHA;
            case BETA -> NostalgicUtil.Resource.MOJANG_BETA;
            case RELEASE_ORANGE -> NostalgicUtil.Resource.MOJANG_RELEASE_ORANGE;
            case RELEASE_BLACK -> NostalgicUtil.Resource.MOJANG_RELEASE_BLACK;
            default -> MOJANG_STUDIOS_LOGO_LOCATION;
        };

        float r = (float) (color >> 16 & 0xFF) / 255.0F;
        float g = (float) (color >> 8 & 0xFF) / 255.0F;
        float b = (float) (color & 0xFF) / 255.0F;

        LoadingOverlay.fill(poseStack, 0, 0, width, height, color);
        GlStateManager._clearColor(r, g, b, 1.0F);
        GlStateManager._clear(16384, Minecraft.ON_OSX);

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

        if (fadeOut >= 2.0F)
            this.minecraft.setOverlay(null);

        if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || fadeIn >= 2.0F))
        {
            try
            {
                this.reload.checkExceptions();
                this.onFinish.accept(Optional.empty());
            }
            catch (Throwable throwable)
            {
                this.onFinish.accept(Optional.of(throwable));
            }

            this.fadeOutStart = Util.getMillis();

            if (this.minecraft.screen != null)
                this.minecraft.screen.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
        }

        callback.cancel();
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
