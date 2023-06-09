package mod.adrenix.nostalgic.client.screen;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;

/**
 * This helper classed is used to render the old style green progress bar.
 * There are two types of rendering progress, one is with chunk progress listener, and the other is with integers.
 */

public abstract class ProgressRenderer
{
    /**
     * Draws the title text to the top of the progress screen.
     * @param graphics The current GuiGraphics object.
     * @param screen A screen instance.
     * @param title The title to draw.
     */
    public static void drawTitleText(GuiGraphics graphics, Screen screen, Component title)
    {
        Minecraft minecraft = Minecraft.getInstance();
        int scaledHeight = minecraft.getWindow().getGuiScaledHeight();

        graphics.drawCenteredString(minecraft.font, title, screen.width / 2, scaledHeight / 2 - 4 - 16, 16777215);
    }

    /**
     * Draws the subtitle text below the title of the progress screen.
     * @param graphics The current GuiGraphics object.
     * @param screen A screen instance.
     * @param subtitle The subtitle to draw.
     */
    public static void drawSubtitleText(GuiGraphics graphics, Screen screen, Component subtitle)
    {
        Minecraft minecraft = Minecraft.getInstance();
        int scaledHeight = minecraft.getWindow().getGuiScaledHeight();

        graphics.drawCenteredString(minecraft.font, subtitle, screen.width / 2, scaledHeight / 2 - 4 + 8, 16777215);
    }

    /**
     * Render a progress bar with a chunk progress listener.
     * @param progressListener A storing chunk progress listener instance.
     */
    public static void renderProgressWithChunks(StoringChunkProgressListener progressListener)
    {
        ProgressRenderer.render(progressListener.getProgress());
    }

    /**
     * Render a progress bar with an integer.
     * @param progress An integer between 0-100.
     */
    public static void renderProgressWithInt(int progress) { ProgressRenderer.render(progress); }

    /**
     * Renders a progress bar with an integer.
     * The chunk progress listener will provide this via its get progress method.
     * @param progress An integer between 0-100.
     */
    private static void render(int progress)
    {
        Window window = Minecraft.getInstance().getWindow();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();

        int xOffset = 100;
        int yOffset = 2;
        int xStart = window.getGuiScaledWidth() / 2 - xOffset / 2;
        int yStart = window.getGuiScaledHeight() / 2 + 16;

        if (progress >= xOffset)
            progress = xOffset;

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // Blank Bar
        bufferbuilder.vertex(xStart, yStart, 0.0D).color(128, 128, 128, 255).endVertex();
        bufferbuilder.vertex(xStart, yStart + yOffset, 0.0D).color(128, 128, 128, 255).endVertex();
        bufferbuilder.vertex(xStart + xOffset, yStart + yOffset, 0.0D).color(128, 128, 128, 255).endVertex();
        bufferbuilder.vertex(xStart + xOffset, yStart, 0.0D).color(128, 128, 128, 255).endVertex();

        // Progress Bar
        bufferbuilder.vertex(xStart, yStart, 0.0D).color(128, 255, 128, 255).endVertex();
        bufferbuilder.vertex(xStart, yStart + yOffset, 0.0D).color(128, 255, 128, 255).endVertex();
        bufferbuilder.vertex(xStart + progress, yStart + yOffset, 0.0D).color(128, 255, 128, 255).endVertex();
        bufferbuilder.vertex(xStart + progress, yStart, 0.0D).color(128, 255, 128, 255).endVertex();
        tesselator.end();

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
    }
}
