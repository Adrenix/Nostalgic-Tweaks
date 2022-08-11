package mod.adrenix.nostalgic.mixin.client.renderer;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.duck.ICameraPitch;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
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

    @Shadow @Final private MinecraftClient minecraft;
    @Unique private boolean NT$isDirectionSet = false;

    /**
     * Brings back the vertical bobbing. Camera pitching data is injected into the player class.
     * Controlled by vertical bobbing tweak.
     */
    @Inject(method = "bobView", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onBobView(MatrixStack poseStack, float partialTicks, CallbackInfo callback)
    {
        if (ModConfig.Animation.oldVerticalBobbing() && this.minecraft.getCameraEntity() instanceof PlayerEntity player)
        {
            ICameraPitch injector = (ICameraPitch) player;

            float distDelta = player.horizontalSpeed - player.prevHorizontalSpeed;
            float walkDist = -(player.horizontalSpeed + distDelta * partialTicks);
            float bob = MathHelper.lerp(partialTicks, player.prevStrideDistance, player.strideDistance);
            float pitch = MathHelper.lerp(partialTicks, injector.NT$getPrevCameraPitch(), injector.NT$getCameraPitch());

            poseStack.translate(MathHelper.sin(walkDist * (float) Math.PI) * bob * 0.5F, -Math.abs(MathHelper.cos(walkDist * (float) Math.PI) * bob), 0.0F);
            poseStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.sin(walkDist * (float) Math.PI) * bob * 3.0F));
            poseStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(Math.abs(MathHelper.cos(walkDist * (float) Math.PI - 0.2F) * bob) * 5.0F));
            poseStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(pitch));

            callback.cancel();
        }
    }

    /**
     * Changes the hurt direction based on the last hurt by mob.
     * Controlled by the old damage tilt tweak.
     */
    @Inject(method = "bobHurt", at = @At("HEAD"))
    private void NT$onBobHurt(MatrixStack poseStack, float partialTicks, CallbackInfo callback)
    {
        boolean isRandom = ModConfig.Animation.oldRandomTilt();
        boolean isVanilla = isRandom && !NostalgicTweaks.isNetworkVerified();
        boolean isOverride = isRandom && !ModConfig.Animation.oldDirectionTilt();
        boolean isTilted = isVanilla || isOverride;

        if (isTilted && this.minecraft.getCameraEntity() instanceof PlayerEntity player)
        {
            if ((float) player.hurtTime - partialTicks > 0 && !this.NT$isDirectionSet)
            {
                this.NT$isDirectionSet = true;
                player.knockbackVelocity = (int) (Math.random() * 2.0) * 180;
            }
            else if ((float) player.hurtTime - partialTicks <= 0)
                this.NT$isDirectionSet = false;
        }
    }
}