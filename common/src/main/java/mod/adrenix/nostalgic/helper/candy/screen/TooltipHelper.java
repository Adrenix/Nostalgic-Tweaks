package mod.adrenix.nostalgic.helper.candy.screen;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.ColorType;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import net.minecraft.client.gui.GuiGraphics;

public abstract class TooltipHelper
{
    /**
     * Render an old tooltip box.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @param x        The x-coordinate of the tooltip.
     * @param y        The y-coordinate of the tooltip.
     * @param width    The width of the tooltip.
     * @param height   The height of the tooltip.
     * @param z        The z-index of the tooltip.
     */
    public static void render(GuiGraphics graphics, int x, int y, int width, int height, int z)
    {
        if (!CandyTweak.OLD_TOOLTIP_BOXES.get())
            return;

        int background = HexUtil.parseInt(CandyTweak.TOOLTIP_BACKGROUND_COLOR.get());
        int gradientTop = HexUtil.parseInt(CandyTweak.TOOLTIP_GRADIENT_TOP.get());
        int gradientBottom = HexUtil.parseInt(CandyTweak.TOOLTIP_GRADIENT_BOTTOM.get());

        if (CandyTweak.TOOLTIP_COLOR_TYPE.get() == ColorType.SOLID)
            graphics.fill(x - 3, y - 3, x + width + 3, y + height + 3, z, background);
        else
            graphics.fillGradient(x - 3, y - 3, x + width + 3, y + height + 3, z, gradientTop, gradientBottom);
    }
}
