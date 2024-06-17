package mod.adrenix.nostalgic.mixin.util.animation;

import net.minecraft.client.model.geom.ModelPart;

/**
 * This utility class is used only by the client.
 */
public abstract class MobArmMixinHelper
{
    /**
     * Apply static arms to the given model parts.
     *
     * @param rightArm The right {@link ModelPart} arm.
     * @param leftArm  The left {@link ModelPart} arm.
     */
    public static void applyStaticArms(ModelPart rightArm, ModelPart leftArm)
    {
        leftArm.xRot = -1.57F;
        leftArm.yRot = 0.0F;
        leftArm.zRot = 0.0F;

        rightArm.xRot = -1.57F;
        rightArm.yRot = 0.0F;
        rightArm.zRot = 0.0F;
    }
}
