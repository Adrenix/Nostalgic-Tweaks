package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.mixin.util.candy.lighting.LightingMixinHelper;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientChunkCache.class)
public abstract class ClientChunkCacheMixin
{
    /**
     * Adds a new client level chunk to the world relighting queue.
     */
    @ModifyExpressionValue(
        method = "replaceWithPacketData",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ChunkPos;)Lnet/minecraft/world/level/chunk/LevelChunk;"
        )
    )
    private LevelChunk nt_world_lighting$onCreateLevelChunk(LevelChunk chunk)
    {
        if (ModTweak.ENABLED.get())
            LightingMixinHelper.PACKED_RELIGHT_QUEUE.add(chunk.getPos().toLong());

        return chunk;
    }
}
