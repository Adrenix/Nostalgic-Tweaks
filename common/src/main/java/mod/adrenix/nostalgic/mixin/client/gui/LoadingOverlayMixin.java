package mod.adrenix.nostalgic.mixin.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.DefaultConfig;
import mod.adrenix.nostalgic.client.config.MixinConfig;
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
        DefaultConfig.VERSION overlay = MixinConfig.Candy.getLoadingOverlay();
        if (overlay == DefaultConfig.VERSION.MODERN)
            return;

        long millis = Util.getMillis();
        int width = this.minecraft.getWindow().getGuiScaledWidth();
        int height = this.minecraft.getWindow().getGuiScaledHeight();

        if (this.fadeIn && this.fadeInStart == -1L)
            this.fadeInStart = millis;

        float fadeOut = this.fadeOutStart > -1L ? (float)(millis - this.fadeOutStart) / 1000.0F : -1.0F;
        float fadeIn = this.fadeInStart > -1L ? (float)(millis - this.fadeInStart) / 500.0F : -1.0F;

        final int BACKGROUND_COLOR = switch (overlay)
        {
            case ALPHA -> FastColor.ARGB32.color(255, 55, 51, 99);
            case BETA -> FastColor.ARGB32.color(255, 255, 255, 255);
            default -> 0;
        };

        final ResourceLocation BACKGROUND = switch (overlay)
        {
            case ALPHA -> NostalgicUtil.Resource.MOJANG_ALPHA;
            case BETA -> NostalgicUtil.Resource.MOJANG_BETA;
            default -> MOJANG_STUDIOS_LOGO_LOCATION;
        };

        float r = (float) (BACKGROUND_COLOR >> 16 & 0xFF) / 255.0F;
        float g = (float) (BACKGROUND_COLOR >> 8 & 0xFF) / 255.0F;
        float b = (float) (BACKGROUND_COLOR & 0xFF) / 255.0F;

        LoadingOverlay.fill(poseStack, 0, 0, width, height, BACKGROUND_COLOR);
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

        int barHeight = MixinConfig.Candy.getLoadingOverlay() == DefaultConfig.VERSION.BETA ? (int) (height * 0.95) : (int) (height * 0.85);
        float actualProgress = this.reload.getActualProgress();
        this.currentProgress = Mth.clamp(this.currentProgress * 0.95F + actualProgress * 0.050000012F, 0.0F, 1.0F);

        if (!MixinConfig.Candy.removeLoadingBar())
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
     * Changes the color and position of the progress bar when using the beta style overlay.
     * Controlled by various tweaks.
     */
    @Redirect(method = "drawProgressBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FastColor$ARGB32;color(IIII)I"))
    private int NT$onDrawProgressBar(int alpha, int red, int green, int blue)
    {
        if (MixinConfig.Candy.getLoadingOverlay() != DefaultConfig.VERSION.BETA)
            return FastColor.ARGB32.color(255, 255, 255, 255);
        return FastColor.ARGB32.color(255, 221, 79, 59);
    }
}
