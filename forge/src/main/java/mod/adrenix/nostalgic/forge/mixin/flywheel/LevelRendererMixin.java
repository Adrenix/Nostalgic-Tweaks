package mod.adrenix.nostalgic.forge.mixin.flywheel;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.light.LightListener;
import com.jozufozu.flywheel.light.LightUpdater;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.WorldClientUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    /* Shadows */

    @Shadow private @Nullable ViewArea viewArea;

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
     * was enqueued.
     *
     * Controlled by the old light rendering tweak.
     */
    @Inject(method = "compileChunks", at = @At("HEAD"))
    private void NT$onFlywheelCompileChunks(Camera camera, CallbackInfo callback)
    {
        LevelAccessor level = Minecraft.getInstance().level;
        boolean isModReady = ModConfig.Candy.oldLightRendering() && WorldClientUtil.isRelightCheckEnqueued();
        boolean isFlywheelReady = Backend.isOn();
        boolean isLevelReady = this.viewArea != null && level != null;

        if (isModReady && isFlywheelReady && isLevelReady)
            ((LightUpdaterAccessor) LightUpdater.get(level)).NT$getChunks().forEach(LevelRendererMixin::update);
    }
}
