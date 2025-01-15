package mod.adrenix.nostalgic.neoforge.mixin.tweak.candy.debug_screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.adrenix.nostalgic.helper.candy.debug.DebugOverlayHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.Generic;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin
{
    /**
     * Prevents rendering of the NeoForge's debug information text so that the mod's information can be displayed
     * instead. The debug text event is not used because we perform special rendering on the debug screen.
     */
    @WrapOperation(
        method = "method_51746",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;renderLines(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;Z)V"
        )
    )
    private void nt_neoforge_debug_screen$wrapGameInformation(DebugScreenOverlay overlay, GuiGraphics graphics, List<String> lines, boolean leftSide, Operation<Void> operation)
    {
        if (CandyTweak.OLD_DEBUG.get() == Generic.MODERN)
        {
            operation.call(overlay, graphics, lines, leftSide);
            return;
        }

        if (leftSide)
            DebugOverlayHelper.renderDebugText(overlay, graphics);
    }
}
