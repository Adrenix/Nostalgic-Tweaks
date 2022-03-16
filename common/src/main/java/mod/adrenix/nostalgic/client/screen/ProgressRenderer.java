package mod.adrenix.nostalgic.client.screen;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;

public abstract class ProgressRenderer
{
    public static void drawTitleText(PoseStack poses, Screen screen, Component title)
    {
        Minecraft minecraft = Minecraft.getInstance();
        int scaledHeight = minecraft.getWindow().getGuiScaledHeight();
        Screen.drawCenteredString(poses, minecraft.font, title, screen.width / 2, scaledHeight / 2 - 4 - 16, 16777215);
    }

    public static void drawSubtitleText(PoseStack poses, Screen screen, Component subtitle)
    {
        Minecraft minecraft = Minecraft.getInstance();
        int scaledHeight = minecraft.getWindow().getGuiScaledHeight();
        Screen.drawCenteredString(poses, minecraft.font, subtitle, screen.width / 2, scaledHeight / 2 - 4 + 8, 16777215);
    }

    public static void renderProgressWithChunks(StoringChunkProgressListener progressListener)
    {
        ProgressRenderer.render(progressListener.getProgress());
    }

    public static void renderProgressWithInt(int progress)
    {
        ProgressRenderer.render(progress);
    }

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
        RenderSystem.disableTexture();
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
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
    }
}
