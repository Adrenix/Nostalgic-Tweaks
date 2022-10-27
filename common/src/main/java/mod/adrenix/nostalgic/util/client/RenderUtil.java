package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.math.Matrix4f;

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
     * @param rgba The RGBA color of the rectangle.
     */
    public static void fill(BufferBuilder builder, Matrix4f matrix, float leftX, float rightX, float topY, float bottomY, int rgba)
    {
        float z = 0.0F;
        builder.vertex(matrix, leftX, bottomY, z).color(rgba).endVertex();
        builder.vertex(matrix, rightX, bottomY, z).color(rgba).endVertex();
        builder.vertex(matrix, rightX, topY, z).color(rgba).endVertex();
        builder.vertex(matrix, leftX, topY, z).color(rgba).endVertex();
    }

    /**
     * Overload method for {@link RenderUtil#fill(BufferBuilder, Matrix4f, float, float, float, float, int)}.
     * This method does not require a 4D matrix.
     */
    public static void fill(BufferBuilder builder, float leftX, float rightX, float topY, float bottomY, int rgba)
    {
        float z = 0.0F;
        builder.vertex(leftX, bottomY, z).color(rgba).endVertex();
        builder.vertex(rightX, bottomY, z).color(rgba).endVertex();
        builder.vertex(rightX, topY, z).color(rgba).endVertex();
        builder.vertex(leftX, topY, z).color(rgba).endVertex();
    }
}
