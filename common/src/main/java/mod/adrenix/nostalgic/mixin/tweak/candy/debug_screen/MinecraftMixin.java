package mod.adrenix.nostalgic.mixin.tweak.candy.debug_screen;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.Tesselator;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.profiling.ProfileResults;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /* Shadows */

    @Shadow @Final private Window window;
    @Shadow @Final public Options options;

    /* Injections */

    /**
     * Adds a semitransparent black background to the FPS pie chart.
     */
    @Inject(
        method = "renderFpsMeter",
        at = @At(
            shift = At.Shift.AFTER,
            ordinal = 0,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;begin(Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lcom/mojang/blaze3d/vertex/VertexFormat;)V"
        )
    )
    private void nt_debug_screen$renderPieBackground(GuiGraphics graphics, ProfileResults results, CallbackInfo callback)
    {
        if (!CandyTweak.OLD_PIE_CHART_BACKGROUND.get())
            return;

        int color = 0xCF000000;
        int x = this.window.getWidth() - 170;
        int y = this.window.getHeight() - 320;

        RenderUtil.fill(Tesselator.getInstance().getBuilder(), x - 176.0F, y - 112.0F, x + 176.0F, y + 320.0F, color);
    }

    /**
     * Forces the rendering of the profiler chart if the user wants to always see it in the debug screen.
     */
    @ModifyReturnValue(
        method = "shouldRenderFpsPie",
        at = @At("RETURN")
    )
    private boolean nt_debug_screen$shouldRenderFpsPie(boolean shouldRenderFpsPie)
    {
        if (CandyTweak.SHOW_DEBUG_PIE_CHART.get() && this.options.renderDebug && !this.options.hideGui)
            return true;

        return shouldRenderFpsPie;
    }
}
