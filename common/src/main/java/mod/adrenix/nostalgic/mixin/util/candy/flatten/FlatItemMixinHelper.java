package mod.adrenix.nostalgic.mixin.util.candy.flatten;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * This utility class is used only by the client.
 */
public abstract class FlatItemMixinHelper
{
    /* Fields */

    private static final FlagHolder RENDERING_FLAT = FlagHolder.off();

    /* Methods */

    /**
     * Enable flat item rendering.
     */
    public static void enableFlatRendering()
    {
        RENDERING_FLAT.enable();
    }

    /**
     * Disable flat item rendering.
     */
    public static void disableFlatRendering()
    {
        RENDERING_FLAT.disable();
    }

    /**
     * @return Whether the item rendering is currently drawing in 2D.
     */
    public static boolean isRendering2D()
    {
        return RENDERING_FLAT.get();
    }

    /**
     * Flattens the z-axis so that rendered items appear as 2D. The pose stack will not be pushed or popped.
     *
     * @param poseStack The {@link PoseStack} instance to scale.
     */
    public static void flattenScaling(PoseStack poseStack)
    {
        poseStack.scale(1.0F, 1.0F, 0.001F);
    }

    /**
     * Get only the front facing quad, or all quads if not rendering in 2D.
     *
     * @param quads The {@link List} of {@link BakedQuad} being rendered.
     * @return A {@link List} of one {@link BakedQuad} or the given quads.
     */
    public static List<BakedQuad> getFrontQuad(List<BakedQuad> quads)
    {
        if (!RENDERING_FLAT.get())
            return quads;

        List<BakedQuad> southQuads = new ArrayList<>();

        for (BakedQuad baked : quads)
        {
            if (baked.getDirection() == Direction.SOUTH)
                southQuads.add(baked);
        }

        return southQuads;
    }

    /**
     * Sets the normals in the pose stack to unit values based on the quad direction.
     *
     * @param pose The {@link PoseStack} instance to normalize.
     * @param quad The {@link BakedQuad} to check.
     */
    public static void setUnitNormals(PoseStack.Pose pose, BakedQuad quad)
    {
        float sign = MathUtil.sign(Minecraft.getInstance().gameRenderer.getMainCamera().getXRot());

        pose.normal().m20(1.0F);
        pose.normal().m21(1.0F);
        pose.normal().m22(sign);

        if (quad.getDirection() == Direction.NORTH)
        {
            pose.normal().m20(-1.0F);
            pose.normal().m21(-1.0F);
            pose.normal().m22(-1.0F * sign);
        }
    }
}
