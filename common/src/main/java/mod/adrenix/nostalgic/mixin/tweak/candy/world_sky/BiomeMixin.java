package mod.adrenix.nostalgic.mixin.tweak.candy.world_sky;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.candy.level.SkyHelper;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Biome.class)
public abstract class BiomeMixin
{
    /**
     * Applies the old sky color to the world sky.
     */
    @ModifyReturnValue(
        method = "getSkyColor",
        at = @At("RETURN")
    )
    private int nt_world_sky$setSkyColor(int color)
    {
        return ModTweak.ENABLED.get() ? SkyHelper.getOldColor(color) : color;
    }
}
