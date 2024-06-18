package mod.adrenix.nostalgic.mixin.tweak.animation.arm_sway;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin
{
    /**
     * Prevents the rotation of the hand in first-person.
     */
    @WrapWithCondition(
        method = "renderHandsWithItems",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V"
        )
    )
    private boolean nt_arm_sway$shouldRotateArm(PoseStack poseStack, Quaternionf quaternion)
    {
        return !AnimationTweak.PREVENT_ARM_SWAY.get();
    }

    /**
     * Modifies the intensity of the arm rotation amount.
     */
    @ModifyArg(
        method = "renderHandsWithItems",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;"
        )
    )
    private float nt_arm_sway$modifyRotationIntensity(float degrees)
    {
        if (!ModTweak.ENABLED.get())
            return degrees;

        float mirror = AnimationTweak.ARM_SWAY_MIRROR.get() ? -1.0F : 1.0F;
        float intensity = AnimationTweak.ARM_SWAY_INTENSITY.get() * mirror / 100.0F;

        return degrees * intensity;
    }
}
