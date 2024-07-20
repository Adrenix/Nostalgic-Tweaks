package mod.adrenix.nostalgic.neoforge.mixin.embeddium.candy.world_lighting;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import me.jellysquid.mods.sodium.client.render.chunk.map.ChunkTracker;
import me.jellysquid.mods.sodium.client.render.chunk.map.ChunkTrackerHolder;
import me.jellysquid.mods.sodium.client.render.viewport.Viewport;
import mod.adrenix.nostalgic.helper.candy.light.LightingHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.data.Pair;
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
    private void nt_embeddium_world_lighting$onSetupTerrain(Camera camera, Viewport viewport, int frame, boolean spectator, boolean updateChunksImmediately, CallbackInfo callback)
    {
        if (CandyTweak.ROUND_ROBIN_RELIGHT.get() && LightingHelper.isRelightCheckEnqueued())
        {
            ChunkTracker.forEachChunk(ChunkTrackerHolder.get(this.world).getReadyChunks(), (x, z) -> {
                for (int y = this.world.getMinSection(); y < this.world.getMaxSection(); y++)
                    this.renderSectionManager.scheduleRebuild(x, y, z, false);
            });
        }

        if (LightingHelper.RELIGHT_ALL_CHUNKS.get())
        {
            ChunkTrackerHolder.get(this.world).getReadyChunks().forEach(packedPos -> {
                Pair<Long, Byte> packedRelight = new Pair<>(packedPos, (byte) 1);
                LightingHelper.PACKED_RELIGHT_QUEUE.add(packedRelight);
            });

            LightingHelper.RELIGHT_ALL_CHUNKS.disable();
        }
    }

    /**
     * Marks the Sodium world relighting as finished.
     */
    @Inject(
        method = "setupTerrain",
        at = @At("RETURN")
    )
    private void nt_embeddium_world_lighting$onFinishSetupTerrain(Camera camera, Viewport viewport, int frame, boolean spectator, boolean updateChunksImmediately, CallbackInfo callback)
    {
        if (LightingHelper.isRelightCheckEnqueued())
            LightingHelper.setRelightingAsFinished();
    }
}
