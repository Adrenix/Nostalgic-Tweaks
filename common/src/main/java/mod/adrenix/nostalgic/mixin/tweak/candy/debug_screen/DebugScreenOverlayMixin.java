package mod.adrenix.nostalgic.mixin.tweak.candy.debug_screen;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import mod.adrenix.nostalgic.helper.candy.debug.DebugOverlayHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.enums.DebugChart;
import mod.adrenix.nostalgic.tweak.enums.Generic;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.util.FrameTimer;
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

    @Shadow @Final private Minecraft minecraft;

    /* Injections */

    /**
     * Prevents rendering of the debug overlay's game information text so that the mod's information can be displayed
     * instead.
     */
    @WrapOperation(
        method = "method_51746",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;drawGameInformation(Lnet/minecraft/client/gui/GuiGraphics;)V"
        )
    )
    private void nt_debug_screen$wrapGameInformation(DebugScreenOverlay overlay, GuiGraphics graphics, Operation<Void> operation)
    {
        if (CandyTweak.OLD_DEBUG.get() == Generic.MODERN)
        {
            operation.call(overlay, graphics);
            return;
        }

        DebugOverlayHelper.renderDebugText(overlay, graphics);
    }

    /**
     * Prevents rendering of the debug overlay's system information text so that the mod's information can be displayed
     * instead.
     */
    @WrapOperation(
        method = "method_51746",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;drawSystemInformation(Lnet/minecraft/client/gui/GuiGraphics;)V"
        )
    )
    private void nt_debug_screen$wrapSystemInformation(DebugScreenOverlay overlay, GuiGraphics graphics, Operation<Void> operation)
    {
        if (CandyTweak.OLD_DEBUG.get() == Generic.MODERN)
            operation.call(overlay, graphics);
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
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;drawChart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/util/FrameTimer;IIZ)V"
        )
    )
    private void nt_debug_screen$wrapFpsChartDraw(DebugScreenOverlay overlay, GuiGraphics graphics, FrameTimer frameTimer, int x, int offset, boolean drawForFps, Operation<Void> operation)
    {
        if (CandyTweak.FPS_CHART.get() == DebugChart.CLASSIC && !this.minecraft.options.renderFpsChart)
            DebugOverlayHelper.renderFpsChart(graphics, frameTimer);
        else
            operation.call(overlay, graphics, frameTimer, x, offset, drawForFps);
    }

    /**
     * Prevents rendering of the vanilla TPS chart if the class FPS chart is being used and F3 + 2 wasn't used.
     */
    @WrapOperation(
        method = "method_51746",
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;drawChart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/util/FrameTimer;IIZ)V"
        )
    )
    private void nt_debug_screen$wrapTpsChartDraw(DebugScreenOverlay overlay, GuiGraphics graphics, FrameTimer frameTimer, int x, int offset, boolean drawForFps, Operation<Void> operation)
    {
        if (CandyTweak.FPS_CHART.get() == DebugChart.CLASSIC && !this.minecraft.options.renderFpsChart)
            return;

        operation.call(overlay, graphics, frameTimer, x, offset, drawForFps);
    }

    /**
     * Lets the debug overlay open with its FPS chart by default when pressing F3. If the FPS chart is disabled, then
     * the normal F3 + 2 shortcut will still bring up the game's FPS chart.
     */
    @ModifyExpressionValue(
        method = "method_51746",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/Options;renderFpsChart:Z"
        )
    )
    private boolean nt_debug_screen$showFpsChart(boolean renderFpsCharts)
    {
        if (CandyTweak.FPS_CHART.get() == DebugChart.DISABLED || !ModTweak.ENABLED.get())
            return renderFpsCharts;

        return true;
    }
}
