package mod.adrenix.nostalgic.neoforge.mixin.embeddium.candy.world_lighting;

import mod.adrenix.nostalgic.mixin.util.candy.lighting.LightingMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.data.Pair;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.embeddedt.embeddium.impl.render.EmbeddiumWorldRenderer;
import org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkTracker;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkTrackerHolder;
import org.embeddedt.embeddium.impl.render.viewport.Viewport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EmbeddiumWorldRenderer.class)
public abstract class EmbeddiumWorldRendererMixin
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
        if (CandyTweak.ROUND_ROBIN_RELIGHT.get() && LightingMixinHelper.isRelightCheckEnqueued())
        {
            ChunkTracker.forEachChunk(ChunkTrackerHolder.get(this.world).getReadyChunks(), (x, z) -> {
                for (int y = this.world.getMinSection(); y < this.world.getMaxSection(); y++)
                    this.renderSectionManager.scheduleRebuild(x, y, z, false);
            });
        }

        if (LightingMixinHelper.RELIGHT_ALL_CHUNKS.get())
        {
            ChunkTrackerHolder.get(this.world).getReadyChunks().forEach(packedPos -> {
                Pair<Long, Byte> packedRelight = new Pair<>(packedPos, (byte) 1);
                LightingMixinHelper.PACKED_RELIGHT_QUEUE.add(packedRelight);
            });

            LightingMixinHelper.RELIGHT_ALL_CHUNKS.disable();
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
        if (LightingMixinHelper.isRelightCheckEnqueued())
            LightingMixinHelper.setRelightingAsFinished();
    }
}
