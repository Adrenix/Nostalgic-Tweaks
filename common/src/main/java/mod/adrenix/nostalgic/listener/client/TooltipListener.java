package mod.adrenix.nostalgic.listener.client;

import dev.architectury.event.events.client.ClientTooltipEvent;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.ColorType;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import net.minecraft.client.gui.GuiGraphics;

public abstract class TooltipListener
{
    /**
     * Registers client tooltip events.
     */
    public static void register()
    {
        ClientTooltipEvent.RENDER_MODIFY_COLOR.register(TooltipListener::setTooltipColor);
    }

    /**
     * Sets all tooltip colors invisible so the TooltipRenderUtil injection can render the black semi-transparent
     * background with the correct dimensional size.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @param x        The x-coordinate of the tooltip.
     * @param y        The y-coordinate of the tooltip.
     * @param context  The current {@link ClientTooltipEvent.ColorContext} context.
     */
    private static void setTooltipColor(GuiGraphics graphics, int x, int y, ClientTooltipEvent.ColorContext context)
    {
        if (!CandyTweak.OLD_TOOLTIP_BOXES.get())
            return;

        context.setBackgroundColor(0);
        context.setOutlineGradientBottomColor(0);
        context.setOutlineGradientTopColor(0);
    }

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
