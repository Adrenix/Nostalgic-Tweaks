package mod.adrenix.nostalgic.helper.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.mixin.duck.GhastCounter;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Ghast;

/**
 * This utility class is used only by the client.
 */
public abstract class GhastChargeHelper
{
    /**
     * Apply a Ghast squishing animation.
     *
     * @param ghast       The {@link Ghast} instance.
     * @param poseStack   The {@link PoseStack} instance.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    public static void applySquish(Ghast ghast, PoseStack poseStack, float partialTick)
    {
        float squish = ((((GhastCounter) ghast).nt$getAttackCounter() + (ghast.isAlive() ? partialTick : 0)) + 10) / 20.0F;
        squish = Mth.clamp(squish, 0.0F, 1.0F);
        squish = 1.0F / (squish * squish * squish * squish * squish * 2.0F + 1.0F);

        float vertical = (8.0F + squish) / 2.0F;
        float horizontal = (8.0F + 1.0F / squish) / 2.0F;

        poseStack.scale(horizontal, vertical, horizontal);
    }
}
