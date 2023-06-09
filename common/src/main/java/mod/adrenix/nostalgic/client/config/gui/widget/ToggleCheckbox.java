package mod.adrenix.nostalgic.client.config.gui.widget;

import mod.adrenix.nostalgic.client.config.gui.screen.list.ListScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.util.common.TextUtil;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/**
 * This checkbox widget is used by various parts of the config user interface system.
 * These widgets are used to toggle boolean flags.
 */

public class ToggleCheckbox extends Checkbox
{
    /* Static Fields */

    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    /* Fields */

    private final Screen screen;
    private int tooltipWidth = 40;
    private OnPress onPress;
    private Component tooltip;

    /* Click Interface */

    public interface OnPress
    {
        /**
         * Runs when a checkbox is clicked.
         * @param newValue The new value of the checkbox.
         */
        void press(boolean newValue);
    }

    /* Constructors */

    /**
     * Create a new toggle checkbox with a custom starting x and y position.
     * @param x The starting x-position of the checkbox.
     * @param y The starting y-position of the checkbox.
     * @param label The label of the checkbox.
     * @param state The default state of the checkbox.
     */
    public ToggleCheckbox(int x, int y, Component label, boolean state)
    {
        super(x, y, WIDTH, HEIGHT, label, state, true);

        this.screen = Minecraft.getInstance().screen;
        this.onPress = (newState) -> {};
        this.tooltip = null;
    }

    /**
     * Create a new toggle checkbox with more control options.
     * @param x The starting x-position of the checkbox.
     * @param y The starting y-position of the checkbox.
     * @param label The label of the checkbox.
     * @param tooltip The tooltip of the checkbox.
     * @param state The default state of the checkbox.
     * @param onPress A consumer that accepts a new value from {@link OnPress#press(boolean)}.
     */
    public ToggleCheckbox(int x, int y, Component label, Component tooltip, Supplier<Boolean> state, OnPress onPress)
    {
        super(x, y, WIDTH, HEIGHT, label, state.get(), true);

        this.screen = Minecraft.getInstance().screen;
        this.onPress = onPress;
        this.tooltip = tooltip;
    }

    /**
     * Create a new toggle checkbox with a custom state supplier and on press logic.
     *
     * The starting x-position is aligned to the config row list starting x-position and the starting y-position is set
     * to zero so that it may be redefined later by a row list renderer.
     *
     * @param label The label of the checkbox.
     * @param state A state supplier.
     * @param onPress A consumer that accepts a new value from {@link OnPress#press(boolean)}.
     */
    public ToggleCheckbox(Component label, Supplier<Boolean> state, OnPress onPress)
    {
        this(ConfigRowList.TEXT_START, 0, label, state.get());
        this.onPress = onPress;
    }

    /* Setters */

    /**
     * Set the tooltip for this checkbox along with a new maximum tooltip width value.
     * @param tooltip The tooltip to render.
     * @param width The maximum width of the tooltip box.
     */
    public void setTooltip(Component tooltip, int width)
    {
        this.tooltip = tooltip;
        this.tooltipWidth = width;
    }

    /**
     * Change the tooltip for this checkbox.
     * @param tooltip The tooltip to render.
     */
    public void setTooltip(Component tooltip) { this.setTooltip(tooltip, this.tooltipWidth); }

    /* Widget Overrides */

    /**
     * Handler method for when the checkbox is clicked.
     * Special logic is included if the active game screen is a swing screen instance.
     */
    @Override
    public void onPress()
    {
        super.onPress();

        this.onPress.press(this.selected());
    }

    /**
     * Handler method for rendering a tooltip for this toggle checkbox.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     */
    public void renderToolTip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        graphics.renderComponentTooltip(Minecraft.getInstance().font, TextUtil.Wrap.tooltip(this.tooltip, this.tooltipWidth), mouseX, mouseY);
    }

    /**
     * Handler method for when the checkbox widget is rendered.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        Font font = Minecraft.getInstance().font;
        int uOffset = 0;
        int vOffset = 103;
        int uWidth = 20;
        int vHeight = 20;

        if (this.isMouseOver(mouseX, mouseY))
        {
            uOffset = 20;
            vOffset = this.selected() ? 83 : vOffset;
        }
        else if (this.selected())
            vOffset = 83;

        graphics.blit(TextureLocation.WIDGETS, this.getX(), this.getY(), uOffset, vOffset, uWidth, vHeight);
        graphics.drawString(font, this.getMessage(), this.getX() + 24, this.getY() + (this.height - 8) / 2, 0xFFFFFF);

        if (this.isMouseOver(mouseX, mouseY) && this.tooltip != null)
        {
            if (this.screen instanceof ListScreen listScreen)
                listScreen.renderOverlayTooltips.add(this::renderToolTip);
            else
                this.renderToolTip(graphics, mouseX, mouseY);
        }
    }
}
