package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.GameRenderer;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 */

public abstract class RenderUtil
{
    /**
     * Creates a filled rectangle at the given positions with the given color.
     * @param builder The current buffer builder.
     * @param matrix The 4D matrix.
     * @param leftX Left starting position of the rectangle.
     * @param rightX Right starting position of the rectangle.
     * @param topY Top starting position of the rectangle.
     * @param bottomY Bottom starting position of the rectangle.
     * @param argb The ARGB color of the rectangle.
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
     * Overload method for {@link RenderUtil#fill(BufferBuilder, Matrix4f, float, float, float, float, int)}.
     * This method does not require a 4D matrix.
     *
     * @param builder A buffer builder instance.
     * @param leftX Left starting position of the rectangle.
     * @param rightX Right starting position of the rectangle.
     * @param topY Top starting position of the rectangle.
     * @param bottomY Bottom starting position of the rectangle.
     * @param argb The ARGB color of the rectangle.
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
     * Overload method for {@link RenderUtil#fill(BufferBuilder, Matrix4f, float, float, float, float, int)}.
     * This method does not require a buffer builder or a 4D matrix, but instead uses the current pose stack.
     *
     * @param poseStack The current pose stack.
     * @param leftX Left starting position of the rectangle.
     * @param rightX Right starting position of the rectangle.
     * @param topY Top starting position of the rectangle.
     * @param bottomY Bottom starting position of the rectangle.
     * @param argb The ARGB color of the rectangle.
     */
    public static void fill(PoseStack poseStack, float leftX, float rightX, float topY, float bottomY, int argb)
    {
        float z = 0.0F;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(matrix, leftX, bottomY, z).color(argb).endVertex();
        builder.vertex(matrix, rightX, bottomY, z).color(argb).endVertex();
        builder.vertex(matrix, rightX, topY, z).color(argb).endVertex();
        builder.vertex(matrix, leftX, topY, z).color(argb).endVertex();
        tesselator.end();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
