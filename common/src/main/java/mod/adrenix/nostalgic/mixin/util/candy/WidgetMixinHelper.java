package mod.adrenix.nostalgic.mixin.util.candy;

import mod.adrenix.nostalgic.util.common.color.Color;
import net.minecraft.client.gui.components.AbstractWidget;

/**
 * This utility class is used only by the client.
 */
public abstract class WidgetMixinHelper
{
    /**
     * Get a new text color for a widget using the given alpha.
     *
     * @param widget An {@link AbstractWidget} instance.
     * @param alpha  The alpha to apply to the returned text color.
     * @return A new widget text color using the given alpha.
     */
    public static int getTextColor(AbstractWidget widget, float alpha)
    {
        Color color = Color.empty();

        if (!widget.isActive())
            color.set(Color.QUICK_SILVER);
        else if (widget.isHoveredOrFocused() && widget.isActive())
            color.set(Color.LEMON_YELLOW);
        else
            color.set(Color.NOSTALGIC_GRAY);

        color.setAlpha(alpha);

        return color.get();
    }
}
