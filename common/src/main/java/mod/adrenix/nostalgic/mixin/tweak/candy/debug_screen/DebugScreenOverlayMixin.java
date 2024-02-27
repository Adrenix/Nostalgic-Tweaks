package mod.adrenix.nostalgic.mixin.tweak.candy.debug_screen;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.adrenix.nostalgic.mixin.util.candy.debug.DebugMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.DebugChart;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.debugchart.FpsDebugChart;
import net.minecraft.client.gui.components.debugchart.TpsDebugChart;
import net.minecraft.util.SampleLogger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin
{
    /* Shadows */

    @Shadow @Final private SampleLogger frameTimeLogger;
    @Shadow private boolean renderFpsCharts;

    @Shadow
    public abstract boolean showDebugScreen();

    /* Injections */

    /**
     * Forces the rendering of the profiler chart if the user wants to always see it in the debug screen.
     */
    @ModifyReturnValue(
        method = "showProfilerChart",
        at = @At("RETURN")
    )
    private boolean nt_debug_screen$showProfilerChart(boolean showChart)
    {
        if (CandyTweak.SHOW_DEBUG_PIE_CHART.get() && this.showDebugScreen())
            return true;

        return showChart;
    }

    /**
     * Changes the text rendering to have a shadow when displaying game information.
     */
    @ModifyArg(
        method = "renderLines",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"
        )
    )
    private boolean nt_debug_screen$showTextShadow(boolean dropShadow)
    {
        return CandyTweak.SHOW_DEBUG_TEXT_SHADOW.get() || dropShadow;
    }

    /**
     * Changes the text's background color when displaying game information.
     */
    @ModifyArg(
        index = 4,
        method = "renderLines",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"
        )
    )
    private int nt_debug_screen$setTextBackground(int backgroundColor)
    {
        if (CandyTweak.SHOW_DEBUG_BACKGROUND.get())
            return HexUtil.parseInt(CandyTweak.DEBUG_BACKGROUND_COLOR.get());

        return backgroundColor;
    }

    /**
     * Prevents rendering of the vanilla FPS chart so that the mod's FPS chart can be displayed instead. If the F3 + 2
     * keyboard shortcut is used, then the modern FPS chart is displayed.
     */
    @WrapOperation(
        method = "method_51746",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/debugchart/FpsDebugChart;drawChart(Lnet/minecraft/client/gui/GuiGraphics;II)V"
        )
    )
    private void nt_debug_screen$wrapFpsChartDraw(FpsDebugChart fpsChart, GuiGraphics graphics, int x, int width, Operation<Void> operation)
    {
        if (CandyTweak.FPS_CHART.get() == DebugChart.CLASSIC && !this.renderFpsCharts)
            DebugMixinHelper.renderFpsChart(fpsChart, this.frameTimeLogger, graphics);
        else
            operation.call(fpsChart, graphics, x, width);
    }

    /**
     * Prevents rendering of the vanilla TPS chart if the class FPS chart is being used and F3 + 2 wasn't used.
     */
    @WrapOperation(
        method = "method_51746",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/debugchart/TpsDebugChart;drawChart(Lnet/minecraft/client/gui/GuiGraphics;II)V"
        )
    )
    private void nt_debug_screen$wrapTpsChartDraw(TpsDebugChart tpsChart, GuiGraphics graphics, int x, int width, Operation<Void> operation)
    {
        if (CandyTweak.FPS_CHART.get() == DebugChart.CLASSIC && !this.renderFpsCharts)
            return;

        operation.call(tpsChart, graphics, x, width);
    }

    /**
     * Lets the debug overlay open with its FPS chart by default when pressing F3. If the FPS chart is disabled, then
     * the normal F3 + 2 shortcut will still bring up the game's FPS chart.
     */
    @ModifyExpressionValue(
        method = "method_51746",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;renderFpsCharts:Z"
        )
    )
    private boolean nt_debug_screen$showFpsChart(boolean renderFpsCharts)
    {
        return CandyTweak.FPS_CHART.get() != DebugChart.DISABLED || renderFpsCharts;
    }
}
