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
import mod.adrenix.nostalgic.mixin.widen.LevelRendererAccessor;
import mod.adrenix.nostalgic.mixin.widen.MinecraftAccessor;
import mod.adrenix.nostalgic.util.common.ColorUtil;
import mod.adrenix.nostalgic.util.common.MathUtil;
import mod.adrenix.nostalgic.util.common.TextUtil;
import mod.adrenix.nostalgic.util.common.WorldCommonUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Map;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin extends GuiComponent
{
    /* Shadows */

    @Shadow @Final private Font font;
    @Shadow @Final private Minecraft minecraft;
    @Shadow protected abstract void drawChart(PoseStack poseStack, FrameTimer frameTimer, int startX, int width, boolean drawForFps);
    @Shadow protected abstract int getSampleColor(int height, int heightMin, int heightMid, int heightMax);
    @Shadow protected abstract String getPropertyValueString(Map.Entry<Property<?>, Comparable<?>> entry);
    @Shadow private static String printBiome(Holder<Biome> biomeHolder) { return null; }
    @Shadow private HitResult block;
    @Shadow private HitResult liquid;

    /* Unique Helpers */

    /**
     * Draws a background color behind a debug line.
     * Controlled by the show debug background tweak.
     */
    @Unique
    private void NT$drawLineBackground(PoseStack poseStack, String info, int index, boolean isLeft)
    {
        if (!ModConfig.Candy.showDebugBackground())
            return;

        int color = ColorUtil.toHexInt(ModConfig.Candy.debugBackgroundColor());
        int scaledWidth = this.minecraft.getWindow().getGuiScaledWidth();
        int fontWidth = this.font.width(info);
        int fontHeight = this.font.lineHeight;

        int minX = isLeft ? 1 : (scaledWidth - 2 - fontWidth) - 1;
        int maxX = isLeft ? 2 + fontWidth + 1 : (scaledWidth - 2 - fontWidth) + fontWidth + 1;
        int minY = (2 + fontHeight * index) - 1;
        int maxY = (2 + fontHeight * index) + fontHeight - 1;

        DebugScreenOverlay.fill(poseStack, minX, minY, maxX, maxY, color);
    }

    /**
     * Draws debug information to the screen.
     * Controlled by the show debug text shadow tweak.
     */
    @Unique
    private void NT$drawInformation(PoseStack poseStack, String text, float x, float y, int color)
    {
        if (ModConfig.Candy.showDebugTextShadow())
            this.font.drawShadow(poseStack, text, x, y, color);
        else
            this.font.draw(poseStack, text, x, y, color);
    }

    /* Injections */

    /**
     * Changes the text rendering to have a shadow when displaying game information.
     * Controlled by the show debug text shadow tweak.
     */
    @Redirect(method = "drawGameInformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;draw(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFI)I"))
    private int NT$onDrawGameInformation(Font font, PoseStack poseStack, String text, float x, float y, int color)
    {
        return ModConfig.Candy.showDebugTextShadow() ? font.drawShadow(poseStack, text, x, y, color) : font.draw(poseStack, text, x, y, color);
    }

    /**
     * Changes the color of the modern debug background when displaying game information.
     * Controlled by the show debug background tweak and debug background color tweak.
     */
    @Redirect(method = "drawGameInformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"))
    private void NT$onDrawGameInformationColor(PoseStack poseStack, int minX, int minY, int maxX, int maxY, int color)
    {
        if (ModConfig.Candy.showDebugBackground())
            DebugScreenOverlay.fill(poseStack, minX, minY, maxX, maxY, ColorUtil.toHexInt(ModConfig.Candy.debugBackgroundColor()));
    }

    /**
     * Changes the text rendering to have a shadow when displaying system information.
     * Controlled by the show debug text shadow tweak.
     */
    @Redirect(method = "drawSystemInformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;draw(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFI)I"))
    private int NT$onDrawSystemInformation(Font font, PoseStack poseStack, String text, float x, float y, int color)
    {
        return ModConfig.Candy.showDebugTextShadow() ? font.drawShadow(poseStack, text, x, y, color) : font.draw(poseStack, text, x, y, color);
    }

    /**
     * Changes the color of the modern debug background when displaying system information.
     * Controlled by the show debug background tweak.
     */
    @Redirect(method = "drawSystemInformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"))
    private void NT$onDrawSystemInformationColor(PoseStack poseStack, int minX, int minY, int maxX, int maxY, int color)
    {
        if (ModConfig.Candy.showDebugBackground())
            DebugScreenOverlay.fill(poseStack, minX, minY, maxX, maxY, ColorUtil.toHexInt(ModConfig.Candy.debugBackgroundColor()));
    }

    /**
     * Changes the rendering of the debugging screen based on the given state of the tweak.
     * Controlled by the old debug screen tweak.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void NT$onRender(PoseStack poseStack, CallbackInfo callback)
    {
        Entity entity = this.minecraft.getCameraEntity();
        TweakVersion.Generic debug = ModConfig.Candy.getDebugScreen();

        if (debug.equals(TweakVersion.Generic.MODERN) || this.minecraft.level == null || entity == null)
            return;

        this.block = entity.pick(20.0D, 0.0F, false);
        this.liquid = entity.pick(20.0D, 0.0F, true);

        boolean isReducedInfo = this.minecraft.showOnlyReducedInfo();
        String overlay = ModConfig.Candy.getOverlayText();
        String title = overlay.isEmpty() ? "Minecraft " + SharedConstants.getCurrentVersion().getName() : overlay;
        String fps = String.format(" (%s fps, %s chunk updates)", MinecraftAccessor.NT$getFPS(), this.minecraft.levelRenderer.getChunkRenderDispatcher().getToUpload());
        String chunks = String.format("C: %d/%d. F: 0, O: 0, E: 0", this.minecraft.levelRenderer.countRenderedChunks(), (long) this.minecraft.levelRenderer.getTotalChunks());
        String entities = String.format("E: %s/%s. B: %s, I: 0", ((LevelRendererAccessor) this.minecraft.levelRenderer).NT$getRenderedEntities(), this.minecraft.level.getEntityCount(), ((LevelRendererAccessor) this.minecraft.levelRenderer).NT$getCulledEntities());
        String particles = String.format("P: %s. T: All: %s", this.minecraft.particleEngine.countParticles(), this.minecraft.level.getEntityCount());
        String overflow = String.format(" (%s fps)", MinecraftAccessor.NT$getFPS());
        BlockPos blockPos = entity.blockPosition();

        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = total - free;
        int width = this.minecraft.getWindow().getGuiScaledWidth();

        String memory = String.format("Used memory: %2d%% (%03dMB) of %03dMB", used * 100L / max, MathUtil.bytesToMegabytes(used), MathUtil.bytesToMegabytes(max));
        String allocated = String.format("Allocated memory: %2d%% (%03dMB)", total * 100L / max, MathUtil.bytesToMegabytes(total));

        if (this.font.width(title + fps) + 2 > width - 2 - this.font.width(memory))
            fps = overflow;

        ArrayList<String> left = Lists.newArrayList(title + fps, chunks, entities, particles);
        ArrayList<String> right = Lists.newArrayList(memory, allocated);

        if (ModConfig.Candy.showDebugGpuUsage())
        {
            String gpu = TextUtil.extract(this.minecraft.fpsString, "GPU:.+");
            String usage = TextUtil.extract(gpu, "\\d+");

            if (!usage.isEmpty())
                right.add("GPU usage: " + TextUtil.getPercentColorHigh(Integer.parseInt(usage)) + "%");
        }

        if (debug.equals(TweakVersion.Generic.BETA))
        {
            left.add(String.format("ChunkCache: %d", this.minecraft.level.getChunkSource().getLoadedChunksCount()));

            if (!isReducedInfo)
            {
                left.add("");
                left.add(String.format("X: %f", entity.getX()));
                left.add(String.format("Y: %f", entity.getY()));
                left.add(String.format("Z: %f", entity.getZ()));
            }
        }

        if (!isReducedInfo)
        {
            left.add("");

            if (ModConfig.Candy.showDebugFacingData())
            {
                Direction direction = entity.getDirection();
                String facing = switch (direction)
                {
                    case NORTH -> "Towards negative Z";
                    case SOUTH -> "Towards positive Z";
                    case WEST -> "Towards negative X";
                    case EAST -> "Towards positive X";
                    default -> "Invalid";
                };

                left.add(String.format("Facing: %s (%s) (%.1f / %.1f)", direction, facing, Mth.wrapDegrees(entity.getYRot()), Mth.wrapDegrees(entity.getXRot())));
            }

            if (ModConfig.Candy.showDebugLightData())
            {
                int nostalgicSkyLight = this.minecraft.level.getBrightness(LightLayer.SKY, blockPos);
                int nostalgicBlockLight = this.minecraft.level.getBrightness(LightLayer.BLOCK, blockPos);
                int nostalgicClientLight = Math.max(nostalgicSkyLight, nostalgicBlockLight);

                left.add(String.format("Nostalgic Light: %d (%d sky, %d block)", nostalgicClientLight, nostalgicSkyLight, nostalgicBlockLight));

                int clientLight = this.minecraft.level.getChunkSource().getLightEngine().getRawBrightness(blockPos, 0);
                int vanillaSkyLight = WorldCommonUtil.getBrightness(this.minecraft.level, LightLayer.SKY, blockPos);
                int vanillaBlockLight = WorldCommonUtil.getBrightness(this.minecraft.level, LightLayer.BLOCK, blockPos);

                left.add(String.format("Vanilla Light: %d (%d sky, %d block)", clientLight, vanillaSkyLight, vanillaBlockLight));
            }

            Entity targetEntity = this.minecraft.crosshairPickEntity;
            boolean isTargeted = ModConfig.Candy.showDebugTargetData();
            boolean isValidHeight = blockPos.getY() >= this.minecraft.level.getMinBuildHeight() && blockPos.getY() < this.minecraft.level.getMaxBuildHeight();

            if (ModConfig.Candy.showDebugBiomeData() && isValidHeight)
                left.add(String.format("Biome: %s", printBiome(this.minecraft.level.getBiome(blockPos))));

            if (isTargeted && this.block.getType() == HitResult.Type.BLOCK)
            {
                BlockPos targetPos = ((BlockHitResult) this.block).getBlockPos();
                BlockState targetState = this.minecraft.level.getBlockState(targetPos);

                right.add("");
                right.add(String.format(ChatFormatting.UNDERLINE + "Targeted Block: %s, %s, %s", targetPos.getX(), targetPos.getY(), targetPos.getZ()));
                right.add(String.valueOf(Registry.BLOCK.getKey(targetState.getBlock())));

                for (Map.Entry<Property<?>, Comparable<?>> entry : targetState.getValues().entrySet())
                    right.add(this.getPropertyValueString(entry));

                targetState.getTags().map(tagKey -> "#" + tagKey.location()).forEach(right::add);
            }

            if (isTargeted && this.liquid.getType() == HitResult.Type.BLOCK)
            {
                BlockPos targetPos = ((BlockHitResult) this.liquid).getBlockPos();
                FluidState targetState = this.minecraft.level.getFluidState(targetPos);

                right.add("");
                right.add(String.format(ChatFormatting.UNDERLINE + "Targeted Fluid: %s, %s, %s", targetPos.getX(), targetPos.getY(), targetPos.getZ()));
                right.add(String.valueOf(Registry.FLUID.getKey(targetState.getType())));

                for (Map.Entry<Property<?>, Comparable<?>> entry : targetState.getValues().entrySet())
                    right.add(this.getPropertyValueString(entry));

                targetState.getTags().map(tagKey -> "#" + tagKey.location()).forEach(right::add);
            }

            if (isTargeted && targetEntity != null)
            {
                right.add("");
                right.add(ChatFormatting.UNDERLINE + "Targeted Entity:");
                right.add(String.valueOf(Registry.ENTITY_TYPE.getKey(targetEntity.getType())));
            }
        }

        for (int i = 0; i < left.size(); i++)
        {
            String text = left.get(i);

            if (Strings.isNullOrEmpty(text))
                continue;

            this.NT$drawLineBackground(poseStack, text, i, true);
            this.NT$drawInformation(poseStack, text, 2.0F, 2.0F + this.font.lineHeight * i, 0xFFFFFF);
        }

        for (int i = 0; i < right.size(); i++)
        {
            String text = right.get(i);

            if (Strings.isNullOrEmpty(text))
                continue;

            int x = width - 2 - this.font.width(text);
            int y = 2 + this.font.lineHeight * i;

            this.NT$drawLineBackground(poseStack, text, i, false);
            this.NT$drawInformation(poseStack, text, (float) x, (float) y, 0xE0E0E0);
        }

        this.drawChart(poseStack, this.minecraft.getFrameTimer(), 0, width / 2, true);

        IntegratedServer server = this.minecraft.getSingleplayerServer();

        if (ModConfig.Candy.displayTpsChart() && server != null)
            this.drawChart(poseStack, server.getFrameTimer(), width - Math.min(width / 2, 240), width / 2, false);

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
        TweakVersion.Generic debug = ModConfig.Candy.getDebugScreen();
        TweakType.DebugChart chart = ModConfig.Candy.getDebugChart();
        boolean isDisabled = chart.equals(TweakType.DebugChart.DISABLED);
        boolean isModern = chart.equals(TweakType.DebugChart.MODERN);
        boolean isOld = chart.equals(TweakType.DebugChart.CLASSIC);

        if (debug.equals(TweakVersion.Generic.MODERN) || !drawForFps)
            return;
        else if (isDisabled)
        {
            callback.cancel();
            return;
        }

        RenderSystem.disableDepthTest();

        long[] log = frameTimer.getLog();
        int maxWidth = (int) Math.max(0, log.length - (this.minecraft.getWindow().getGuiScaledWidth() / 3.5F));
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

        int height = this.minecraft.getWindow().getGuiScaledHeight();
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

        Tesselator.getInstance().end();
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

        int limit = this.minecraft.options.framerateLimit;
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
