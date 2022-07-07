package mod.adrenix.nostalgic.forge.mixin;

import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO:
/**
 * This mixin is temporary. There seems to have been a forge porting issue that leaves out the fog mode when the
 * RenderFogEvent was updated. Once that oversight is resolved, this mixin will be removed and the forge event reinstated.
 */
@Mixin(FogRenderer.class)
public abstract class TempFogRendererMixin
{
    @Inject(method = "setupFog", at = @At(value = "RETURN"))
    private static void NT$onSetupFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean nearFog, float partialTick, CallbackInfo callback)
    {
        ModClientUtil.Fog.setupFog(camera, fogMode);
        ModClientUtil.Fog.setupNetherFog(camera, fogMode);
    }
}
