package mod.adrenix.nostalgic.helper.candy;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.ModTracker;

/**
 * This utility class is used only by the client.
 */
public abstract class ModelGapFix
{
    /**
     * Apply model gap fix to the given uvs.
     *
     * @param uvs           The uvs to fix.
     * @param uvShrinkRatio The atlas uv shrink ratio.
     */
    public static void apply(float[] uvs, float uvShrinkRatio)
    {
        if (NostalgicTweaks.isForge() && !ModTracker.OPTIFINE.isInstalled() && !ModTracker.SODIUM.isInstalled())
            uvShrinkRatio += 0.00195F;

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
