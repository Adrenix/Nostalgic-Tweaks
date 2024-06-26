package mod.adrenix.nostalgic.mixin.tweak.animation.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.adrenix.nostalgic.mixin.access.PlayerAccess;
import mod.adrenix.nostalgic.mixin.duck.CameraPitching;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
    /* Shadows */

    @Shadow @Final Minecraft minecraft;

    /* Unique */

    @Unique private final FlagHolder nt$directionFlag = FlagHolder.off();

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

    /**
     * Randomizes the player hurt direction if the damage source has no direction.
     */
    @Inject(
        method = "bobHurt",
        at = @At("HEAD")
    )
    private void nt_player_animation$onBobHurt(PoseStack poseStack, float partialTick, CallbackInfo callback)
    {
        if (AnimationTweak.OLD_RANDOM_DAMAGE.get() && this.minecraft.getCameraEntity() instanceof Player player)
        {
            if (player.getHurtDir() != 0.0F)
                return;

            if (player.hurtTime - partialTick > 0.0F && this.nt$directionFlag.ifDisabledThenEnable())
                player.animateHurt((int) (Math.random() * 2.0D) * 180);
            else if (player.hurtTime - partialTick <= 0.0F)
                this.nt$directionFlag.disable();
        }
    }

    /**
     * Resets the player's hurt direction after it has been used. This is needed so that the random hurt direction does
     * not pollute directional damage input.
     */
    @Inject(
        method = "bobHurt",
        at = @At(
            ordinal = 0,
            value = "RETURN"
        )
    )
    private void nt_player_animation$onGetHurtDir(PoseStack poseStack, float partialTick, CallbackInfo callback)
    {
        if (AnimationTweak.OLD_RANDOM_DAMAGE.get() && this.minecraft.getCameraEntity() instanceof Player player)
            ((PlayerAccess) player).nt$setHurtDir(0.0F);
    }
}
