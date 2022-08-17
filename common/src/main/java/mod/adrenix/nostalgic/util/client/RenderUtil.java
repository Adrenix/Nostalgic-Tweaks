package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.math.Matrix4f;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 */

public abstract class RenderUtil
{
    public static void fill(BufferBuilder builder, Matrix4f matrix, float leftX, float rightX, float topY, float bottomY, int rgba)
    {
        float z = 0.0F;
        builder.vertex(matrix, leftX, bottomY, z).color(rgba).endVertex();
        builder.vertex(matrix, rightX, bottomY, z).color(rgba).endVertex();
        builder.vertex(matrix, rightX, topY, z).color(rgba).endVertex();
        builder.vertex(matrix, leftX, topY, z).color(rgba).endVertex();
    }
}
