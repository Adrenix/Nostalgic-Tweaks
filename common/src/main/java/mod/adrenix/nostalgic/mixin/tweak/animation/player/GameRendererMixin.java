package mod.adrenix.nostalgic.mixin.tweak.animation.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.adrenix.nostalgic.mixin.duck.CameraPitching;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
    /* Shadows */

    @Shadow @Final Minecraft minecraft;

    /* Injections */

    /**
     * Brings back old vertical bobbing.
     */
    @Inject(
        method = "bobView",
        at = @At(
            ordinal = 1,
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V"
        )
    )
    private void nt_player_animation$onBobView(PoseStack poseStack, float partialTick, CallbackInfo callback)
    {
        if (AnimationTweak.OLD_VERTICAL_BOBBING.get() && this.minecraft.getCameraEntity() instanceof Player player)
        {
            CameraPitching cameraPitching = (CameraPitching) player;
            float pitch = Mth.lerp(partialTick, cameraPitching.nt$getPrevCameraPitch(), cameraPitching.nt$getCameraPitch());

            poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        }
    }
}
