package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigWidgets;
import mod.adrenix.nostalgic.client.config.gui.screen.list.ListScreen;
import mod.adrenix.nostalgic.util.common.MathUtil;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * An overlap button is a button that overlaps the edge of another button by 1 pixel. This overlap can be on side or on
 * both sides of the button.
 */

public class OverlapButton extends Button
{
    /* Fields */

    /**
     * This screen tracker is needed for widget gathering and rendering.
     */
    protected final ConfigScreen screen;

    /**
     * The list button flag is unique since the group tab bar for the config screen has a button that uses an image
     * sprite for display, rather than text.
     */
    private boolean isListButton = false;

    /**
     * This set is defined during constructions and will either be widgets from a config screen instance, or from a list
     * screen instance.
     */
    private final Set<Renderable> widgets;

    /* Constructors */

    /**
     * Constructor helper for getting the width of an overlap button based on the provided translated component.
     * @param text A component to get a translation from.
     * @return A width for an overlap button based on translation and width padding.
     */
    private static int getWidth(Component text)
    {
        return Minecraft.getInstance().font.width(text) + ConfigWidgets.WIDTH_PADDING;
    }

    /**
     * Create a new overlap button with custom position data.
     * @param startX The starting x-position.
     * @param startY The starting y-position.
     * @param width The button width.
     * @param height The button height.
     * @param text The button component title.
     * @param onPress Instructions for when the button is pressed.
     */
    public OverlapButton(int startX, int startY, int width, int height, Component text, OnPress onPress)
    {
        super(startX, startY, width, height, text, onPress, DEFAULT_NARRATION);

        this.screen = (ConfigScreen) Minecraft.getInstance().screen;

        if (Minecraft.getInstance().screen instanceof ListScreen listScreen)
            this.widgets = listScreen.getListWidgets();
        else if (Minecraft.getInstance().screen instanceof ConfigScreen configScreen)
            this.widgets = configScreen.getWidgets().children;
        else
            this.widgets = new HashSet<>();
    }

    /**
     * Create a new overlap button with predefined position data.
     * @param text The button component title.
     * @param onPress Instructions for when the button is pressed.
     */
    public OverlapButton(Component text, OnPress onPress)
    {
        this(0, ConfigWidgets.TOP_ROW, getWidth(text), ConfigWidgets.BUTTON_HEIGHT, text, onPress);
    }

    /* Helpers */

    /**
     * Utility method that returns the caller instance after changing button fields so that this overlap button appears
     * as a list button.
     *
     * @return The caller overlap button instance.
     */
    public OverlapButton setAsList()
    {
        this.isListButton = true;
        this.width = 20;
        this.height = 20;

        return this;
    }

    /**
     * Check if the mouse is over the given widget and check if the widget is not an instanceof this overlap button.
     * @param widget The widget to get dimensions from.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @return Whether the mouse is over the given widget and that widget is not an instance of the caller.
     */
    private boolean isMouseOver(AbstractWidget widget, int mouseX, int mouseY)
    {
        boolean isSame = widget.equals(this);
        boolean isInBounds = MathUtil.isWithinBox(mouseX, mouseY, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());

        return !isSame && isInBounds;
    }

    /**
     * Custom logic is needed for rendering tooltips since these buttons intentionally overlap each other.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @return Whether the tooltip associated with this button should be rendered.
     */
    protected boolean shouldRenderToolTip(int mouseX, int mouseY)
    {
        boolean isOtherHovered = false;

        for (Renderable child : this.widgets)
        {
            if (child instanceof AbstractWidget widget)
            {
                if (this.isMouseOver(widget, mouseX, mouseY))
                    isOtherHovered = true;
            }
        }

        return !isOtherHovered;
    }

    /* Overrides */

    /**
     * Handler method for rendering the button widget.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        for (Renderable child : this.widgets)
        {
            if (child instanceof AbstractWidget widget)
            {
                if (this.isMouseOver(widget, mouseX, mouseY))
                    this.isHovered = false;
            }
        }

        super.renderWidget(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Handler method for rendering list button textures.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (this.isListButton)
        {
            graphics.blit(TextureLocation.WIDGETS, this.getX(), this.getY(), this.isHovered && this.active ? 20 : 0, 163, 20, 20);
        }
    }
}
