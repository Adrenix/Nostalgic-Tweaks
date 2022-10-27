package mod.adrenix.nostalgic.mixin.common.server;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.server.WorldServerUtil;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin
{
    /**
     * Forces the chunk border to render as a square instead of a disc.
     * Controlled by the old square chunk border tweak.
     */
    @Inject(method = "isChunkInRange", at = @At(value = "HEAD"), cancellable = true)
    private static void NT$onIsChunkRange(int chunkX, int chunkZ, int secX, int secZ, int viewDistance, CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Candy.oldSquareBorder())
            callback.setReturnValue(WorldServerUtil.isChunkInRange(chunkX, chunkZ, secX, secZ, viewDistance));
    }
}
