package mod.adrenix.nostalgic.util.client;

import net.minecraft.client.model.geom.ModelPart;

/**
 * Client animation utility.
 */

public abstract class AnimationUtil
{
    /**
     * Makes arm models parallel to the ground like in the old days.
     * @param rightArm The right arm of the model.
     * @param leftArm The left arm of the model.
     */
    public static void setStaticArms(ModelPart rightArm, ModelPart leftArm)
    {
        rightArm.xRot = -1.57F;
        rightArm.yRot = 0.0F;
        rightArm.zRot = 0.0F;

        leftArm.xRot = -1.57F;
        leftArm.yRot = 0.0F;
        leftArm.zRot = 0.0F;
    }
}
