package mod.adrenix.nostalgic.forge.mixin.client;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.client.FogUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin allows for the mod to tap into optifine changes, if that mod is installed.
 * Otherwise, we should use Forge's ViewportEvent.RenderFog event.
 */

@Mixin(FogRenderer.class)
public abstract class OptifineFogRendererMixin
{
    @Inject(method = "setupFog", at = @At(value = "RETURN"))
    private static void NT$onSetupFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean nearFog, CallbackInfo callback)
    {
        if (NostalgicTweaks.OPTIFINE.get())
        {
            FogUtil.setupFog(camera, fogMode);
            FogUtil.setupNetherFog(camera, fogMode);
        }
    }
}
