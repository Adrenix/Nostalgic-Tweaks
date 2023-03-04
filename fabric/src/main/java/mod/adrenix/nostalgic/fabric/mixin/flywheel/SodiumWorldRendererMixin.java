package mod.adrenix.nostalgic.fabric.mixin.flywheel;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.light.LightListener;
import com.jozufozu.flywheel.light.LightUpdater;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.util.frustum.Frustum;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.WorldClientUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SodiumWorldRenderer.class)
public abstract class SodiumWorldRendererMixin
{
    /* Helpers */

    /**
     * Update the light layers for a light listener.
     */
    private static void update(LightListener listener)
    {
        listener.onLightUpdate(LightLayer.BLOCK, listener.getVolume());
        listener.onLightUpdate(LightLayer.SKY, listener.getVolume());
    }

    /* Injections */

    /**
     * Forces a light update on all light listeners when the user is using old light rendering and a new relight check
     * was enqueued with Sodium installed.
     *
     * Controlled by the old light rendering tweak.
     */
    @Inject(method = "updateChunks", at = @At("HEAD"))
    private void NT$onFlywheelUpdateChunks(Camera camera, Frustum frustum, int frame, boolean spectator, CallbackInfo callback)
    {
        LevelAccessor level = Minecraft.getInstance().level;

        if (Backend.isOn() && WorldClientUtil.isRelightCheckEnqueued() && ModConfig.Candy.oldLightRendering() && level != null)
            ((LightUpdaterAccessor) LightUpdater.get(level)).NT$getChunks().forEach(SodiumWorldRendererMixin::update);
    }
}
