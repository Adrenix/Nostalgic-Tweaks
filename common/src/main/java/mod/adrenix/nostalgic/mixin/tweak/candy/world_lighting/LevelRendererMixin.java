package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.helper.candy.light.LightingHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.common.data.NullableAction;
import mod.adrenix.nostalgic.util.common.data.Pair;
import net.minecraft.client.Camera;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
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
    @Shadow @Nullable private ViewArea viewArea;

    @Shadow
    public abstract boolean isChunkCompiled(BlockPos pos);

    @Shadow
    protected abstract void setSectionDirty(int sectionX, int sectionY, int sectionZ, boolean reRenderOnMainThread);

    /* Injections */

    /**
     * Relighting queues are slowly emptied at the start of each render pass of the level.
     */
    @Inject(
        method = "renderLevel",
        at = @At("HEAD")
    )
    private void nt_world_lighting$onRenderLevel(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo callback)
    {
        if (!ModTweak.ENABLED.get() || this.level == null)
            return;

        if (!LightingHelper.PACKED_RELIGHT_QUEUE.isEmpty())
        {
            Pair<Long, Byte> packedRelight = LightingHelper.PACKED_RELIGHT_QUEUE.pop();
            ChunkPos chunkPos = new ChunkPos(packedRelight.left());
            LevelChunk chunk = this.level.getChunkSource().getChunk(chunkPos.x, chunkPos.z, false);

            if (chunk != null)
                LightingHelper.relightChunk(chunk, packedRelight.right());
            else if (this.isChunkCompiled(chunkPos.getWorldPosition()))
                LightingHelper.PACKED_RELIGHT_QUEUE.add(packedRelight);
        }

        for (int i = 0; i < 4096; i++)
        {
            if (LightingHelper.PACKED_CHUNK_BLOCK_QUEUE.isEmpty())
                break;

            Pair<Long, Long> packedQueue = LightingHelper.PACKED_CHUNK_BLOCK_QUEUE.pop();
            ChunkPos chunkPos = new ChunkPos(packedQueue.left());
            LevelChunk chunk = this.level.getChunkSource().getChunk(chunkPos.x, chunkPos.z, false);

            if (chunk != null)
            {
                BlockPos blockPos = BlockPos.of(packedQueue.right());
                int x = blockPos.getX() & 15;
                int y = blockPos.getY() & 15;
                int z = blockPos.getZ() & 15;

                NullableAction.attempt(chunk.getSkyLightSources(), skyLightSources -> skyLightSources.update(chunk, x, y, z));

                chunk.getLevel().getLightEngine().checkBlock(blockPos);
            }
            else if (this.isChunkCompiled(chunkPos.getWorldPosition()))
                LightingHelper.PACKED_CHUNK_BLOCK_QUEUE.add(packedQueue);
        }
    }

    /**
     * Applies world relighting to all chunks when needed.
     */
    @Inject(
        method = "allChanged",
        at = @At("HEAD")
    )
    private void nt_world_lighting$onAllChanged(CallbackInfo callback)
    {
        if (!LightingHelper.RELIGHT_ALL_CHUNKS.get() || ModTracker.SODIUM.isInstalled() || this.level == null || this.viewArea == null)
            return;

        for (ChunkRenderDispatcher.RenderChunk renderChunk : this.viewArea.chunks)
        {
            if (renderChunk.getCompiledChunk().hasNoRenderableLayers())
                continue;

            long packedPos = SectionPos.of(renderChunk.getOrigin()).chunk().toLong();
            LightingHelper.PACKED_RELIGHT_QUEUE.add(new Pair<>(packedPos, (byte) 1));
        }

        LightingHelper.RELIGHT_ALL_CHUNKS.disable();
    }

    /**
     * Enqueues chunk relighting if requested by the old lighting engine.
     */
    @Inject(
        method = "compileChunks",
        at = @At("HEAD")
    )
    private void nt_world_lighting$onCompileSections(Camera camera, CallbackInfo callback)
    {
        boolean isRelightNeeded = CandyTweak.ROUND_ROBIN_RELIGHT.get() && LightingHelper.isRelightCheckEnqueued();

        if (!isRelightNeeded || this.viewArea == null || ModTracker.SODIUM.isInstalled())
            return;

        for (ChunkRenderDispatcher.RenderChunk renderChunk : this.viewArea.chunks)
        {
            SectionPos sectionPos = SectionPos.of(renderChunk.getOrigin());
            this.setSectionDirty(sectionPos.x(), sectionPos.y(), sectionPos.z(), true);
        }
    }

    /**
     * Changes chunk update priority to "none" so that lag spikes are prevented during world relighting.
     */
    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(
        method = "compileChunks",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"
        )
    )
    private <T> T nt_world_lighting$onGetCompilePriority(T chunkPriority)
    {
        if (ModTracker.SODIUM.isInstalled())
            return chunkPriority;

        return CandyTweak.ROUND_ROBIN_RELIGHT.get() && LightingHelper.isRelightCheckEnqueued() ? (T) PrioritizeChunkUpdates.NONE : chunkPriority;
    }

    /**
     * Marks the world relighting as finished.
     */
    @Inject(
        method = "compileChunks",
        at = @At("RETURN")
    )
    private void nt_world_lighting$onFinishCompileSections(Camera camera, CallbackInfo callback)
    {
        if (LightingHelper.isRelightCheckEnqueued() && !ModTracker.SODIUM.isInstalled())
            LightingHelper.setRelightingAsFinished();
    }
}
