package mod.adrenix.nostalgic.util.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class MatrixUtil
{
    /**
     * Get x-coordinate data.
     *
     * @param matrix A {@link Matrix4f} instance.
     * @return The matrix x-coordinate.
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
     * @return The matrix x-coordinate.
     */
    @PublicAPI
    public static float getX(PoseStack poseStack)
    {
        return getX(poseStack.last().pose());
    }

    /**
     * Get x-coordinate data.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @return The matrix x-coordinate.
     */
    @PublicAPI
    public static float getX(GuiGraphics graphics)
    {
        return getX(graphics.pose());
    }

    /**
     * Get y-coordinate data.
     *
     * @param matrix A {@link Matrix4f} instance.
     * @return The matrix y-coordinate.
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
     * @return The matrix y-coordinate.
     */
    @PublicAPI
    public static float getY(PoseStack poseStack)
    {
        return getY(poseStack.last().pose());
    }

    /**
     * Get y-coordinate data.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @return The matrix y-coordinate.
     */
    @PublicAPI
    public static float getY(GuiGraphics graphics)
    {
        return getY(graphics.pose());
    }

    /**
     * Get z-coordinate data.
     *
     * @param matrix A {@link Matrix4f} instance.
     * @return The matrix z-coordinate.
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
     * @return The matrix z-coordinate.
     */
    @PublicAPI
    public static float getZ(PoseStack poseStack)
    {
        return getZ(poseStack.last().pose());
    }

    /**
     * Get z-coordinate data.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @return The matrix z-coordinate.
     */
    @PublicAPI
    public static float getZ(GuiGraphics graphics)
    {
        return getZ(graphics.pose());
    }

    /**
     * Get the average scale from a transformation matrix.
     *
     * @param matrix A {@link Matrix4f} instance.
     * @return The average scaling applied to the transformation matrix.
     */
    @PublicAPI
    public static float getAverageScale(Matrix4f matrix)
    {
        Vector3f scale = matrix.getScale(new Vector3f());

        return (scale.x + scale.y + scale.z) / 3.0F;
    }

    /**
     * Get the average scale from a {@linkplain PoseStack}.
     *
     * @param poseStack The {@link PoseStack} instance.
     * @return The average scaling applied to the {@linkplain PoseStack} transformation matrix.
     */
    @PublicAPI
    public static float getAverageScale(PoseStack poseStack)
    {
        return getAverageScale(poseStack.last().pose());
    }

    /**
     * Get the average scale from {@linkplain GuiGraphics}.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @return The average scaling applied to the {@linkplain GuiGraphics} transformation matrix.
     */
    @PublicAPI
    public static float getAverageScale(GuiGraphics graphics)
    {
        return getAverageScale(graphics.pose());
    }

    /**
     * Get the scale applied to the x-axis.
     *
     * @param matrix The {@link Matrix4f} instance.
     * @return The x-axis scale applied to the transformation matrix.
     */
    @PublicAPI
    public static float getScaleX(Matrix4f matrix)
    {
        return matrix.getScale(new Vector3f()).x;
    }

    /**
     * Get the scale applied to the x-axis.
     *
     * @param poseStack The {@link PoseStack} instance.
     * @return The x-axis scale applied to the {@linkplain PoseStack} transformation matrix.
     */
    @PublicAPI
    public static float getScaleX(PoseStack poseStack)
    {
        return getScaleX(poseStack.last().pose());
    }

    /**
     * Get the scale applied to the x-axis.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @return The x-axis scale applied to the {@linkplain GuiGraphics} transformation matrix.
     */
    @PublicAPI
    public static float getScaleX(GuiGraphics graphics)
    {
        return getScaleX(graphics.pose());
    }

    /**
     * Get the scale applied to the y-axis.
     *
     * @param matrix The {@link Matrix4f} instance.
     * @return The y-axis scale applied to the transformation matrix.
     */
    @PublicAPI
    public static float getScaleY(Matrix4f matrix)
    {
        return matrix.getScale(new Vector3f()).y;
    }

    /**
     * Get the scale applied to the y-axis.
     *
     * @param poseStack The {@link PoseStack} instance.
     * @return The y-axis scale applied to the {@linkplain PoseStack} transformation matrix.
     */
    @PublicAPI
    public static float getScaleY(PoseStack poseStack)
    {
        return getScaleY(poseStack.last().pose());
    }

    /**
     * Get the scale applied to the y-axis.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @return The y-axis scale applied to the {@linkplain GuiGraphics} transformation matrix.
     */
    @PublicAPI
    public static float getScaleY(GuiGraphics graphics)
    {
        return getScaleY(graphics.pose());
    }

    /**
     * Get the scale applied to the z-axis.
     *
     * @param matrix The {@link Matrix4f} instance.
     * @return The z-axis scale applied to the transformation matrix.
     */
    @PublicAPI
    public static float getScaleZ(Matrix4f matrix)
    {
        return matrix.getScale(new Vector3f()).z;
    }

    /**
     * Get the scale applied to the z-axis.
     *
     * @param poseStack The {@link PoseStack} instance.
     * @return The z-axis scale applied to the {@linkplain PoseStack} transformation matrix.
     */
    @PublicAPI
    public static float getScaleZ(PoseStack poseStack)
    {
        return getScaleZ(poseStack.last().pose());
    }

    /**
     * Get the scale applied to the z-axis.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @return The z-axis scale applied to the {@linkplain GuiGraphics} transformation matrix.
     */
    @PublicAPI
    public static float getScaleZ(GuiGraphics graphics)
    {
        return getScaleZ(graphics.pose());
    }
}
