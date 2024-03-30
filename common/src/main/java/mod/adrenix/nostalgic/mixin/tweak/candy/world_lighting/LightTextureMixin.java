package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.platform.NativeImage;
import mod.adrenix.nostalgic.mixin.util.candy.lighting.LightmapMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin
{
    /* Shadows */

    @Shadow private float blockLightRedFlicker;
    @Shadow @Final private NativeImage lightPixels;
    @Shadow @Final private Minecraft minecraft;

    @Shadow
    protected abstract float getDarknessGamma(float partialTick);

    @Shadow
    protected abstract float calculateDarknessScale(LivingEntity entity, float gamma, float partialTick);

    /* Injections */

    /**
     * Disables the red light flickering from light emitting sources.
     */
    @Inject(
        method = "tick",
        at = @At("RETURN")
    )
    private void nt_world_lighting$setRedLightFlicker(CallbackInfo callback)
    {
        if (CandyTweak.DISABLE_LIGHT_FLICKER.get())
            this.blockLightRedFlicker = 0.0F;
    }

    /**
     * This is an optimization injection that prevents the assignment of light pixels from the vanilla lightmap
     * creator.
     */
    @ModifyExpressionValue(
        method = "updateLightTexture",
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/client/renderer/LightTexture;blockLightRedFlicker:F"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/LightTexture;getBrightness(Lnet/minecraft/world/level/dimension/DimensionType;I)F"
            )
        ),
        at = @At(
            value = "CONSTANT",
            args = "intValue=16"
        )
    )
    private int nt_world_lighting$setLightmapIndexes(int index)
    {
        if (CandyTweak.OLD_CLASSIC_ENGINE.get() || CandyTweak.OLD_LIGHT_COLOR.get())
            return 0;

        return index;
    }

    /**
     * Simulates the old lighting engines by setting the lightmap texture image to grayscale.
     */
    @Inject(
        method = "updateLightTexture",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;upload()V"
        )
    )
    private void nt_world_lighting$onBeforeLightmapUpload(float partialTick, CallbackInfo callback)
    {
        if (CandyTweak.OLD_CLASSIC_ENGINE.get())
        {
            LightmapMixinHelper.setClassicTexture(this.lightPixels);
            return;
        }

        if (CandyTweak.OLD_LIGHT_COLOR.get())
        {
            float darknessScale = this.minecraft.options.darknessEffectScale().get().floatValue();
            float darknessGamma = this.getDarknessGamma(partialTick) * darknessScale;
            float darknessEffect = this.calculateDarknessScale(this.minecraft.player, darknessGamma, partialTick) * darknessScale;

            LightmapMixinHelper.setGrayscaleTexture(this.lightPixels, darknessEffect, partialTick);
        }
    }

    /**
     * Modifies the brightness applied to light levels.
     */
    @ModifyReturnValue(
        method = "getBrightness",
        at = @At("RETURN")
    )
    private static float nt_world_lighting$modifyGetBrightness(float brightness, DimensionType dimensionType, int lightLevel)
    {
        if (CandyTweak.OLD_LIGHT_COLOR.get())
            return Mth.lerp(dimensionType.ambientLight(), (float) Math.pow(0.8D, 15.0D - lightLevel), 1.0F);

        return brightness;
    }
}
