package mod.adrenix.nostalgic.forge.mixin.util;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.light.LightUpdater;
import mod.adrenix.nostalgic.forge.mixin.flywheel.candy.world_lighting.LightUpdaterAccess;
import mod.adrenix.nostalgic.helper.candy.light.LightingHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.Nullable;

/**
 * This utility is used only by the client.
 */
public abstract class FlywheelForgeHelper
{
    /**
     * Forces a light update on all light listeners when the round-robin chunk relighting is enabled and the level
     * relight flag is active.
     *
     * @param level The {@link ClientLevel} instance.
     */
    public static void sendLightUpdate(@Nullable ClientLevel level)
    {
        if (LightingHelper.isRelightCheckEnqueued() && Backend.isOn() && level != null)
        {
            ((LightUpdaterAccess) LightUpdater.get(level)).nt$getChunks().forEach(listener -> {
                listener.onLightUpdate(LightLayer.BLOCK, listener.getVolume());
                listener.onLightUpdate(LightLayer.SKY, listener.getVolume());
            });
        }
    }
}
