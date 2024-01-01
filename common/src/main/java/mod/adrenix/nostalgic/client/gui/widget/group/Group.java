package mod.adrenix.nostalgic.client.gui.widget.group;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.LayoutBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.RelativeLayout;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetHolder;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconTemplate;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.util.client.gui.DrawText;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class Group extends DynamicWidget<GroupBuilder, Group> implements RelativeLayout, WidgetHolder
{
    /* Builders */

    /**
     * Start the creation of a new {@link Group} instance.
     *
     * @param parent A {@link WidgetHolder} parent that manages {@link DynamicWidget}. This parent will receive widgets
     *               added and removed from this group.
     * @return A new {@link GroupBuilder} instance.
     */
    public static GroupBuilder create(WidgetHolder parent)
    {
        return new GroupBuilder(parent);
    }

    /* Fields */

    final IconWidget icon;
    final UniqueArrayList<DynamicWidget<?, ?>> widgets;

    /* Constructor */

    protected Group(GroupBuilder builder)
    {
        super(builder);

        this.widgets = builder.widgets;
        this.icon = IconTemplate.text(builder.icon).build();

        this.getBuilder().widgets.forEach(this::setWidgetRelative);
        this.getBuilder().addFunction(new GroupSync());
        this.getBuilder().addFunction(new GroupResizer());

        builder.parent.addWidgets(this.widgets);

        CollectionUtil.fromCast(this.getWidgetStream().map(DynamicWidget::getBuilder), LayoutBuilder.class)
            .forEach(layout -> layout.relativeTo(this));
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueArrayList<DynamicWidget<?, ?>> getWidgets()
    {
        return this.widgets;
    }

    /**
     * Set, and add, the given widget relative to the group's parent.
     *
     * @param widget A {@link DynamicWidget} instance.
     */
    protected void setWidgetRelative(DynamicWidget<?, ?> widget)
    {
        this.builder.parent.addWidget(widget);

        if (widget.getBuilder() instanceof LayoutBuilder<?, ?> layout)
            layout.relativeTo(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addWidget(DynamicWidget<?, ?> widget)
    {
        this.setWidgetRelative(widget);
        this.widgets.add(widget);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeWidget(DynamicWidget<?, ?> widget)
    {
        this.builder.parent.removeWidget(widget);
        this.widgets.remove(widget);
    }

    /**
     * @return Whether this group has a border around it.
     */
    @PublicAPI
    public boolean isBordered()
    {
        return this.builder.border;
    }

    /**
     * @return The {@link Color} instance being used by the group border.
     */
    @PublicAPI
    public Color getColor()
    {
        return this.builder.borderColor;
    }

    /**
     * @return Whether this group has a title.
     */
    @PublicAPI
    public boolean isTitled()
    {
        return this.builder.groupTitle != null;
    }

    /**
     * @return The title of this group with an ellipsis if the width of the title exceeds the group's border/width.
     */
    @PublicAPI
    public Component getTitle()
    {
        return TextUtil.ellipsis(GuiUtil.font()::width, this.builder.groupTitle.get(), this.width - (this.isBordered() ? 16 : 0));
    }

    /**
     * @return The padding between the ending x-position and the group's border if applicable.
     */
    @PublicAPI
    public int getInsidePaddingX()
    {
        return this.isBordered() ? 9 : 0;
    }

    /**
     * @return The padding between the ending y-position and the group's border if applicable.
     */
    @PublicAPI
    public int getInsidePaddingY()
    {
        return this.isBordered() || this.isTitled() ? 12 : 0;
    }

    /**
     * @return Where the origin is for widgets of this group relative to the game's x-axis.
     */
    @PublicAPI
    public int getInsideX()
    {
        return this.x + this.getInsidePaddingX();
    }

    /**
     * @return Where the origin is for widgets of this group relative to the game's y-axis.
     */
    @PublicAPI
    public int getInsideY()
    {
        return this.y + this.getInsidePaddingY();
    }

    /**
     * @return A modified width depending on whether this group is bordered.
     */
    @PublicAPI
    public int getInsideWidth()
    {
        return this.width - (this.isBordered() ? 18 : 0);
    }

    /**
     * @return A modified height depending on whether this group is bordered.
     */
    @PublicAPI
    public int getInsideHeight()
    {
        return this.height - (this.isBordered() ? 21 : 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRelativeX(DynamicWidget<?, ?> widget)
    {
        return this.getInsideX();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRelativeY(DynamicWidget<?, ?> widget)
    {
        return this.getInsideY();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAnchoredX(DynamicWidget<?, ?> widget)
    {
        return this.getInsideX();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAnchoredY(DynamicWidget<?, ?> widget)
    {
        return this.getInsideY();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (this.isInvisible())
            return;

        if (this.isBordered())
            this.renderBorder(graphics, mouseX, mouseY, partialTick);
        else if (this.builder.groupTitle != null)
        {
            int textX = this.x;
            int textY = this.y;

            if (this.builder.icon.isPresent())
            {
                this.icon.pos(textX, textY);
                this.icon.render(graphics, mouseX, mouseY, partialTick);

                textX = this.icon.getEndX() + 4;
            }

            DrawText.begin(graphics, this.getTitle()).pos(textX, textY).draw();
        }

        this.renderDebug(graphics);
    }

    /**
     * Renders the border around this group widget.
     *
     * @param graphics    The {@link GuiGraphics} object used for rendering.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    private void renderBorder(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        graphics.pose().pushPose();
        graphics.pose().translate(this.x, this.y, 0.0D);

        Color color = this.builder.borderColor;
        float yOffset = this.isTitled() ? (GuiUtil.textHeight() / 2.0F) - 2.0F : 0.0F;

        RenderUtil.beginBatching();
        RenderUtil.fill(graphics, 0.0F, 2.0F, yOffset + 2.0F, this.height, color);
        RenderUtil.fill(graphics, 2.0F, this.width - 2.0F, this.height - 2.0F, this.height, color);
        RenderUtil.fill(graphics, this.width - 2.0F, this.width, yOffset + 2.0F, this.height, color);

        if (this.builder.groupTitle != null)
        {
            Component title = this.getTitle();
            float atText = this.getInsideX() - this.x;
            float beforeText = atText - 4.0F;
            float afterText = beforeText + GuiUtil.font().width(title) + 7.0F;

            if (this.builder.icon.isPresent())
            {
                this.icon.pos(Math.round(atText), 0);
                this.icon.render(graphics, mouseX, mouseY, partialTick);

                float iconOffset = this.icon.getWidth() + 4.0F;
                atText += iconOffset;
                afterText += iconOffset;
            }

            RenderUtil.fill(graphics, 0.0F, beforeText, yOffset, yOffset + 2.0F, color);
            RenderUtil.fill(graphics, afterText, this.width, yOffset, yOffset + 2.0F, color);
            DrawText.begin(graphics, title).posX(atText).draw();
        }
        else
            RenderUtil.fill(graphics, 0.0F, this.width, 0.0F, 2.0F, color);

        RenderUtil.endBatching();
        graphics.pose().popPose();
    }
}
