package mod.adrenix.nostalgic.util.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import org.joml.Matrix4f;

public abstract class MatrixUtil
{
    /**
     * Get x-coordinate data.
     *
     * @param matrix A {@link Matrix4f} instance.
     * @return A x-coordinate.
     */
    @PublicAPI
    public static float getX(Matrix4f matrix)
    {
        return matrix.m30();
    }

    /**
     * Get x-coordinate data.
     *
     * @param poseStack A {@link PoseStack} instance.
     * @return A x-coordinate.
     */
    @PublicAPI
    public static float getX(PoseStack poseStack)
    {
        return getX(poseStack.last().pose());
    }

    /**
     * Get y-coordinate data.
     *
     * @param matrix A {@link Matrix4f} instance.
     * @return A y-coordinate.
     */
    @PublicAPI
    public static float getY(Matrix4f matrix)
    {
        return matrix.m31();
    }

    /**
     * Get y-coordinate data.
     *
     * @param poseStack A {@link PoseStack} instance.
     * @return A y-coordinate.
     */
    @PublicAPI
    public static float getY(PoseStack poseStack)
    {
        return getY(poseStack.last().pose());
    }

    /**
     * Get z-coordinate data.
     *
     * @param matrix A {@link Matrix4f} instance.
     * @return A z-coordinate.
     */
    @PublicAPI
    public static float getZ(Matrix4f matrix)
    {
        return matrix.m32();
    }

    /**
     * Get z-coordinate data.
     *
     * @param poseStack A {@link PoseStack} instance.
     * @return A z-coordinate.
     */
    @PublicAPI
    public static float getZ(PoseStack poseStack)
    {
        return getZ(poseStack.last().pose());
    }
}
