package mod.adrenix.nostalgic.fabric.mixin;

import mod.adrenix.nostalgic.util.MixinUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FogRenderer.class, priority = MixinUtil.PRIORITY)
public abstract class FogRendererFabricMixin
{
    /**
     * Sky fog starts at 0 and goes to 80% of the far plane distance.
     * Terrain fog starts further away from the player and extends to the far plane distance.
     * This is controlled by the old fog toggle.
     */
    @Inject(method = "setupFog", at = @At(value = "RETURN"))
    private static void onSetupFog(Camera camera, FogRenderer.FogMode fogType, float farPlaneDistance, boolean nearFog, CallbackInfo callback)
    {
        MixinUtil.Fog.setupFog(camera, fogType);
    }

    /**
     * The old fog rendering in the nether starts where the player stands and extends off into the distance.
     * Controlled by the old nether fog toggle.
     */
    @Inject(method = "setupFog", at = @At(value = "RETURN"))
    private static void onSetupNetherFog(Camera camera, FogRenderer.FogMode fogType, float farPlaneDistance, boolean nearFog, CallbackInfo callback)
    {
        MixinUtil.Fog.setupNetherFog(camera, fogType);
    }
}
