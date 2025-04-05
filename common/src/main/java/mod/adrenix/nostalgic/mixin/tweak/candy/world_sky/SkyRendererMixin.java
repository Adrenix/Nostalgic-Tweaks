package mod.adrenix.nostalgic.mixin.tweak.candy.world_sky;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.client.AfterConfigSave;
import mod.adrenix.nostalgic.helper.candy.level.SkyHelper;
import mod.adrenix.nostalgic.helper.candy.level.fog.VoidFogRenderer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.enums.Generic;
import mod.adrenix.nostalgic.tweak.enums.WorldFog;
import mod.adrenix.nostalgic.util.client.GameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin
{
    /* Shadows */

    @Mutable @Shadow @Final private VertexBuffer starBuffer;

    @Shadow protected abstract void buildSkyDisc(VertexConsumer buffer, float y);
    @Shadow protected abstract void buildStars(VertexConsumer buffer);

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void nt_world_sky$onCreateLevelRenderer(CallbackInfo callback)
    {
        SkyHelper.createBlueVoid(this::buildSkyDisc);

        if (SkyHelper.STARS_RUNNABLE_SAVED.ifDisabledThenEnable())
            AfterConfigSave.addInstruction(() -> starBuffer = VertexBuffer.uploadStatic(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION, this::buildStars));

        if (SkyHelper.BLUE_RUNNABLE_SAVED.ifDisabledThenEnable())
            AfterConfigSave.addInstruction(() -> SkyHelper.createBlueVoid(this::buildSkyDisc));
    }

    /**
     * Disables rendering of the dark void if the blue void is enabled and its respective override is enabled.
     */
    @WrapWithCondition(
            method = "renderDarkDisc",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithRenderType(Lnet/minecraft/client/renderer/RenderType;)V"
            )
    )
    private boolean nt_world_sky$onRenderVoid(VertexBuffer instance, RenderType renderType)
    {
        if (!ModTweak.ENABLED.get())
            return true;

        Generic voidState = CandyTweak.OLD_BLUE_VOID.get();
        boolean isBlueOverride = CandyTweak.OLD_BLUE_VOID_OVERRIDE.get();
        boolean isBlueRendered = Generic.ALPHA == voidState || Generic.BETA == voidState;

        if (!isBlueRendered || !isBlueOverride)
        {
            final float[] DARK_RGB = RenderSystem.getShaderColor();
            final float[] VOID_RGB = VoidFogRenderer.getVoidRGB();

            VoidFogRenderer.setVoidRGB(DARK_RGB[0], DARK_RGB[1], DARK_RGB[2]);

            if (VoidFogRenderer.isRendering())
                RenderSystem.setShaderColor(VOID_RGB[0], VOID_RGB[1], VOID_RGB[2], DARK_RGB[3]);

            return true;
        }

        return false;
    }

    /**
     * Changes the star brightness and transparency values.
     */
    @Inject(
            method = "renderStars",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFog(Lnet/minecraft/client/renderer/FogParameters;)V",
                    ordinal = 0
            )
    )
    private void nt_world_sky$onSetupStarColor(FogParameters fog, float starBrightness, PoseStack poseStack, CallbackInfo ci)
    {
        Generic starsState = CandyTweak.OLD_STARS.get();

        if (!ModTweak.ENABLED.get())
            return;

        float color = Generic.BETA == starsState ? starBrightness : starBrightness / 0.5F;

        VoidFogRenderer.setStarsTransparency(starBrightness);

        if (VoidFogRenderer.isRendering())
            starBrightness = VoidFogRenderer.getStarsTransparency();

        if (Generic.MODERN != starsState)
            RenderSystem.setShaderColor(color, color, color, starBrightness);

        if (CandyTweak.OLD_WORLD_FOG.get() == WorldFog.ALPHA_R164 && GameUtil.getRenderDistance() <= 4)
            RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 0.0F);
    }

    /**
     * Lets the dark void follow the camera's current y-level.
     */
    @ModifyArg(
            index = 1,
            method = "renderDarkDisc",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
            )
    )
    private float nt_world_sky$setTranslationForDarkSkyBuffer(float y)
    {
        if (!CandyTweak.OLD_DARK_VOID_HEIGHT.get())
            return y;

        return (float) (y - Math.max(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y - 65.0F, 0.0F));
    }


    /**
     * Changes the rotation of the sunrise/sunset sky disc color by 90 degrees on the Z-axis.
     */
    @ModifyArg(
            method = "renderSunriseAndSunset",
            at = @At(
                    ordinal = 0,
                    value = "INVOKE",
                    target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;"
            )
    )
    private float nt_world_sky$setSkyDiscColorRotation(float rotation)
    {
        return CandyTweak.OLD_SUNRISE_AT_NORTH.get() ? 0.0F : rotation;
    }

    /**
     * Changes the rotation of the sun and moon by 90 degrees on the y-axis.
     */
    @ModifyArg(
            method = "renderSunriseAndSunset",
            at = @At(
                    ordinal = 2,
                    value = "INVOKE",
                    target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;"
            )
    )
    private float nt_world_sky$setCelestialRotation(float rotation)
    {
        return CandyTweak.OLD_SUNRISE_AT_NORTH.get() ? 0.0F : rotation;
    }

    /**
     * Changes the width of the stars to simulate old stars.
     */
    @ModifyExpressionValue(
            method = "buildStars",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=0.15F"
            )
    )
    private float nt_world_sky$setStarWidth(float width)
    {
        return switch (CandyTweak.OLD_STARS.get())
        {
            case ALPHA, BETA -> 0.25F;
            default -> width;
        };
    }

    /**
     * Changes the height of the stars to simulate old stars.
     */
    @ModifyExpressionValue(
            method = "buildStars",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=0.1F"
            )
    )
    private float nt_world_sky$setStarHeight(float height)
    {
        return switch (CandyTweak.OLD_STARS.get())
        {
            case ALPHA, BETA -> 0.25F;
            default -> height;
        };
    }
}
