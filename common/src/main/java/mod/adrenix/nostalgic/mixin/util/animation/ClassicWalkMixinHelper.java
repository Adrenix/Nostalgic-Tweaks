package mod.adrenix.nostalgic.mixin.util.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.mixin.access.LivingEntityAccess;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * This utility class is used only by the client.
 */
public abstract class ClassicWalkMixinHelper
{
    /**
     * Applies the classic walk bobbing animation.
     *
     * @param entity      The {@link LivingEntity} instance.
     * @param poseStack   The {@link PoseStack} instance.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    public static void applyBobbing(LivingEntity entity, PoseStack poseStack, float partialTick)
    {
        LivingEntityAccess livingAccess = (LivingEntityAccess) entity;

        float animStep = Mth.lerp(partialTick, livingAccess.nt$getOldAnimStep(), livingAccess.nt$getAnimStep());
        float run = Mth.lerp(partialTick, livingAccess.nt$getOldRun(), livingAccess.nt$getRun());
        float bob = -Math.abs(Mth.cos(animStep * 0.6662F)) * 5.0F * run - 23.0F;

        poseStack.translate(0.0F, bob * 0.0625F + 1.435F, 0.0F);
    }
}
