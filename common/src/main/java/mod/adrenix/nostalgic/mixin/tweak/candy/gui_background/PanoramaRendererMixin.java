package mod.adrenix.nostalgic.mixin.tweak.candy.gui_background;


import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.timer.PartialTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    private float nt_panorama_renderer$DoPanoramaOverride(float bob)
    {
        if (CandyTweak.OLD_PANORAMA_ROTATION.get()) {
            time += PartialTick.realtime();
            return (float)(Math.sin(time/400.0f) * 25.0f + 20.0f);
        }
        return bob;
    }
    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void nt_panorama_renderer$DoBlurAndGradient(GuiGraphics guiGraphics, int width, int height, float fade, float partialTick, CallbackInfo ci) {
        if (CandyTweak.OLD_PANORAMA_BLUR.get()) {
            // Seemingly no way to control the blur, so just do it four times, cant hurt performance too much + we get to reuse minecraft code instead of inventing our own
            Minecraft.getInstance().gameRenderer.processBlurEffect(partialTick);
            Minecraft.getInstance().gameRenderer.processBlurEffect(partialTick);
            Minecraft.getInstance().gameRenderer.processBlurEffect(partialTick);
            Minecraft.getInstance().gameRenderer.processBlurEffect(partialTick);
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
            guiGraphics.fillGradient(0, 0, width, height, -2130706433, 16777215);
            guiGraphics.fillGradient(0, 0, width, height, 0, Integer.MIN_VALUE);
        }
    }
}