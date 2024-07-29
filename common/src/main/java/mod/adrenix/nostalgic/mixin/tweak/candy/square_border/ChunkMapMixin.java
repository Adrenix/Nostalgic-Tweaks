package mod.adrenix.nostalgic.mixin.tweak.candy.square_border;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.candy.level.ServerLevelHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin
{
    /**
     * Forces the chunk border to render as a square instead of a disc.
     */
    @ModifyReturnValue(
        method = "isChunkInRange",
        at = @At("RETURN")
    )
    private static boolean nt_world_border$isChunkInRange(boolean isChunkInRange, int chunkX, int chunkZ, int secX, int secZ, int viewDistance)
    {
        if (isChunkInRange)
            return true;

        return CandyTweak.OLD_SQUARE_BORDER.get() && ServerLevelHelper.isChunkInRange(chunkX, chunkZ, secX, secZ, viewDistance);
    }
}
