package mod.adrenix.nostalgic.mixin.tweak.candy.debug_screen;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.Generic;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public abstract class GuiMixin
{
    /**
     * Prevents the axis crosshair from overriding the default crosshair when using the old debug screens.
     */
    @ModifyExpressionValue(
        method = "renderCrosshair",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/Options;renderDebug:Z"
        )
    )
    private boolean nt_debug_screen$renderCrosshair(boolean showDebugScreen)
    {
        if (CandyTweak.OLD_DEBUG.get().equals(Generic.MODERN))
            return showDebugScreen;

        return false;
    }
}
