package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.helper.candy.light.LightingHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.data.Pair;
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
        if (CandyTweak.OLD_WATER_LIGHTING.get() || CandyTweak.CHEST_LIGHT_BLOCK.get())
            LightingHelper.PACKED_RELIGHT_QUEUE.add(new Pair<>(chunk.getPos().toLong(), (byte) 0));

        return chunk;
    }
}
