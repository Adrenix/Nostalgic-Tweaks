package mod.adrenix.nostalgic.fabric.mixin.client;

import mod.adrenix.nostalgic.util.client.ModClientUtil;
import mod.adrenix.nostalgic.util.common.ModCommonUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FogRenderer.class, priority = ModCommonUtil.APPLY_FIRST)
public abstract class FogRendererFabricMixin
{
    /**
     * Sky fog starts at 0 and goes to 80% of the far plane distance.
     * Terrain fog starts further away from the player and extends to the far plane distance.
     * This is controlled by the old fog tweak.
     */
    @Inject(method = "setupFog", at = @At(value = "RETURN"))
    private static void NT$onSetupFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean nearFog, float partialTick, CallbackInfo callback)
    {
        ModClientUtil.Fog.setupFog(camera, fogMode);
    }

    /**
     * The old fog rendering in the nether starts where the player stands and extends off into the distance.
     * Controlled by the old nether fog tweak.
     */
    @Inject(method = "setupFog", at = @At(value = "RETURN"))
    private static void NT$onSetupNetherFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean nearFog, float partialTick, CallbackInfo callback)
    {
        ModClientUtil.Fog.setupNetherFog(camera, fogMode);
    }
}
