package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import mod.adrenix.nostalgic.helper.candy.light.LightingHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.GameUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockAndTintGetter.class)
public interface BlockAndTintGetterMixin extends BlockAndTintGetter
{
    /**
     * Modifies the client's determination of whether it can see the sky due to changes made by the old round-robin
     * relighting tweak. Ideally, a modification to the return value would be ideal, but these types of injections are
     * not allowed by the Mixin system used by Forge on 1.20.1.
     */
    @Override
    default boolean canSeeSky(@NotNull BlockPos blockPos)
    {
        if (GameUtil.isOnIntegratedSeverThread() || !CandyTweak.ROUND_ROBIN_RELIGHT.get())
            return this.getBrightness(LightLayer.SKY, blockPos) >= this.getMaxLightLevel();

        return this.getBrightness(LightLayer.SKY, blockPos) >= LightingHelper.getCombinedLight(this.getMaxLightLevel(), 0);
    }
}
