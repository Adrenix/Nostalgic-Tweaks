package mod.adrenix.nostalgic.mixin.tweak.candy.debug_screen;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import mod.adrenix.nostalgic.mixin.util.candy.debug.DebugMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.enums.DebugChart;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.debugchart.FpsDebugChart;
import net.minecraft.client.gui.components.debugchart.TpsDebugChart;
import net.minecraft.util.debugchart.LocalSampleLogger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin
{
    /* Shadows */

    @Shadow @Final private LocalSampleLogger frameTimeLogger;
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
     * Tracks whether the left or right side of the debug overlay is being rendered.
     */
    @Inject(
        method = "renderLines",
        at = @At("HEAD")
    )
    private void nt_debug_screen$onRenderLines(GuiGraphics graphics, List<String> lines, boolean leftSide, CallbackInfo callback, @Share("leftSide") LocalBooleanRef leftSideRef)
    {
        leftSideRef.set(leftSide);
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
    private boolean nt_debug_screen$showTextShadow(boolean dropShadow, @Share("leftSide") LocalBooleanRef leftSideRef)
    {
        if (leftSideRef.get())
            return CandyTweak.SHOW_DEBUG_LEFT_TEXT_SHADOW.get() || dropShadow;
        else
            return CandyTweak.SHOW_DEBUG_RIGHT_TEXT_SHADOW.get() || dropShadow;
    }

    /**
     * Changes the text's color when displaying game information.
     */
    @ModifyArg(
        method = "renderLines",
        index = 4,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"
        )
    )
    private int nt_debug_screen$setTextColor(int color, @Share("leftSide") LocalBooleanRef leftSideRef)
    {
        if (leftSideRef.get())
        {
            if (CandyTweak.SHOW_DEBUG_LEFT_TEXT_COLOR.get())
                return HexUtil.parseInt(CandyTweak.DEBUG_LEFT_TEXT_COLOR.get());
        }
        else
        {
            if (CandyTweak.SHOW_DEBUG_RIGHT_TEXT_COLOR.get())
                return HexUtil.parseInt(CandyTweak.DEBUG_RIGHT_TEXT_COLOR.get());
        }

        return color;
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
    private int nt_debug_screen$setTextBackground(int backgroundColor, @Share("leftSide") LocalBooleanRef leftSideRef)
    {
        if (leftSideRef.get())
        {
            if (CandyTweak.SHOW_DEBUG_LEFT_BACKGROUND.get())
                return HexUtil.parseInt(CandyTweak.DEBUG_LEFT_BACKGROUND_COLOR.get());
        }
        else
        {
            if (CandyTweak.SHOW_DEBUG_RIGHT_BACKGROUND.get())
                return HexUtil.parseInt(CandyTweak.DEBUG_RIGHT_BACKGROUND_COLOR.get());
        }

        return ModTweak.ENABLED.get() ? 0x00FFFFFF : backgroundColor;
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
