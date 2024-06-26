package mod.adrenix.nostalgic.fabric.mixin.util;

/**
 * This utility class is used only by the client.
 */
public abstract class FabricModelGapFix
{
    /**
     * Apply model gap fix to the given uvs.
     *
     * @param uvs           The uvs to fix.
     * @param uvShrinkRatio The atlas uv shrink ratio.
     */
    public static void apply(float[] uvs, float uvShrinkRatio)
    {
        float centerU = (uvs[0] + uvs[2]) / 2.0F;
        float centerV = (uvs[1] + uvs[3]) / 2.0F;

        uvs[0] = fix(uvs[0], centerU, uvShrinkRatio);
        uvs[1] = fix(uvs[1], centerV, uvShrinkRatio);
        uvs[2] = fix(uvs[2], centerU, uvShrinkRatio);
        uvs[3] = fix(uvs[3], centerV, uvShrinkRatio);
    }

    /**
     * Fixes the face uv by applying the uv shrink ratio.
     *
     * @param uv            The uv to fix.
     * @param center        The center of u/v.
     * @param uvShrinkRatio The uv shrink ratio.
     * @return A fixed face uv.
     */
    private static float fix(float uv, float center, float uvShrinkRatio)
    {
        return (uv - uvShrinkRatio * center) / (1 - uvShrinkRatio);
    }
}
