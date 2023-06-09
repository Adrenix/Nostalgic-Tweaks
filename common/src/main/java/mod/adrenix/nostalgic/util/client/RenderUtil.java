package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 */

public abstract class RenderUtil
{
    /**
     * Creates a filled rectangle at the given positions with the given color.
     *
     * @param builder The current buffer builder.
     * @param matrix  The 4D matrix.
     * @param leftX   Left starting position of the rectangle.
     * @param rightX  Right starting position of the rectangle.
     * @param topY    Top starting position of the rectangle.
     * @param bottomY Bottom starting position of the rectangle.
     * @param argb    The ARGB color of the rectangle.
     */
    public static void fill(BufferBuilder builder, Matrix4f matrix, float leftX, float rightX, float topY, float bottomY, int argb)
    {
        float z = 0.0F;
        builder.vertex(matrix, leftX, bottomY, z).color(argb).endVertex();
        builder.vertex(matrix, rightX, bottomY, z).color(argb).endVertex();
        builder.vertex(matrix, rightX, topY, z).color(argb).endVertex();
        builder.vertex(matrix, leftX, topY, z).color(argb).endVertex();
    }

    /**
     * Overload method for {@link RenderUtil#fill(BufferBuilder, Matrix4f, float, float, float, float, int)}. This
     * method does not require a 4D matrix.
     *
     * @param builder A buffer builder instance.
     * @param leftX   Left starting position of the rectangle.
     * @param rightX  Right starting position of the rectangle.
     * @param topY    Top starting position of the rectangle.
     * @param bottomY Bottom starting position of the rectangle.
     * @param argb    The ARGB color of the rectangle.
     */
    public static void fill(BufferBuilder builder, float leftX, float rightX, float topY, float bottomY, int argb)
    {
        float z = 0.0F;

        builder.vertex(leftX, bottomY, z).color(argb).endVertex();
        builder.vertex(rightX, bottomY, z).color(argb).endVertex();
        builder.vertex(rightX, topY, z).color(argb).endVertex();
        builder.vertex(leftX, topY, z).color(argb).endVertex();
    }

    /**
     * Overload method for {@link RenderUtil#fill(BufferBuilder, Matrix4f, float, float, float, float, int)}. This
     * method does not require a buffer builder or a 4D matrix, but instead uses the current pose stack.
     *
     * @param graphics The current GuiGraphics object.
     * @param leftX     Left starting position of the rectangle.
     * @param rightX    Right starting position of the rectangle.
     * @param topY      Top starting position of the rectangle.
     * @param bottomY   Bottom starting position of the rectangle.
     * @param argb      The ARGB color of the rectangle.
     */
    public static void fill(GuiGraphics graphics, float leftX, float rightX, float topY, float bottomY, int argb)
    {
        float z = 0.0F;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        Matrix4f matrix = graphics.pose().last().pose();

        RenderSystem.enableBlend();
        RenderSystem.depthFunc(515);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(matrix, leftX, bottomY, z).color(argb).endVertex();
        builder.vertex(matrix, rightX, bottomY, z).color(argb).endVertex();
        builder.vertex(matrix, rightX, topY, z).color(argb).endVertex();
        builder.vertex(matrix, leftX, topY, z).color(argb).endVertex();
        tesselator.end();

        RenderSystem.disableBlend();
    }

    /**
     * Render a texture from a texture sheet (256x256).
     *
     * @param resource  A resource location that points to the texture sheet.
     * @param graphics  The current GuiGraphics object.
     * @param x         The x-position on the screen to place the texture.
     * @param y         The y-position on the screen to place the texture.
     * @param uOffset   The x-position of the texture on the texture sheet.
     * @param vOffset   The y-position of the texture on the texture sheet.
     * @param uWidth    The width of the texture on the texture sheet.
     * @param vHeight   The height of the texture on the texture sheet.
     */
    public static void blit256(ResourceLocation resource, GuiGraphics graphics, float x, float y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        RenderSystem.setShaderTexture(0, resource);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        Matrix4f matrix = graphics.pose().last().pose();

        float x2 = x + uWidth;
        float y2 = y + vHeight;
        float minU = uOffset / 256.0F;
        float maxU = (uOffset + uWidth) / 256.0F;
        float minV = vOffset / 256.0F;
        float maxV = (vOffset + vHeight) / 256.0F;

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(matrix, x, y2, 0.0F).uv(minU, maxV).endVertex();
        builder.vertex(matrix, x2, y2, 0.0F).uv(maxU, maxV).endVertex();
        builder.vertex(matrix, x2, y, 0.0F).uv(maxU, minV).endVertex();
        builder.vertex(matrix, x, y, 0.0F).uv(minU, minV).endVertex();
        BufferUploader.drawWithShader(builder.end());
    }
}
