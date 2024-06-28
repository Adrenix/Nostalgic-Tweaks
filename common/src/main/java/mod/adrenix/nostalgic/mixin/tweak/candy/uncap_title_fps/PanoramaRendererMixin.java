package mod.adrenix.nostalgic.mixin.tweak.candy.uncap_title_fps;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.timer.PartialTick;
import net.minecraft.client.renderer.PanoramaRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PanoramaRenderer.class)
public abstract class PanoramaRendererMixin
{
    /**
     * Modifies the partial tick used by the panorama to ensure that it uses realtime change between two ticks.
     */
    @ModifyVariable(
        index = 5,
        argsOnly = true,
        method = "render",
        at = @At("HEAD")
    )
    private float nt_title_screen$modifyPanoramaPartialTick(float partialTick)
    {
        return CandyTweak.UNCAP_TITLE_FPS.get() ? PartialTick.realtime() : partialTick;
    }
}
