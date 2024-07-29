package mod.adrenix.nostalgic.mixin.tweak.candy.world_sky;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.GameUtil;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DimensionSpecialEffects.class)
public abstract class DimensionSpecialEffectsMixin
{
    /**
     * Changes the overworld dimension's cloud height.
     */
    @ModifyReturnValue(
        method = "getCloudHeight",
        at = @At("RETURN")
    )
    private float nt_world_sky$setCloudHeight(float cloudHeight)
    {
        int customHeight = CandyTweak.OLD_CLOUD_HEIGHT.get();
        boolean isCustomHeight = customHeight != CandyTweak.OLD_CLOUD_HEIGHT.getDisabled();

        return GameUtil.isInOverworld() && isCustomHeight ? (float) customHeight : cloudHeight;
    }
}
