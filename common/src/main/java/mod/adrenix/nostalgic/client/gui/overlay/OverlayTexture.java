package mod.adrenix.nostalgic.client.gui.overlay;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import net.minecraft.client.gui.GuiGraphics;

public abstract class OverlayTexture
{
    /**
     * Render an overlay border with the given dimensions.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @param x        The x-coordinate to begin drawing at.
     * @param y        The y-coordinate to begin drawing at.
     * @param width    The window width.
     * @param height   The window height.
     */
    public static void render(GuiGraphics graphics, int x, int y, int width, int height)
    {
        int xOffset = 8;
        int yOffset = 15;

        PoseStack poseStack = graphics.pose();
        BufferBuilder builder = RenderUtil.getAndBeginTexture(TextureLocation.OVERLAY);

        poseStack.pushPose();
        poseStack.translate(x, y, 0.0D);

        // Corners
        RenderUtil.blit256(builder, graphics, 0, 0, 0, 0, 8, 15);
        RenderUtil.blit256(builder, graphics, 0, height - 8, 0, 248, 8, 8);
        RenderUtil.blit256(builder, graphics, width - 8, 0, 248, 0, 8, 15);
        RenderUtil.blit256(builder, graphics, width - 8, height - 8, 248, 248, 8, 8);

        // Left & Right
        if (width < 256)
        {
            RenderUtil.blit256(builder, graphics, xOffset, 0, 8, 0, width - 16, 15);
            RenderUtil.blit256(builder, graphics, xOffset, height - 8, 8, 248, width - 16, 8);
        }
        else
        {
            int uWidth = 240;
            int repeat = width / uWidth;
            int remainder = width % uWidth;

            for (int i = 0; i < repeat; i++)
            {
                RenderUtil.blit256(builder, graphics, xOffset, 0, 8, 0, uWidth, 15);
                RenderUtil.blit256(builder, graphics, xOffset, height - 8, 8, 248, uWidth, 8);
                xOffset += uWidth;
            }

            RenderUtil.blit256(builder, graphics, xOffset, 0, 256 - remainder, 0, remainder - 16, 15);
            RenderUtil.blit256(builder, graphics, xOffset, height - 8, 256 - remainder, 248, remainder - 16, 8);
        }

        // Top & Bottom
        if (height < 256)
        {
            RenderUtil.blit256(builder, graphics, 0, yOffset, 0, 15, 8, height - 23);
            RenderUtil.blit256(builder, graphics, width - 8, yOffset, 248, 15, 8, height - 23);
        }
        else
        {
            int vHeight = 233;
            int repeat = height / vHeight;
            int remainder = height % vHeight;

            for (int i = 0; i < repeat; i++)
            {
                RenderUtil.blit256(builder, graphics, 0, yOffset, 0, 15, 8, vHeight);
                RenderUtil.blit256(builder, graphics, width - 8, yOffset, 248, 15, 8, vHeight);
                yOffset += vHeight;
            }

            RenderUtil.blit256(builder, graphics, 0, yOffset, 0, 256 - remainder, 8, remainder - 23);
            RenderUtil.blit256(builder, graphics, width - 8, yOffset, 248, 256 - remainder, 8, remainder - 23);
        }

        RenderUtil.endTexture(builder);
        poseStack.popPose();
    }
}
