package mod.adrenix.nostalgic.helper.candy.screen;

import mod.adrenix.nostalgic.client.gui.widget.WidgetBackground;
import mod.adrenix.nostalgic.mixin.access.AbstractWidgetAccess;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.resources.ResourceLocation;

/**
 * This utility class is used only by the client.
 */
public abstract class WidgetHelper
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

    /**
     * @return Whether to use the old style tabs on the world creation screen.
     */
    public static boolean isOldStyleTabs()
    {
        return Minecraft.getInstance().screen instanceof CreateWorldScreen && CandyTweak.OLD_STYLE_CREATE_WORLD_TABS.get();
    }

    /**
     * Render the old style tabs for the world creation screen.
     *
     * @param widget     The {@link AbstractWidget} tab button instance.
     * @param graphics   The {@link GuiGraphics} instance.
     * @param isSelected Whether the tab button is selected.
     */
    public static void renderOldStyleTabs(AbstractWidget widget, GuiGraphics graphics, boolean isSelected)
    {
        ResourceLocation sprite = WidgetBackground.BUTTON.get(widget.isActive(), widget.isHoveredOrFocused() || isSelected);
        int color = isSelected ? Color.LEMON_YELLOW.get() : getTextColor(widget, 1.0F);

        widget.setY(1);
        widget.setHeight(20);

        int minX = widget.getX() + 1;
        int minY = widget.getY();
        int maxX = widget.getX() + widget.getWidth() - 1;
        int maxY = widget.getY() + widget.getHeight();

        WidgetBackground.BUTTON.render(graphics, sprite, widget.getX(), widget.getY(), widget.getWidth() - 1, widget.getHeight());
        AbstractWidgetAccess.nt$renderScrollingString(graphics, GuiUtil.font(), widget.getMessage(), minX, minY, maxX, maxY, color);
    }
}
