package mod.adrenix.nostalgic.mixin.client.world;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionType.class)
public abstract class DimensionTypeMixin
{
    /* Shadow & Unique Fields */

    @Shadow @Final private transient float[] brightnessRamp;
    @Unique private final float[] NT$vanillaBrightnessRamp = new float[16];
    @Unique private final float[] NT$moddedBrightnessRamp = new float[16];

    /**
     * The following injections bring back the old brightness table for old lighting.
     * Controlled by the old lighting tweak.
     */

    @Inject
    (
        method = "<init>(Ljava/util/OptionalLong;ZZZZDZZZZZIIILnet/minecraft/tags/TagKey;Lnet/minecraft/resources/ResourceLocation;F)V",
        at = @At(value = "RETURN")
    )
    private void NT$onCreateInstance(CallbackInfo callback)
    {
        float mod = 0.05F;
        for (int i = 0; i <= 15; i++)
        {
            float brightness = 1.0F - (float) i / 15.0F;
            this.NT$moddedBrightnessRamp[i] = ((1.0F - brightness) / (brightness * 3.0F + 1.0F)) * (1.0F - mod) + mod;
        }

        System.arraycopy(this.brightnessRamp, 0, this.NT$vanillaBrightnessRamp, 0, 16);
    }

    @Inject(method = "brightness", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onGetBrightness(int light, CallbackInfoReturnable<Float> callback)
    {
        if (MixinConfig.Candy.oldLighting())
            callback.setReturnValue(this.NT$moddedBrightnessRamp[light]);
        else
            callback.setReturnValue(this.NT$vanillaBrightnessRamp[light]);
    }
}
