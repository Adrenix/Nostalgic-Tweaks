package mod.adrenix.nostalgic.mixin.util.gameplay.combat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

/**
 * This utility class is used only by the client.
 */
public abstract class SwordBlockRenderer
{
    /**
     * Apply the sword blocking animation in first-person view.
     *
     * @param poseStack The {@link PoseStack} instance.
     * @param player    The {@link Player} instance.
     * @param hand      The {@link InteractionHand} value.
     */
    public static void applyFirstPerson(PoseStack poseStack, Player player, InteractionHand hand)
    {
        HumanoidArm mainArm = player.getMainArm();
        HumanoidArm usingArm = InteractionHand.MAIN_HAND == hand ? mainArm : mainArm.getOpposite();
        int mirror = HumanoidArm.RIGHT == usingArm ? 1 : -1;

        poseStack.translate(mirror * -0.14F, 0.15F, 0.1F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-102.25F));
        poseStack.mulPose(Axis.YP.rotationDegrees(mirror * 13.37F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(mirror * 78.05F));
    }

    /**
     * Apply the sword blocking animation in third-person view.
     *
     * @param player   The {@link Player} instance.
     * @param leftArm  The left {@link ModelPart} arm instance.
     * @param rightArm The right {@link ModelPart} arm instance.
     */
    public static void applyThirdPerson(Player player, ModelPart leftArm, ModelPart rightArm)
    {
        if (player.getUsedItemHand() == InteractionHand.OFF_HAND)
            leftArm.xRot -= ((float) Math.PI * 2.0F) / 10.0F;
        else
            rightArm.xRot -= ((float) Math.PI * 2.0F) / 10.0F;
    }

    /**
     * Render sword blocking in a player's model hand.
     *
     * @param poseStack      The {@link PoseStack} instance.
     * @param itemStack      The {@link ItemStack} in the hand.
     * @param displayContext The {@link ItemDisplayContext} value.
     * @param player         The {@link Player} instance.
     * @param arm            The {@link HumanoidArm} value.
     * @param armedModel     The {@link ArmedModel} instance.
     * @param buffer         The {@link MultiBufferSource} instance.
     * @param packedLight    The amount of combined light applied to the hand layer.
     */
    public static void renderModelBlockingInHand(PoseStack poseStack, ItemStack itemStack, ItemDisplayContext displayContext, Player player, HumanoidArm arm, ArmedModel armedModel, MultiBufferSource buffer, int packedLight)
    {
        boolean leftArm = arm == HumanoidArm.LEFT;

        poseStack.pushPose();
        armedModel.translateToHand(arm, poseStack);
        poseStack.translate((leftArm ? 1.0F : -1.0F) / 16.0F, 0.4375F, 0.0625F);
        poseStack.translate(leftArm ? -0.035F : 0.05F, leftArm ? 0.045F : 0.0F, leftArm ? -0.135F : -0.1F);
        poseStack.mulPose(Axis.YP.rotationDegrees((leftArm ? -1.0F : 1.0F) * -50.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(-10.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees((leftArm ? -1.0F : 1.0F) * -60.0F));
        poseStack.translate(0.0F, 0.1875F, 0.0F);
        poseStack.scale(0.625F, 0.625F, 0.625F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.XN.rotationDegrees(-100.0F));
        poseStack.mulPose(Axis.YN.rotationDegrees(leftArm ? 35.0F : 45.0F));
        poseStack.translate(0.0F, -0.3F, 0.0F);
        poseStack.scale(1.5F, 1.5F, 1.5F);
        poseStack.mulPose(Axis.YN.rotationDegrees(50.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(335.0F));
        poseStack.translate(-0.9375F, -0.0625F, 0.0F);
        poseStack.translate(0.5F, 0.5F, 0.25F);
        poseStack.mulPose(Axis.YN.rotationDegrees(180.0F));
        poseStack.translate(0.0F, 0.0F, 0.28125F);

        BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(itemStack, player.level(), player, 0);
        ItemTransform transform = itemModel.getTransforms().getTransform(displayContext);

        if (transform != ItemTransform.NO_TRANSFORM)
        {
            float angle = (float) (Math.PI / 180.0D);
            float x = transform.rotation.x() * angle;
            float y = transform.rotation.y() * angle;
            float z = transform.rotation.z() * angle;

            if (leftArm)
            {
                y *= -1.0F;
                z *= -1.0F;
            }

            Quaternionf quaternion = new Quaternionf().rotationXYZ(x, y, z);
            quaternion.conjugate();

            poseStack.scale(1.0F / transform.scale.x(), 1.0F / transform.scale.y(), 1.0F / transform.scale.z());
            poseStack.mulPose(quaternion);
            poseStack.translate((leftArm ? -1.0F : 1.0F) * -transform.translation.x(), -transform.translation.y(), -transform.translation.z());
        }

        Minecraft.getInstance()
            .getEntityRenderDispatcher()
            .getItemInHandRenderer()
            .renderItem(player, itemStack, displayContext, leftArm, poseStack, buffer, packedLight);

        poseStack.popPose();
    }
}
