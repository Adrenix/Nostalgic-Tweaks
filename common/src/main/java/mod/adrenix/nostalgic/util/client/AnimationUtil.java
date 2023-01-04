package mod.adrenix.nostalgic.util.client;

import net.minecraft.client.model.geom.ModelPart;

/**
 * Client animation utility.
 */

public abstract class AnimationUtil
{
    /* Swing Animations */

    /**
     * Changes the swinging animation based on the current swing type. This is used by the old classic swing tweak since
     * different animations played depending on whether the mouse was left-clicked or right-clicked.
     */
    public static SwingType swingType = SwingType.LEFT_CLICK;

    /* Mob Animations */

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
