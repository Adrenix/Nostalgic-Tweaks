package mod.adrenix.nostalgic.mixin.common.server;

import mod.adrenix.nostalgic.common.config.MixinConfig;
import mod.adrenix.nostalgic.util.server.MixinServerUtil;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin
{
    /**
     * Forces the chunk border to render as a square instead of a disc.
     * Controlled by the old square chunk border tweak.
     */
    @Inject(method = "isChunkInRange", at = @At(value = "HEAD"), cancellable = true)
    private static void NT$onIsChunkRangeBorder(int chunkX, int chunkZ, int secX, int secZ, int viewDistance, CallbackInfoReturnable<Boolean> callback)
    {
        if (MixinConfig.Candy.oldSquareBorder())
            callback.setReturnValue(MixinServerUtil.World.squareDistance(chunkX, chunkZ, secX, secZ) <= viewDistance);
    }
}
