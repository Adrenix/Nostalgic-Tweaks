package mod.adrenix.nostalgic.client.gui.screen.config.widget.crumb;

import mod.adrenix.nostalgic.client.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconTemplate;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.client.gui.DrawText;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

class Crumb
{
    /* Fields */

    private final Container container;
    private final CrumbWidget widget;
    private final IconWidget icon;
    private final int index;
    private final int padding;
    private int line;
    private int offset;
    private boolean isLast;

    /* Constructor */

    Crumb(CrumbWidget widget, Container container)
    {
        this.line = 0;
        this.offset = 0;
        this.padding = 3;
        this.widget = widget;
        this.container = container;
        this.index = widget.crumbs.size() + 1;
        this.icon = IconTemplate.text(container.getIcon()).build();
    }

    /* Static */

    /**
     * Helper that assigns a new offset based on a previous crumb's offset and text width with slash width.
     *
     * @param prev The previous crumb.
     * @param next The next crumb.
     */
    public static void setFromPrev(Crumb prev, Crumb next)
    {
        next.offset = prev.offset + prev.getWidthWithSlash();
        next.line = prev.line;

        int endX = GuiUtil.getScreenWidth();

        if (next.widget.row != null)
            endX = next.widget.row.getEndX();

        if (next.getX() + next.getWidthWithSlash() > endX)
        {
            next.offset = 0;
            next.line++;
        }
    }

    /* Methods */

    /**
     * Set this crumb as the last crumb within a collection of crumbs.
     */
    public void setAsLast()
    {
        this.isLast = true;
    }

    /**
     * The x-coordinate of this crumb is calculated by the parent crumb widget x-coordinate in addition to the x-offset
     * calculated for this crumb instance.
     *
     * @return This crumb's x-coordinate relative to the parent crumb widget.
     */
    public int getX()
    {
        return this.widget.getX() + this.offset;
    }

    /**
     * The y-coordinate of this crumb is the same as the parent crumb widget.
     *
     * @return This crumb's y-coordinate relative to the parent crumb widget.
     */
    public int getY()
    {
        return this.widget.getY() + (this.line * this.widget.getLineHeight());
    }

    /**
     * @return Get the width of this crumb. This will be the font width of the container's translation.
     */
    public int getWidth()
    {
        int iconWidth = this.icon.getWidth();
        int textWidth = this.padding + GuiUtil.font().width(this.container.toString());

        return iconWidth + textWidth;
    }

    /**
     * This uses both {@link Crumb#getWidth()} and the font width of {@link CrumbWidget#SLASH}.
     *
     * @return The width of the crumb combined with the text width of the defined slash symbol.
     */
    public int getWidthWithSlash()
    {
        return this.getWidth() + GuiUtil.font().width(CrumbWidget.SLASH);
    }

    /**
     * @return The max width of all crumbs that share a line with this crumb.
     */
    public int getMaxWidthFromCrumbLine()
    {
        return this.widget.crumbs.stream()
            .filter(crumb -> crumb.line == this.line)
            .mapToInt(Crumb::getWidthWithSlash)
            .sum();
    }

    /**
     * @return Get the height of this crumb. This will be line height of the font.
     */
    public int getHeight()
    {
        return this.widget.getLineHeight();
    }

    /**
     * @return Get the line height for this crumb.
     */
    public int getLineHeight()
    {
        return (this.line + 1) * this.widget.getLineHeight();
    }

    /**
     * Check if the mouse is over this crumb instance.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     * @return Whether the mouse is over this crumb.
     */
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return MathUtil.isWithinBox(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    /**
     * Instructions to perform when the mouse clicks this crumb.
     */
    public void onClick()
    {
        GuiUtil.getScreenAs(ConfigScreen.class).ifPresent(screen -> {
            screen.setCategory(this.container.getCategory());
            screen.jumpTo(this.container);
        });
    }

    /**
     * The color of a crumb (<i>if it is not defined by its container</i>) will be assigned based on appearing index
     * order. The color pattern will repeat infinitely.
     *
     * @return A generic Minecraft chat color.
     */
    private ChatFormatting getColor()
    {
        ChatFormatting color = ChatFormatting.GOLD;

        if (this.index % 6 == 0)
            color = ChatFormatting.LIGHT_PURPLE;
        else if (this.index % 5 == 0)
            color = ChatFormatting.BLUE;
        else if (this.index % 4 == 0)
            color = ChatFormatting.AQUA;
        else if (this.index % 3 == 0)
            color = ChatFormatting.GREEN;
        else if (this.index % 2 == 0)
            color = ChatFormatting.YELLOW;

        return color;
    }

    /**
     * Render this crumb to the screen.
     *
     * @param graphics    The current {@link GuiGraphics} instance.
     * @param mouseX      The current x-coordinate of the mouse.
     * @param mouseY      The current y-coordinate of the mouse.
     * @param partialTick The normalized progress made between two ticks [0.0F, 1.0F].
     */
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        MutableComponent title = Component.literal(this.container.toString());

        if (this.container.isColorEmpty())
            title = title.copy().withStyle(this.getColor());

        if (this.isMouseOver(mouseX, mouseY) && this.widget.isHoveredOrFocused())
            title.withStyle(ChatFormatting.UNDERLINE);

        int x = this.getX();
        int y = this.getY();
        int color = this.container.getColor().get();

        this.icon.pos(x, y);
        this.icon.render(graphics, mouseX, mouseY, partialTick);

        int nextX = DrawText.begin(graphics, title).pos(this.icon.getEndX() + this.padding, y).color(color).draw();

        if (!this.isLast)
            DrawText.begin(graphics, CrumbWidget.SLASH).pos(nextX, this.getY()).draw();
    }
}
