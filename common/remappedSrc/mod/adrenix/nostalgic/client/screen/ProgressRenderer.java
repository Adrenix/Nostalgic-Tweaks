package mod.adrenix.nostalgic.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public abstract class ProgressRenderer
{
    public static void drawTitleText(MatrixStack poses, Screen screen, Text title)
    {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        int scaledHeight = minecraft.getWindow().getScaledHeight();
        Screen.drawCenteredText(poses, minecraft.textRenderer, title, screen.width / 2, scaledHeight / 2 - 4 - 16, 16777215);
    }

    public static void drawSubtitleText(MatrixStack poses, Screen screen, Text subtitle)
    {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        int scaledHeight = minecraft.getWindow().getScaledHeight();
        Screen.drawCenteredText(poses, minecraft.textRenderer, subtitle, screen.width / 2, scaledHeight / 2 - 4 + 8, 16777215);
    }

    public static void renderProgressWithChunks(WorldGenerationProgressTracker progressListener)
    {
        ProgressRenderer.render(progressListener.getProgressPercentage());
    }

    public static void renderProgressWithInt(int progress)
    {
        ProgressRenderer.render(progress);
    }

    private static void render(int progress)
    {
        Window window = MinecraftClient.getInstance().getWindow();
        Tessellator tesselator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuffer();

        int xOffset = 100;
        int yOffset = 2;
        int xStart = window.getScaledWidth() / 2 - xOffset / 2;
        int yStart = window.getScaledHeight() / 2 + 16;

        if (progress >= xOffset)
            progress = xOffset;

        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        // Blank Bar
        bufferbuilder.vertex(xStart, yStart, 0.0D).color(128, 128, 128, 255).next();
        bufferbuilder.vertex(xStart, yStart + yOffset, 0.0D).color(128, 128, 128, 255).next();
        bufferbuilder.vertex(xStart + xOffset, yStart + yOffset, 0.0D).color(128, 128, 128, 255).next();
        bufferbuilder.vertex(xStart + xOffset, yStart, 0.0D).color(128, 128, 128, 255).next();

        // Progress Bar
        bufferbuilder.vertex(xStart, yStart, 0.0D).color(128, 255, 128, 255).next();
        bufferbuilder.vertex(xStart, yStart + yOffset, 0.0D).color(128, 255, 128, 255).next();
        bufferbuilder.vertex(xStart + progress, yStart + yOffset, 0.0D).color(128, 255, 128, 255).next();
        bufferbuilder.vertex(xStart + progress, yStart, 0.0D).color(128, 255, 128, 255).next();
        tesselator.draw();

        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
    }
}
