package mod.adrenix.nostalgic.mixin.tweak.candy.square_border;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.mixin.util.candy.world.ServerWorldHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.server.level.ChunkTrackingView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkTrackingView.class)
public interface ChunkTrackingViewMixin
{
    /**
     * Forces the chunk border to render as a square instead of a disc.
     */
    @ModifyReturnValue(
        method = "isWithinDistance",
        at = @At("RETURN")
    )
    private static boolean nt_world_border$isChunkWithinDistance(boolean isWithinRange, int centerX, int centerZ, int viewDistance, int chunkX, int chunkZ, boolean searchAllChunks)
    {
        if (isWithinRange)
            return true;

        return CandyTweak.OLD_SQUARE_BORDER.get() && ServerWorldHelper.isChunkInRange(chunkX, chunkZ, centerX, centerZ, viewDistance);
    }
}
