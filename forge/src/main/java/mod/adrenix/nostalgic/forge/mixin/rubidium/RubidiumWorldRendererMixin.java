package mod.adrenix.nostalgic.forge.mixin.rubidium;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkStatus;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkTracker;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import me.jellysquid.mods.sodium.client.util.frustum.Frustum;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.WorldClientUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SodiumWorldRenderer.class)
public abstract class RubidiumWorldRendererMixin
{
    /* Shadows */

    @Shadow private ClientLevel world;
    @Shadow private ChunkTracker chunkTracker;
    @Shadow private RenderSectionManager renderSectionManager;

    /* Injections */

    /**
     * Schedules a rebuilding task for all sections in Rubidium's render section manager when chunks need relighted.
     * Controlled by old light rendering.
     */
    @Inject(
        remap = false,
        method = "updateChunks",
        at = @At("HEAD")
    )
    private void NT$onUpdateChunks(Camera camera, Frustum frustum, int frame, boolean spectator, CallbackInfo callback)
    {
        if (WorldClientUtil.isRelightCheckEnqueued() && ModConfig.Candy.oldLightRendering())
        {
            this.chunkTracker.getChunks(ChunkStatus.FLAG_HAS_BLOCK_DATA).forEach((pos) -> {
                int x = ChunkPos.getX(pos);
                int z = ChunkPos.getZ(pos);

                for (int y = this.world.getMinSection(); y < this.world.getMaxSection(); y++)
                    this.renderSectionManager.scheduleRebuild(x, y, z, false);
            });
        }
    }

    /**
     * When Rubidium chunk updates are finished, if relighting of the chunks was performed, then the flag that controls
     * this functionality needs disabled.
     * <p>
     * Not controlled by any tweak.
     */
    @Inject(
        remap = false,
        method = "updateChunks",
        at = @At("RETURN")
    )
    private void NT$onFinishUpdateChunks(Camera camera, Frustum frustum, int frame, boolean spectator, CallbackInfo callback)
    {
        if (WorldClientUtil.isRelightCheckEnqueued())
            WorldClientUtil.setRelightFinished();
    }
}
