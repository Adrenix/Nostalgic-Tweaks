package mod.adrenix.nostalgic.mixin.tweak.candy.gui_background;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public abstract class GuiMixin
{
    /**
     * Fixes the broken text offset position of the gui saving indicator text.
     */
    @WrapOperation(
        method = "renderSavingIndicator",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawStringWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)I"
        )
    )
    private int nt_gui_background$modifySavingIndicatorText(GuiGraphics graphics, Font font, Component text, int x, int y, int xOffset, int color, Operation<Integer> operation)
    {
        if (!CandyTweak.FIX_SAVING_INDICATOR_OFFSET.get())
            return operation.call(graphics, font, text, x, y, xOffset, color);

        int dx = graphics.guiWidth() - font.width(text) - 10;
        int dy = graphics.guiHeight() - 15;

        return graphics.drawString(font, text, dx, dy, color);
    }
}
