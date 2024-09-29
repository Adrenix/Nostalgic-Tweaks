package mod.adrenix.nostalgic.mixin.tweak.candy.gui_background;


import mod.adrenix.nostalgic.util.client.timer.PartialTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PanoramaRenderer.class)
public abstract class PanoramaRendererMixin
{
    private float time;
    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/CubeMap;render(Lnet/minecraft/client/Minecraft;FFF)V"
            ),
            index = 1
    )
    private float nt_panorama_renderer$DoPanoramaBob(float bob)
    {
        time += PartialTick.realtime();
        return (float)(Math.sin(time/400.0f) * 25.0f + 20.0f);
    }
}