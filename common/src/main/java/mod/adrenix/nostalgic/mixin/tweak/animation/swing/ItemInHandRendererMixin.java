package mod.adrenix.nostalgic.mixin.tweak.animation.swing;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.mixin.util.animation.PlayerArmMixinHelper;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin
{
    /**
     * Simulates the old arm swinging animation.
     */
    @Inject(
        method = "applyItemArmAttackTransform",
        at = @At("HEAD")
    )
    private void nt_swing$onApplyItemArmAttackTransform(PoseStack poseStack, HumanoidArm hand, float swingProgress, CallbackInfo callback)
    {
        if (AnimationTweak.OLD_SWING.get())
            PlayerArmMixinHelper.oldSwing(poseStack, swingProgress);
    }

    /**
     * Simulates the old classic arm swinging animation.
     */
    @WrapWithCondition(
        method = "renderArmWithItem",
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmAttackTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V"
        )
    )
    private boolean nt_swing$onRenderArmWithItem(ItemInHandRenderer handRenderer, PoseStack poseStack, HumanoidArm arm, float swingProgress, AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float copyOfSwingProgress, ItemStack itemStack, float equippedProgress)
    {
        return !PlayerArmMixinHelper.oldClassicSwing(poseStack, arm, swingProgress, equippedProgress);
    }
}
