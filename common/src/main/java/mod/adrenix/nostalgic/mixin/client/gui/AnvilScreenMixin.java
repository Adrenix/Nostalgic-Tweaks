package mod.adrenix.nostalgic.mixin.client.gui;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin
{
    /**
     * Prevents rendering the semi-transparent rectangle behind the enchantment cost text.
     * Controlled by the old anvil screen tweak.
     */
    @Redirect(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"))
    private void NT$onRenderLabelRectangle(GuiGraphics graphics, int minX, int minY, int maxX, int maxY, int color)
    {
        if (!ModConfig.Candy.oldAnvilScreen())
            graphics.fill(minX, minY, maxX, maxY, color);
    }

    /**
     * Changes the appearance and position of the enchant cost text.
     * Controlled by the old anvil screen tweak.
     */
    @Redirect(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"))
    private int NT$onRenderLabelText(GuiGraphics graphics, Font font, Component text, int x, int y, int color)
    {
        if (ModConfig.Candy.oldAnvilScreen())
        {
            int foreground = 0x203F08;
            int background = 0x80FF20;

            if (color == 0xFF6060)
            {
                foreground = 0x3F1818;
                background = 0xFF6060;
            }

            graphics.drawString(font, text, x + 3, y - 2, foreground, false);
            graphics.drawString(font, text, x + 3, y - 1, foreground, false);
            graphics.drawString(font, text, x + 2, y - 1, foreground, false);
            graphics.drawString(font, text, x + 2, y - 2, background, false);
        }
        else
            graphics.drawString(font, text, x, y, color);

        return 0;
    }
}
