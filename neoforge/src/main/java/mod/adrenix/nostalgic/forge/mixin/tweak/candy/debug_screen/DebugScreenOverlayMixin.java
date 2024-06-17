package mod.adrenix.nostalgic.forge.mixin.tweak.candy.debug_screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.adrenix.nostalgic.mixin.util.candy.debug.DebugMixinHelper;
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
     * Prevents rendering of the debug overlay's game information text so that the mod's information can be displayed
     * instead.
     */
    @WrapOperation(
        method = "method_51746",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;drawGameInformation(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;)V"
        )
    )
    private void nt_neoforge_debug_screen$wrapGameInformation(DebugScreenOverlay overlay, GuiGraphics graphics, List<String> list, Operation<Void> operation)
    {
        if (CandyTweak.OLD_DEBUG.get() == Generic.MODERN)
        {
            operation.call(overlay, graphics, list);
            return;
        }

        DebugMixinHelper.renderDebugText(overlay, graphics);
    }

    /**
     * Prevents rendering of the debug overlay's system information text so that the mod's information can be displayed
     * instead.
     */
    @WrapOperation(
        method = "method_51746",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;drawSystemInformation(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;)V"
        )
    )
    private void nt_neoforge_debug_screen$wrapSystemInformation(DebugScreenOverlay overlay, GuiGraphics graphics, List<String> list, Operation<Void> operation)
    {
        if (CandyTweak.OLD_DEBUG.get() == Generic.MODERN)
            operation.call(overlay, graphics, list);
    }
}
