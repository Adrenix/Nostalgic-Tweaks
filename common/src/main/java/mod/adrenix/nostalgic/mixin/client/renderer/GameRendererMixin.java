package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.duck.CameraPitching;
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
    /* Shadows & Uniques */

    @Shadow @Final Minecraft minecraft;
    @Unique private boolean NT$isDirectionSet = false;

    /**
     * Brings back the vertical bobbing. Camera pitching data is injected into the player class. Controlled by vertical
     * bobbing tweak.
     */
    @Inject(
        method = "bobView",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private void NT$onBobView(PoseStack poseStack, float partialTicks, CallbackInfo callback)
    {
        if (ModConfig.Animation.oldVerticalBobbing() && this.minecraft.getCameraEntity() instanceof Player player)
        {
            CameraPitching injector = (CameraPitching) player;

            float distDelta = player.walkDist - player.walkDistO;
            float walkDist = -(player.walkDist + distDelta * partialTicks);
            float bob = Mth.lerp(partialTicks, player.oBob, player.bob);
            float pitch = Mth.lerp(partialTicks, injector.NT$getPrevCameraPitch(), injector.NT$getCameraPitch());

            // @formatter:off
            poseStack.translate(Mth.sin(walkDist * (float) Math.PI) * bob * 0.5F, -Math.abs(Mth.cos(walkDist * (float) Math.PI) * bob), 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(walkDist * (float) Math.PI) * bob * 3.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(Math.abs(Mth.cos(walkDist * (float) Math.PI - 0.2F) * bob) * 5.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
            // @formatter:on

            callback.cancel();
        }
    }

    /**
     * Changes the hurt direction based on the last hurt by mob. Controlled by the old damage tilt tweak.
     */
    @Inject(
        method = "bobHurt",
        at = @At("HEAD")
    )
    private void NT$onBobHurt(PoseStack poseStack, float partialTicks, CallbackInfo callback)
    {
        boolean isRandom = ModConfig.Animation.oldRandomTilt();
        boolean isVanilla = isRandom && !NostalgicTweaks.isNetworkVerified();

        if (isVanilla && this.minecraft.getCameraEntity() instanceof Player player)
        {
            if ((float) player.hurtTime - partialTicks > 0 && !this.NT$isDirectionSet)
            {
                this.NT$isDirectionSet = true;
                player.animateHurt((int) (Math.random() * 2.0) * 180);
            }
            else if ((float) player.hurtTime - partialTicks <= 0)
                this.NT$isDirectionSet = false;
        }
    }
}