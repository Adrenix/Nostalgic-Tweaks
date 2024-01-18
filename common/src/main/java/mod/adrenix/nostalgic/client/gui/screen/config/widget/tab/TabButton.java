package mod.adrenix.nostalgic.client.gui.screen.config.widget.tab;

import mod.adrenix.nostalgic.client.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.RowProvider;
import mod.adrenix.nostalgic.client.gui.widget.button.AbstractButton;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicField;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicFunction;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetCache;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.client.gui.DrawText;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

public class TabButton extends AbstractButton<TabBuilder, TabButton>
{
    /* Builder */

    /**
     * Create a new {@link TabButton} using a {@link TabBuilder}.
     *
     * @param configScreen The {@link ConfigScreen} managing this button.
     * @param category     The {@link Container} {@code category} this button is linked to.
     * @return A new {@link TabBuilder} instance.
     * @throws AssertionError If the given container is not a category.
     */
    public static TabBuilder create(ConfigScreen configScreen, Container category)
    {
        if (!category.isCategory())
            throw new AssertionError("Tab buttons must point to a category");

        return new TabBuilder(configScreen, category);
    }

    /* Colors */

    public static final Color DARK_BORDER = Color.BLACK.fromAlpha(0.7F);
    public static final Color WHITE_BORDER = Color.WHITE;
    public static final Color LIGHT_BORDER = Color.SILVER_CHALICE;
    public static final Color TAB_BACKGROUND = Color.PHILIPPINE_GRAY;
    public static final Color TAB_OVER_BACKGROUND = Color.QUICK_SILVER;

    /* Fields */

    private final Container category;
    private final ConfigScreen configScreen;

    private boolean isLeftHidden = false;
    private boolean isRightHidden = false;

    /* Constructor */

    /**
     * Create a new {@link TabButton} widget instance.
     *
     * @param builder A {@link TabBuilder} instance.
     * @param onPress A {@link Consumer} that accepts this {@link TabButton} instance.
     */
    protected TabButton(TabBuilder builder, Consumer<TabButton> onPress)
    {
        super(builder, onPress);

        this.configScreen = builder.configScreen;
        this.category = builder.category;

        builder.onPress(this::select);
        builder.renderer(this::renderer);
        builder.icon(this.category.getIcon(), GuiUtil.textHeight() - 1);
        builder.width(GuiUtil.textHeight() + 1 + GuiUtil.font().width(this.getTitle()) + 30);

        class Overflow implements DynamicFunction<TabBuilder, TabButton>
        {
            @Override
            public void apply(TabButton button, TabBuilder builder)
            {
            }

            @Override
            public boolean isReapplyNeeded(TabButton button, TabBuilder builder, WidgetCache cache)
            {
                if (button.isOverflow())
                    button.setHiddenRight();
                else
                    button.setVisibleRight();

                return false;
            }

            @Override
            public List<DynamicField> getManaging(TabBuilder builder)
            {
                return List.of();
            }
        }

        this.getBuilder().addFunction(new Overflow());
    }

    /* Methods */

    /**
     * @return The tweak category associated with this tab button.
     */
    public Container getCategory()
    {
        return this.category;
    }

    /**
     * Change the config category to the category stored in this tab button.
     */
    public void select()
    {
        this.configScreen.setCategory(this.category);
    }

    /**
     * @return Whether this tab is the currently selected tab in the config screen.
     */
    public boolean isSelected()
    {
        return this.category == this.configScreen.getCategory() && RowProvider.DEFAULT.isProviding();
    }

    /**
     * @return Whether this tab has an ending x-position outside the game window.
     */
    public boolean isOverflow()
    {
        return this.getEndX() > this.configScreen.width;
    }

    /**
     * @return Whether tab realignment should be performed.
     */
    public boolean isRealignNeeded()
    {
        return this.isSelected() && (this.isOverflow() || this.isLeftHidden);
    }

    /**
     * Used for tab overflowing.
     *
     * @return Whether this tab is hidden to the right.
     */
    public boolean isHiddenRight()
    {
        return this.isRightHidden;
    }

    /**
     * Used for tab overflowing.
     *
     * @return Whether this tab is hidden to the left.
     */
    public boolean isHiddenLeft()
    {
        return this.isLeftHidden;
    }

    /**
     * Set whether this tab is hidden by overflow to the right.
     */
    public void setHiddenRight()
    {
        this.isRightHidden = true;
    }

    /**
     * Set whether this tab is no longer hidden by overflow to the right.
     */
    public void setVisibleRight()
    {
        this.isRightHidden = false;
    }

    /**
     * Set whether this tab is hidden by overflow to the left.
     */
    public void setHiddenLeft()
    {
        this.isLeftHidden = true;
    }

    /**
     * Set whether this tab is no longer hidden by overflow to the left.
     */
    public void setVisibleLeft()
    {
        this.isLeftHidden = false;
    }

