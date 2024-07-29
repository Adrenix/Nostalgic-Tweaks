package mod.adrenix.nostalgic.client.gui.screen.config.widget.tag;

import mod.adrenix.nostalgic.client.gui.tooltip.Tooltip;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.client.gui.DrawText;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import mod.adrenix.nostalgic.util.common.text.TextWrap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

class Tag
{
    /* Fields */

    private final TagWidget widget;
    private final TagType type;
    private int offset;

    /* Constructor */

    /**
     * Create a new {@link Tag}. The purpose of this subclass is to individualize each {@link TagType} enumeration for
     * the tweak attached to a tag widget instance. The {@link Tag#offset} field tracks the x-offset from the parent
     * widget's x-position.
     *
     * @param widget The {@link TagWidget} instance.
     * @param type   The {@link TagType} enumeration.
     */
    Tag(TagWidget widget, TagType type)
    {
        this.offset = 0;
        this.widget = widget;
        this.type = type;
    }

    /* Static */

    /**
     * Helper that assigns a new offset based on a previous tag's offset, width, and padding.
     *
     * @param prev The previous tag.
     * @param next The next tag.
     */
    static void setFromPrev(Tag prev, Tag next)
    {
        next.offset = prev.offset + prev.getWidth() + prev.widget.padding;
    }

    /* Methods */

    /**
     * @return Whether this tag is an alert tag.
     */
    public boolean isAlertTag()
    {
        return this.type == TagType.ALERT;
    }

    /**
     * The x-coordinate of this tag is calculated by the parent tag widget x-coordinate in addition to the x-offset
     * calculated for this tag instance.
     *
     * @return This tag's x-coordinate relative to the parent tag widget.
     */
    public int getX()
    {
        return this.widget.getX() + this.offset;
    }

    /**
     * The y-coordinate of this tag is the same as the parent tag widget.
     *
     * @return This tag's y-coordinate relative to the parent tag widget.
     */
    public int getY()
    {
        return this.widget.getY();
    }

    /**
     * @return Get the width of this tag. Includes both the tag title width and the colored border padding.
     */
    public int getWidth()
    {
        return GuiUtil.font().width(this.type.getTitle()) + 3;
    }

    /**
     * @return Get the height of this tag. This will be the same as the parent tag widget.
     */
    public int getHeight()
    {
        return this.widget.getHeight();
    }

    /**
     * Check if the mouse is over this tag instance.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     * @return Whether the mouse is over this tag.
     */
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return MathUtil.isWithinBox(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    /**
     * Set the tooltip for this tag.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     */
    public void setTooltip(int mouseX, int mouseY)
    {
        boolean isHovered = this.widget.isHoveredOrFocused();
        boolean isMouseOver = this.isMouseOver(mouseX, mouseY);
        boolean isTooltipOn = ModTweak.DISPLAY_TAG_TOOLTIPS.get();

        if (!isMouseOver || !isTooltipOn || !isHovered)
            return;

        Component tooltip = switch (this.type)
        {
            case NEW -> Lang.Tag.NEW_TOOLTIP.get();
            case CLIENT -> Lang.Tag.CLIENT_TOOLTIP.get();
            case SERVER -> Lang.Tag.SERVER_TOOLTIP.get();
            case DYNAMIC -> Lang.Tag.DYNAMIC_TOOLTIP.get();
            case RELOAD -> Lang.Tag.RELOAD_TOOLTIP.get();
            case SYNC -> Lang.Tag.SYNC_TOOLTIP.get();
            case ALERT -> this.widget.getTweak().getAlertMessage();
            case WARNING -> this.widget.getTweak().getWarningMessage();
            case NO_SSO -> this.widget.getTweak().getNoSSOMessage();
        };

        Tooltip.setListTooltip(TextWrap.tooltip(tooltip, 40));
        Tooltip.setMousePosition(mouseX, mouseY);
    }

    /**
     * Render this tag to the screen.
     *
     * @param graphics The current {@link GuiGraphics} instance.
     */
    public void render(GuiGraphics graphics)
    {
        int borderX = this.getX();
        int borderY = this.getY();
        int borderEndX = this.getX() + this.getWidth();
        int borderEndY = this.getY() + this.getHeight();
        Color borderColor = this.type.getBorderColor();

        RenderUtil.fill(graphics, borderX, borderY, borderEndX, borderEndY, borderColor);

        int inX = this.getX() + 1;
        int inY = this.getY() + 1;
        int inEndX = this.getX() + this.getWidth() - 1;
        int inEndY = this.getY() + this.getHeight() - 1;
        Color inColor = this.type.getBackgroundColor();

        RenderUtil.fill(graphics, inX, inY, inEndX, inEndY, inColor);

        int titleX = this.getX() + 2;
        int titleY = this.getY() + 2;

        DrawText.begin(graphics, this.type.getTitle()).pos(titleX, titleY).flat().draw();
    }
}
