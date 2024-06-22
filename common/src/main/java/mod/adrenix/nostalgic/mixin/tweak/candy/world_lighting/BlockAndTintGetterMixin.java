package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.mixin.util.candy.lighting.LightingMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.GameUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockAndTintGetter.class)
public interface BlockAndTintGetterMixin extends BlockGetter
{
    /* Shadows */

    @Shadow
    int getBrightness(LightLayer lightType, BlockPos blockPos);

    /* Injections */

    /**
     * Modifies the client's determination of whether it can see the sky due to changes made by the old round-robin
     * relighting tweak.
     */
    @ModifyReturnValue(
        method = "canSeeSky",
        at = @At("RETURN")
    )
    default boolean nt_world_light$canSeeSky(boolean canSeeSky, BlockPos blockPos)
    {
        if (GameUtil.isOnIntegratedSeverThread() || !CandyTweak.ROUND_ROBIN_RELIGHT.get())
            return canSeeSky;

        return this.getBrightness(LightLayer.SKY, blockPos) >= LightingMixinHelper.getCombinedLight(this.getMaxLightLevel(), 0);
    }
}
