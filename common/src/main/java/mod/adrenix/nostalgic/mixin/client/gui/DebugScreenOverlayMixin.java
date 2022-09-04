package mod.adrenix.nostalgic.mixin.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.mixin.widen.IMixinLevelRenderer;
import mod.adrenix.nostalgic.mixin.widen.IMixinMinecraft;
import mod.adrenix.nostalgic.util.common.ModUtil;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin extends GuiComponent
{
    /* Shadows */

    @Shadow @Final private Font font;
    @Shadow protected abstract void drawChart(PoseStack poseStack, FrameTimer frameTimer, int startX, int width, boolean drawForFps);
    @Shadow protected abstract int getSampleColor(int height, int heightMin, int heightMid, int heightMax);

    /* Injections */

    /**
     * Changes the rendering of the debugging screen based on the given state of the tweak.
     * Controlled by the old debug screen tweak.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void NT$onRender(PoseStack poseStack, CallbackInfo callback)
    {
        TweakVersion.Generic debug = ModConfig.Candy.getDebugScreen();
        Minecraft minecraft = Minecraft.getInstance();

        if (debug.equals(TweakVersion.Generic.MODERN) || minecraft.level == null || minecraft.getCameraEntity() == null)
            return;

        String overlay = ModConfig.Candy.getOverlayText();
        String title = overlay.isEmpty() ? "Minecraft " + SharedConstants.getCurrentVersion().getName() : overlay;
        String fps = String.format(" (%s fps, %s chunk updates)", IMixinMinecraft.NT$getFPS(), minecraft.levelRenderer.getChunkRenderDispatcher().getToUpload());
        String chunks = String.format("C: %d/%d. F: 0, O: 0, E: 0", minecraft.levelRenderer.countRenderedChunks(), (long) minecraft.levelRenderer.getTotalChunks());
        String entities = String.format("E: %s/%s. B: %s, I: 0", ((IMixinLevelRenderer) minecraft.levelRenderer).NT$getRenderedEntities(), minecraft.level.getEntityCount(), ((IMixinLevelRenderer) minecraft.levelRenderer).NT$getCulledEntities());
        String particles = String.format("P: %s. T: All: %s", minecraft.particleEngine.countParticles(), minecraft.level.getEntityCount());
        String overflow = String.format(" (%s fps)", IMixinMinecraft.NT$getFPS());

        BlockPos blockPos = minecraft.getCameraEntity().blockPosition();
        int clientLight = minecraft.level.getChunkSource().getLightEngine().getRawBrightness(blockPos, 0);
        int skyLight = minecraft.level.getBrightness(LightLayer.SKY, blockPos);
        int blockLight = minecraft.level.getBrightness(LightLayer.BLOCK, blockPos);
        String light = String.format("Client Light: %d (%d sky, %d block)", clientLight, skyLight, blockLight);

        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = total - free;

        String memory = String.format("Used memory: %2d%% (%03dMB) of %03dMB", used * 100L / max, ModUtil.Numbers.bytesToMegabytes(used), ModUtil.Numbers.bytesToMegabytes(max));
        String allocated = String.format("Allocated memory: %2d%% (%03dMB)", total * 100L / max, ModUtil.Numbers.bytesToMegabytes(total));

        if (this.font.width(title + fps) + 2 > minecraft.getWindow().getGuiScaledWidth() - 2 - this.font.width(memory))
            fps = overflow;

        ArrayList<String> left = Lists.newArrayList(title + fps, chunks, entities, particles);
        ArrayList<String> right = Lists.newArrayList(memory, allocated);

        if (debug.equals(TweakVersion.Generic.BETA))
        {
            left.add(String.format("ChunkCache: %d", minecraft.level.getChunkSource().getLoadedChunksCount()));

            if (ModConfig.Candy.displayLightLevels())
                left.add(light);

            left.add("");
            left.add(String.format("X: %f", minecraft.getCameraEntity().getX()));
            left.add(String.format("Y: %f", minecraft.getCameraEntity().getY()));
            left.add(String.format("Z: %f", minecraft.getCameraEntity().getZ()));
        }
        else if (ModConfig.Candy.displayLightLevels())
            left.add(light);

        for (int i = 0; i < left.size(); i++)
        {
            String text = left.get(i);

            if (Strings.isNullOrEmpty(text))
                continue;

            this.font.drawShadow(poseStack, text, 2.0F, 2.0F + this.font.lineHeight * i, 0xFFFFFF);
        }

        for (int i = 0; i < right.size(); i++)
        {
            String text = right.get(i);

            if (Strings.isNullOrEmpty(text))
                continue;

            int x = minecraft.getWindow().getGuiScaledWidth() - 2 - this.font.width(text);
            int y = 2 + this.font.lineHeight * i;

            this.font.drawShadow(poseStack, text, (float) x, (float) y, 0xE0E0E0);
        }

        this.drawChart(poseStack, minecraft.getFrameTimer(), 0, Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2, true);

        callback.cancel();
    }

    /**
     * Changes the visuals of the FPS chart. This change to the FPS chart will only occur when the old debug screen is
     * enabled.
     *
     * Controlled by the old debug screen and draw for FPS flag.
     */
    @Inject(method = "drawChart", at = @At("HEAD"), cancellable = true)
    private void NT$onDrawChart(PoseStack poseStack, FrameTimer frameTimer, int startX, int width, boolean drawForFps, CallbackInfo callback)
    {
        Minecraft minecraft = Minecraft.getInstance();
        TweakVersion.Generic debug = ModConfig.Candy.getDebugScreen();
        TweakType.DebugChart chart = ModConfig.Candy.getDebugChart();
        boolean isDisabled = chart.equals(TweakType.DebugChart.DISABLED);
        boolean isModern = chart.equals(TweakType.DebugChart.MODERN);
        boolean isOld = chart.equals(TweakType.DebugChart.CLASSIC);

        if (debug.equals(TweakVersion.Generic.MODERN))
            return;
        else if (isDisabled || !drawForFps)
        {
            callback.cancel();
            return;
        }

        RenderSystem.disableDepthTest();

        long[] log = frameTimer.getLog();
        int maxWidth = (int) Math.max(0, log.length - (minecraft.getWindow().getGuiScaledWidth() / 3.5F));
        int endX = log.length - maxWidth - (isOld ? 1 : 0);
        int max = Integer.MAX_VALUE;
        int min = Integer.MIN_VALUE;
        int index = frameTimer.wrapIndex(frameTimer.getLogStart() + maxWidth);
        long avg = 0L;

        for (int i = 0; i < endX; ++i)
        {
            int time = (int) (log[frameTimer.wrapIndex(index + i)] / 1000000L);
            max = Math.min(max, time);
            min = Math.max(min, time);
            avg += time;
        }

        int height = minecraft.getWindow().getGuiScaledHeight();
        int color = isOld ? -1876951040 : -1873784752;

        GuiComponent.fill(poseStack, startX, height - 60, startX + endX, height, color);

        if (isOld)
            GuiComponent.fill(poseStack, startX, height - 32, startX + endX, height - 15, -1879048192);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();

        Matrix4f transform = Transformation.identity().getMatrix();
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        int drawX = startX;
        while (index != frameTimer.getLogEnd())
        {
            int scale = frameTimer.scaleSampleTo(log[index], 30, 60);
            int rgb = this.getSampleColor(Mth.clamp(scale, 0, 100), 0, 50, 100);

            int a = rgb >> 24 & 0xFF;
            int r = rgb >> 16 & 0xFF;
            int g = rgb >> 8 & 0xFF;
            int b = rgb & 0xFF;

            builder.vertex(transform, drawX + 1, height, 0.0F).color(r, g, b, a).endVertex();
            builder.vertex(transform, drawX + 1, height - scale + 1, 0.0F).color(r, g, b, a).endVertex();
            builder.vertex(transform, drawX, height - scale + 1, 0.0F).color(r, g, b, a).endVertex();
            builder.vertex(transform, drawX, height, 0.0F).color(r, g, b, a).endVertex();

            index = frameTimer.wrapIndex(index + 1);
            ++drawX;
        }

        BufferUploader.drawWithShader(builder.end());
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();

        if (isModern)
        {
            GuiComponent.fill(poseStack, startX + 1, height - 30 + 1, startX + 14, height - 30 + 10, -1873784752);
            this.font.draw(poseStack, "60 FPS", (float) (startX + 2), (float) (height - 30 + 2), 0xE0E0E0);
            this.hLine(poseStack, startX, startX + endX - 1, height - 30, -1);

            GuiComponent.fill(poseStack, startX + 1, height - 60 + 1, startX + 14, height - 60 + 10, -1873784752);
            this.font.draw(poseStack, "30 FPS", (float) (startX + 2), (float) (height - 60 + 2), 0xE0E0E0);

            this.hLine(poseStack, startX, startX + endX - 1, height - 60, -1);
            this.hLine(poseStack, startX, startX + endX - 1, height - 1, -1);
            this.vLine(poseStack, startX, height - 60, height, -1);
            this.vLine(poseStack, startX + endX - 1, height - 60, height, -1);
        }

        int limit = minecraft.options.framerateLimit().get();
        if (limit > 0 && limit <= 250)
        {
            int fpsLine = height - 1 - (int) (1800.0 / (double) limit);

            if (isOld)
                this.hLine(poseStack, startX, startX + endX - 1, fpsLine, -65536);
            else
                this.hLine(poseStack, startX + 1, startX + endX - 2, fpsLine, -65536);
        }

        if (isModern)
        {
            String maxed = max + " ms min";
            String average = avg / (long) endX + " ms avg";
            String minimum = min + " ms max";

            this.font.drawShadow(poseStack, maxed, (float) (startX + 2), (float) (height - 70 - this.font.lineHeight), 0xFFFFFF);
            this.font.drawShadow(poseStack, average, (float) (startX + 2), (float) (height - 60 - this.font.lineHeight), 0xFFFFFF);
            this.font.drawShadow(poseStack, minimum, (float) (startX + 2), (float) (height - 80 - this.font.lineHeight), 0xFFFFFF);
        }

        RenderSystem.enableDepthTest();

        callback.cancel();
    }
}
