package mod.adrenix.nostalgic.client.gui.screen.vanilla.progress;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.BufferBuilder;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;

abstract class ProgressRenderer
{
    /**
     * Draws the header text to the top of the progress screen.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @param header   The {@link Component} header to draw.
     * @param width    The width of the current {@link Screen}.
     */
    public static void drawHeaderText(GuiGraphics graphics, Component header, int width)
    {
        graphics.drawCenteredString(GuiUtil.font(), header, width / 2, GuiUtil.getGuiHeight() / 2 - 4 - 16, 16777215);
    }

    /**
     * Draws the stage text below the title of the progress screen.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @param stage    The {@link Component} stage to draw.
     * @param width    The width of the current {@link Screen}.
     */
    public static void drawStageText(GuiGraphics graphics, Component stage, int width)
    {
        graphics.drawCenteredString(GuiUtil.font(), stage, width / 2, GuiUtil.getGuiHeight() / 2 - 4 + 8, 16777215);
    }

    /**
     * Render a progress bar with a chunk progress listener.
     *
     * @param progressListener A {@link StoringChunkProgressListener} instance.
     */
    public static void renderProgressWithChunks(StoringChunkProgressListener progressListener)
    {
        ProgressRenderer.render(progressListener.getProgress());
    }

    /**
     * Render a progress bar with an integer.
     *
     * @param progress An integer between 0-100.
     */
    public static void renderProgressWithInt(int progress)
    {
        ProgressRenderer.render(progress);
    }

    /**
     * Renders a progress bar.
     *
     * @param progress An integer between 0-100.
     */
    private static void render(int progress)
    {
        Window window = GuiUtil.getWindow();

        int xOffset = 100;
        int yOffset = 2;
        int xStart = window.getGuiScaledWidth() / 2 - xOffset / 2;
        int yStart = window.getGuiScaledHeight() / 2 + 16;

        if (progress >= xOffset)
            progress = xOffset;

        BufferBuilder builder = RenderUtil.getAndBeginFill();
        Color background = new Color(128, 128, 128);
        Color foreground = new Color(128, 255, 128);

        RenderUtil.fill(builder, xStart, yStart, xStart + xOffset, yStart + yOffset, background.get());
        RenderUtil.fill(builder, xStart, yStart, xStart + progress, yStart + yOffset, foreground.get());
        RenderUtil.endFill(builder);
    }
}
