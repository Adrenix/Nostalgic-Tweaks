package mod.adrenix.nostalgic.init.listener.client;

import dev.architectury.event.events.client.ClientTooltipEvent;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
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
}
