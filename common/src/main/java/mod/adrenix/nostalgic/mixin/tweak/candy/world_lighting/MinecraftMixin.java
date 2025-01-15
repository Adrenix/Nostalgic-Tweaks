package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /**
     * Overrides the game's smooth lighting video setting. This is useful for config presets that are simulating older
     * versions of the game that did not have smooth lighting, such as alpha and classic. This tweak was originally
     * contributed by Xenthio.
     */
    @ModifyReturnValue(
        method = "useAmbientOcclusion",
        at = @At("RETURN")
    )
    private static boolean nt_world_lighting$modifyUseAmbientOcclusion(boolean useAmbientOcclusion)
    {
        return !CandyTweak.DISABLE_SMOOTH_LIGHTING.get() && useAmbientOcclusion;
    }
}
