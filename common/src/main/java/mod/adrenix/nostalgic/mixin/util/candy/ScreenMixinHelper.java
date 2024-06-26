package mod.adrenix.nostalgic.mixin.util.candy;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

/**
 * This utility class is used only by the client.
 */
public abstract class ScreenMixinHelper
{
    /**
     * Check if the current screen should use a dirt background.
     *
     * @param backgroundLocation The menu {@link ResourceLocation} background.
     * @return Whether the current screen should render a dirt background.
     */
    public static boolean hasDirtBackground(ResourceLocation backgroundLocation)
    {
        if (!CandyTweak.OLD_DIRT_SCREEN_BACKGROUND.get())
            return false;

        boolean hasRowList = NullableResult.getOrElse(GuiUtil.getScreenOrNull(), false, screen -> screen.children()
            .stream()
            .anyMatch(widget -> ClassUtil.isInstanceOf(widget, AbstractSelectionList.class)));

        return Screen.MENU_BACKGROUND == backgroundLocation || hasRowList;
    }

    /**
     * Render a colored background on a screen.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @param width    The width of the background.
     * @param height   The height of the background.
     * @return Yields {@code true} if nothing was rendered, {@code false} otherwise.
     */
    public static boolean renderColoredBackground(GuiGraphics graphics, int width, int height)
    {
        if (Minecraft.getInstance().level == null || GuiUtil.getScreenAs(ProgressScreen.class).isPresent())
            return true;

        if (CandyTweak.APPLY_GUI_COLOR_BACKGROUND.get())
        {
            if (CandyTweak.CUSTOM_GUI_BACKGROUND.get())
            {
                int top = HexUtil.parseInt(CandyTweak.CUSTOM_GUI_TOP_GRADIENT.get());
                int bottom = HexUtil.parseInt(CandyTweak.CUSTOM_GUI_BOTTOM_GRADIENT.get());

                graphics.fillGradient(0, 0, width, height, top, bottom);
            }
            else
            {
                switch (CandyTweak.OLD_GUI_BACKGROUND.get())
                {
                    case SOLID_BLACK -> graphics.fillGradient(0, 0, width, height, 0xC0101010, 0xD0101010);
                    case SOLID_BLUE -> graphics.fillGradient(0, 0, width, height, 0xA0303060, 0xA0303060);
                    case GRADIENT_BLUE -> graphics.fillGradient(0, 0, width, height, 0x60050500, 0xA0303060);
                }
            }

            return false;
        }

        return true;
    }
}
