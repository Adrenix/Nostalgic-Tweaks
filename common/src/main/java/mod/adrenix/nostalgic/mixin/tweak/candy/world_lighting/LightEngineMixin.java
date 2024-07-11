package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.mixin.util.candy.lighting.NostalgicDataLayer;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.lighting.SkyLightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightEngine.class)
public abstract class LightEngineMixin
{
    /* Shadows */

    @Shadow @Final protected LightChunkGetter chunkSource;

    /* Injections */

    /**
     * Modifies returned light values from the client light engine to help simulate old light rendering.
     */
    @ModifyReturnValue(
        method = "getLightValue",
        at = @At("RETURN")
    )
    private int nt_world_lighting$getLightValue(int lightValue, BlockPos blockPos)
    {
        if (GameUtil.isOnIntegratedSeverThread() || ClassUtil.isNotInstanceOf(this.chunkSource, ClientChunkCache.class))
            return lightValue;

        boolean isSkyEngine = ClassUtil.isInstanceOf(this, SkyLightEngine.class);

        if (this.chunkSource.getLevel() instanceof ClientLevel level)
            return NostalgicDataLayer.getLightValue(isSkyEngine ? LightLayer.SKY : LightLayer.BLOCK, level, blockPos, lightValue);

        return lightValue;
    }

    /**
     * Returns a wrapped data layer with custom mod data to help simulate old light rendering.
     */
    @ModifyReturnValue(
        method = "getDataLayerData",
        at = @At("RETURN")
    )
    private DataLayer nt_world_lighting$getLightValue(DataLayer original)
    {
        if (GameUtil.isOnIntegratedSeverThread() || ClassUtil.isNotInstanceOf(this.chunkSource, ClientChunkCache.class))
            return original;

        boolean isSkyEngine = ClassUtil.isInstanceOf(this, SkyLightEngine.class);

        if (this.chunkSource.getLevel() instanceof ClientLevel level)
            return new NostalgicDataLayer(original, level, isSkyEngine ? LightLayer.SKY : LightLayer.BLOCK);

        return original;
    }
}