    /**
     * Set the starting x-position for this tab so that is to the right of the "move right" tab controller button.
     */
    public void setAtStartPosition()
    {
        this.setX(this.configScreen.getWidgetManager().getTabRight().getEndX() + 1);
    }

    /**
     * @return Whether the tab button is visible.
     */
    public boolean isVisible()
    {
        return !this.isLeftHidden;
    }

    /**
     * Renderer for a tab button.
     *
     * @param button      A {@link TabButton} instance.
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    public void renderer(TabButton button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        boolean isSelected = this.isSelected();
        boolean isHoverOrFocused = this.isHoveredOrFocused();

        int startY = isSelected ? this.y : this.y + 5 - (isHoverOrFocused ? 1 : 0);
        int startX = this.getX();
        int height = this.getHeight();
        int width = this.getWidth();
        int endX = startX + width;
        int endY = isSelected ? startY + height : startY + height - (isHoverOrFocused ? 5 : 6);

        if (!this.isVisible())
        {
            if (isSelected)
                RenderUtil.hLine(graphics, 0, endY - 1, GuiUtil.getScreenWidth(), LIGHT_BORDER);

            return;
        }

        RenderUtil.beginBatching();

        int background = this.category.getColor(TAB_BACKGROUND).darken(0.25D).get();
        int overBackground = this.category.getColor(TAB_OVER_BACKGROUND).darken(0.20D).get();

        if (isSelected)
        {
            int dx = this.getEndX();
            int dy = this.getEndY();
            int selectOverBackground = new Color(overBackground).brighten(0.05D).get();

            RenderUtil.fill(graphics, this.getX(), this.getY(), dx, dy, isHoverOrFocused ? selectOverBackground : overBackground);
            RenderUtil.hLine(graphics, 0, endY - 1, startX, LIGHT_BORDER);
            RenderUtil.hLine(graphics, endX, endY - 1, this.configScreen.width, LIGHT_BORDER);
        }
        else
            RenderUtil.fill(graphics, startX, startY, endX, endY, isHoverOrFocused ? overBackground : background);

        RenderUtil.vLine(graphics, startX, startY, endY, 0x5CFFFFFF);
        RenderUtil.hLine(graphics, startX + 1, startY, endX, 0x5CFFFFFF);
        RenderUtil.vLine(graphics, endX - 1, startY, endY, DARK_BORDER);

        int iconWidth = this.getIconManager().getWidth() + 3;
        int textWidth = GuiUtil.font().width(this.getTitle());
        int centerX = Math.round(MathUtil.center(startX, textWidth + iconWidth, width)) - 1;
        int centerY = Math.round(DrawText.centerY(startY, isSelected ? height : height - 3));

        this.getIconManager().pos(centerX, centerY);
        this.getIconManager().render(graphics, mouseX, mouseY, partialTick);

        ChatFormatting style = isSelected ? ChatFormatting.UNDERLINE : ChatFormatting.RESET;
        Component title = Component.literal(this.getTitle().getString()).withStyle(style);
        Color color = isHoverOrFocused ? Color.LEMON_YELLOW : Color.WHITE;

        DrawText.begin(graphics, title).pos(centerX + iconWidth, centerY).color(color).draw();
        RenderUtil.endBatching();
    }

    /**
     * Renderer for arrow tab button controllers.
     *
     * @param button      A {@link ButtonWidget} instance.
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    @PublicAPI
    public static void arrowRenderer(ButtonWidget button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        boolean isHoveredOrFocused = button.isHoveredOrFocused();

        int startX = button.getX();
        int startY = button.getY();
        int endX = button.getEndX();
        int endY = button.getEndY();
        Color fill = isHoveredOrFocused ? TAB_OVER_BACKGROUND : TAB_BACKGROUND;

        if (button.isInactive())
            fill = Color.DARK_GRAY;

        RenderUtil.beginBatching();
        RenderUtil.fill(graphics, startX, startY, endX, endY, fill);
        RenderUtil.vLine(graphics, startX, startY, endY, button.isActive() ? WHITE_BORDER : TAB_BACKGROUND);
        RenderUtil.hLine(graphics, startX + 1, startY, endX, button.isActive() ? WHITE_BORDER : TAB_BACKGROUND);
        RenderUtil.vLine(graphics, endX - 1, startY, endY, DARK_BORDER);

        float textX = DrawText.centerX(button.getX(), button.getWidth(), button.getTitle());
        float textY = DrawText.centerY(button.getY(), button.getHeight()) + 1;
        Color color = isHoveredOrFocused ? Color.LEMON_YELLOW : Color.WHITE;

        DrawText.begin(graphics, button.getTitle())
            .pos(textX, textY)
            .color(button.isActive() ? color : Color.TAUPE_GRAY)
            .draw();

        RenderUtil.endBatching();
    }
}
