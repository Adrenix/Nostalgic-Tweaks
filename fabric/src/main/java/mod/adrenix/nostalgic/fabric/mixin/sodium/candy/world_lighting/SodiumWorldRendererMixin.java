package mod.adrenix.nostalgic.fabric.mixin.sodium.candy.world_lighting;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import me.jellysquid.mods.sodium.client.render.chunk.map.ChunkTracker;
import me.jellysquid.mods.sodium.client.render.chunk.map.ChunkTrackerHolder;
import me.jellysquid.mods.sodium.client.render.viewport.Viewport;
import mod.adrenix.nostalgic.mixin.util.candy.lighting.LightingMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SodiumWorldRenderer.class)
public abstract class SodiumWorldRendererMixin
{
    /* Shadows */

    @Shadow private ClientLevel world;
    @Shadow private RenderSectionManager renderSectionManager;

    /* Injections */

    /**
     * Schedules rebuilding tasks for all chunks in Sodium's render section manager when world relighting is needed.
     */
    @Inject(
        method = "setupTerrain",
        at = @At("HEAD")
    )
    private void nt_sodium_world_lighting$onSetupTerrain(Camera camera, Viewport viewport, int frame, boolean spectator, boolean updateChunksImmediately, CallbackInfo callback)
    {
        if (CandyTweak.ROUND_ROBIN_RELIGHT.get() && LightingMixinHelper.isRelightCheckEnqueued())
        {
            ChunkTracker.forEachChunk(ChunkTrackerHolder.get(this.world).getReadyChunks(), (x, z) -> {
                for (int y = this.world.getMinSection(); y < this.world.getMaxSection(); y++)
                    this.renderSectionManager.scheduleRebuild(x, y, z, false);
            });
        }
    }

    /**
     * Marks the Sodium world relighting as finished.
     */
    @Inject(
        method = "setupTerrain",
        at = @At("RETURN")
    )
    private void nt_sodium_world_lighting$onFinishSetupTerrain(Camera camera, Viewport viewport, int frame, boolean spectator, boolean updateChunksImmediately, CallbackInfo callback)
    {
        if (LightingMixinHelper.isRelightCheckEnqueued())
            LightingMixinHelper.setRelightingAsFinished();
    }
}
