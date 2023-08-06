package mod.adrenix.nostalgic.fabric.mixin.sodium;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import me.jellysquid.mods.sodium.client.render.chunk.map.ChunkTracker;
import me.jellysquid.mods.sodium.client.render.chunk.map.ChunkTrackerHolder;
import me.jellysquid.mods.sodium.client.render.viewport.Viewport;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.WorldClientUtil;
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
     * Schedules a rebuilding task for all sections in Sodium's render section manager when chunks need relighted.
     * Controlled by the old light rendering tweak.
     */
    @Inject(
        method = "setupTerrain",
        at = @At("HEAD")
    )
    private void NT$onUpdateChunks(Camera camera, Viewport viewport, int frame, boolean spectator, boolean updateChunksImmediately, CallbackInfo callback)
    {
        if (WorldClientUtil.isRelightCheckEnqueued() && ModConfig.Candy.oldLightRendering())
        {
            ChunkTracker.forEachChunk(ChunkTrackerHolder.get(this.world).getReadyChunks(), (x, z) -> {
                for (int y = this.world.getMinSection(); y < this.world.getMaxSection(); y++)
                    this.renderSectionManager.scheduleRebuild(x, y, z, false);
            });
        }
    }

    /**
     * When Sodium chunk updates are finished, if relighting of the chunks was performed, then the flag that controls
     * this functionality needs disabled.
     * <p>
     * Not controlled by any tweak.
     */
    @Inject(
        method = "setupTerrain",
        at = @At("RETURN")
    )
    private void NT$onFinishUpdateChunks(Camera camera, Viewport viewport, int frame, boolean spectator, boolean updateChunksImmediately, CallbackInfo callback)
    {
        if (WorldClientUtil.isRelightCheckEnqueued())
            WorldClientUtil.setRelightFinished();
    }
}
