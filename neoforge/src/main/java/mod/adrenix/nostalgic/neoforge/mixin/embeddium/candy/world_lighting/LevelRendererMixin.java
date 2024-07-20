package mod.adrenix.nostalgic.neoforge.mixin.embeddium.candy.world_lighting;

import mod.adrenix.nostalgic.helper.candy.light.LightingHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.data.Pair;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.embeddedt.embeddium.impl.render.EmbeddiumWorldRenderer;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkTracker;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkTrackerHolder;
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

    @Shadow @Nullable private ClientLevel level;

    /* Injections */

    /**
     * Schedules relighting for all chunks when needed.
     */
    @Inject(
        method = "renderLevel",
        at = @At("HEAD")
    )
    private void nt_embeddium_world_lighting$onRenderLevel(CallbackInfo callback)
    {
        if (CandyTweak.ROUND_ROBIN_RELIGHT.get() && LightingHelper.isRelightCheckEnqueued())
        {
            EmbeddiumWorldRenderer worldRenderer = EmbeddiumWorldRenderer.instanceNullable();

            if (worldRenderer == null || this.level == null)
                return;

            ChunkTracker.forEachChunk(ChunkTrackerHolder.get(this.level).getReadyChunks(), (x, z) -> {
                for (int y = this.level.getMinSection(); y < this.level.getMaxSection(); y++)
                    worldRenderer.scheduleRebuildForChunk(x, y, z, false);
            });
        }

        if (LightingHelper.RELIGHT_ALL_CHUNKS.get() && this.level != null)
        {
            ChunkTrackerHolder.get(this.level).getReadyChunks().forEach(packedPos -> {
                Pair<Long, Byte> packedRelight = new Pair<>(packedPos, (byte) 1);
                LightingHelper.PACKED_RELIGHT_QUEUE.add(packedRelight);
            });

            LightingHelper.RELIGHT_ALL_CHUNKS.disable();
        }
    }

    /**
     * Marks world relighting as finished.
     */
    @Inject(
        method = "renderLevel",
        at = @At("RETURN")
    )
    private void nt_embeddium_world_lighting$onFinishRenderLevel(CallbackInfo callback)
    {
        if (LightingHelper.isRelightCheckEnqueued())
            LightingHelper.setRelightingAsFinished();
    }
}
