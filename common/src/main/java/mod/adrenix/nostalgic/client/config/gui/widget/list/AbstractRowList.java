package mod.adrenix.nostalgic.client.config.gui.widget.list;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ContainerButton;
import mod.adrenix.nostalgic.client.config.gui.widget.group.TextGroup;
import mod.adrenix.nostalgic.mixin.widen.AbstractSelectionListAccessor;
import mod.adrenix.nostalgic.mixin.widen.AbstractWidgetAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * This class defines the row lists that are used throughout various parts of the mod's configuration graphical user
 * interface system. These lists can be transparent or opaque and can have their individual row item heights dynamically
 * changed during runtime.
 *
 * @param <R> A class type that will be used for rows.
 */

public abstract class AbstractRowList<R extends AbstractEntry<R>> extends ContainerObjectSelectionList<R>
{
    /* Fields */

    /**
     * If a row list instance is set as transparent, then nothing will be rendered behind the individual row instances.
     * If this field is set to false, then a semi-opaque black background will be rendered.
     *
     * If the game is not within a level, then the default dirt background will be rendered if this list is set as a
     * fully transparent list.
     */
    private boolean isTransparentList = true;

    /**
     * This fields keeps track of the original item row height. This is needed so that the row item heights can be
     * dynamically changed during runtime.
     */
    private final int originalItemHeight;

    /* Constructor */

    /**
     * Create a new abstract row list instance.
     * @param width The width of the row list.
     * @param height The height of the row list.
     * @param startY The starting position of where individual row entries are visible.
     * @param endY The ending position of where individual row entries are no longer visible.
     * @param rowHeight The maximum height of individual row entries.
     */
    public AbstractRowList(int width, int height, int startY, int endY, int rowHeight)
    {
        super(Minecraft.getInstance(), width, height, startY, endY, rowHeight);

        this.centerListVertically = false;
        this.originalItemHeight = this.itemHeight;
    }

    /* Methods */

    /**
     * A getter method that provides the row list's current row item height.
     * @return The row list's row item height.
     */
    public int getRowHeight() { return this.itemHeight; }

    /**
     * Resets the row list's row item height to its original row item height.
     */
    public void resetRowHeight() { this.itemHeight = this.originalItemHeight; }

    /**
     * Change the row list's row item height.
     * @param height The new row item height.
     */
    public void setRowHeight(int height) { this.itemHeight = height; }

    /**
     * Set this row list as being semi-transparent. This will set the {@link AbstractRowList#isTransparentList} field
     * flag to false. This results in the row list having a semi-opaque black background.
     */
    public void setAsSemiTransparent() { this.isTransparentList = false; }

    /**
     * Sets the scrollbar so that is centered on the given row entry.
     * @param entry A row instance that is associated with this row list.
     */
    public void setScrollOn(R entry) { this.centerScrollOn(entry); }

    /**
     * Reset the scrollbar to the top of the list.
     */
    public void resetScrollbar() { this.setScrollAmount(0.0D); }

    /*
       Tab Key Utility

       The following fields and methods are used for tab key support. When the tab key is pressed, the row list should
       update which row/widget should be highlighted. A filtering bi-function can be used to skip widgets that should
       not be considered for highlighting. Holding the shift key while pressing a tab key will go backwards.
     */

    /* Tab Fields */

    /**
     * Keeps track of the last widget that was selected within this row list.
     */
    private AbstractWidget tabLastSelectedWidget = null;

    /* Tab Methods */

    /**
     * A getter method that returns the last widget that was selected by tabbing.
     * @return The last abstract widget instance that was selected.
     */
    public AbstractWidget getLastSelection() { return this.tabLastSelectedWidget; }

    /**
     * Set the last widget that was selected by tabbing.
     * @param widget An abstract widget instance.
     */
    public void setLastSelection(AbstractWidget widget) { this.tabLastSelectedWidget = widget; }

    /**
     * Reset the last widget selection that was selected by tabbing. This will set the
     * {@link AbstractRowList#tabLastSelectedWidget} field as <code>null</code>.
     */
    public void resetLastSelection() { this.tabLastSelectedWidget = null; }

    /**
     * Removes any focus that is set on a selected widget.
     * @return Whether a widget was unfocused.
     */
    public boolean unsetFocus()
    {
        if (this.tabLastSelectedWidget != null)
        {
            ((AbstractWidgetAccessor) this.tabLastSelectedWidget).NT$setFocus(false);
            this.tabLastSelectedWidget = null;

            return true;
        }

        return false;
    }

    /**
     * Checks whether the focused widget handled a key pressing event.
     * @param keyCode A key code.
     * @param scanCode A key scancode.
     * @param modifiers Any held modifiers.
     * @return Whether a focused widget handled a key press event.
     */
    public boolean getFocusKeyPress(int keyCode, int scanCode, int modifiers)
    {
        /*
           This boolean supplier is required to avoid out of bounds exceptions. It is possible that a widget can change
           the row list after being pressed. Therefore, we do not want to run any instructions that will change the
           list after checking if a widget handles key input. Caching a runnable avoids this problem so that key press
           instructions can be run without interfering for loop execution.
         */

        Supplier<Boolean> press = null;

        // Cache key presses to focused widgets
        for (R row : this.children())
        {
            for (GuiEventListener listener : row.children())
            {
                if (listener instanceof AbstractWidget widget)
                {
                    if (widget.isFocused())
                    {
                        press = () -> widget.keyPressed(keyCode, scanCode, modifiers);
                        break;
                    }
                }
            }

            if (press != null)
                break;
        }

        // Run key presses
        AtomicBoolean isPressed = new AtomicBoolean(false);

        if (press != null)
            isPressed.set(press.get());

        return isPressed.get();
    }

    /**
     * Get the next eligible widget for tab focusing.
     * @param filterWidget A bi-function that accepts a row and an abstract widget instance and returns whether that
     *                     given widget is eligible for focusing.
     * @return Whether a widget was successfully focused.
     */
    public boolean setFocus(@Nullable BiFunction<R, AbstractWidget, Boolean> filterWidget)
    {
        R scrollSelection = null;
        R tabFirstRow = null;
        R tabLastRow = null;
        R tabPreviousRow = null;
        R tabNextRow = null;

        AbstractWidget tabFirstWidget = null;
        AbstractWidget tabLastWidget = null;
        AbstractWidget tabPreviousWidget = null;
        AbstractWidget tabNextWidget = null;

        // Find the first eligible selectable widget
        for (R row : this.children())
        {
            for (GuiEventListener listener : row.children())
            {
                if (listener instanceof AbstractWidget widget)
                {
                    if (filterWidget != null && filterWidget.apply(row, widget))
                        continue;

                    tabFirstRow = row;
                    tabFirstWidget = widget;

                    break;
                }
            }

            if (tabFirstWidget != null)
                break;
        }

        // Find the first last eligible selectable widget
        for (int i = this.children().size() - 1; i >= 0; i--)
        {
            R row = this.children().get(i);

            for (int j = row.children().size() - 1; j >= 0; j--)
            {
                GuiEventListener listener = row.children().get(j);

                if (listener instanceof AbstractWidget widget)
                {
                    if (filterWidget != null && filterWidget.apply(row, widget))
                        continue;

                    tabLastRow = row;
                    tabLastWidget = widget;

                    break;
                }
            }

            if (tabLastWidget != null)
                break;
        }

        // Get surrounding widgets of the current selection, so we can jump depending on shift key state
        if (this.tabLastSelectedWidget != null)
        {
            boolean getNextWidget = false;

            for (R row : this.children())
            {
                for (GuiEventListener listener : row.children())
                {
                    if (listener instanceof AbstractWidget widget)
                    {
                        if (getNextWidget)
                        {
                            if (filterWidget != null && filterWidget.apply(row, widget))
                                continue;

                            tabNextRow = row;
                            tabNextWidget = widget;

                            break;
                        }
                        else if (!widget.equals(this.tabLastSelectedWidget))
                        {
                            boolean isFiltered = filterWidget != null && filterWidget.apply(row, widget);

                            if (!isFiltered)
                            {
                                tabPreviousRow = row;
                                tabPreviousWidget = widget;
                            }
                        }

                        if (widget.equals(this.tabLastSelectedWidget))
                            getNextWidget = true;
                    }
                }

                if (tabNextWidget != null)
                    break;
            }
        }

        // Go to next/previous first/last based on shift state and last selection
        if (this.tabLastSelectedWidget == null)
        {
            if (Screen.hasShiftDown() && tabLastWidget != null)
            {
                scrollSelection = tabLastRow;
                ((AbstractWidgetAccessor) tabLastWidget).NT$setFocus(true);
                this.tabLastSelectedWidget = tabLastWidget;
            }
            else if (tabFirstWidget != null)
            {
                scrollSelection = tabFirstRow;
                ((AbstractWidgetAccessor) tabFirstWidget).NT$setFocus(true);
                this.tabLastSelectedWidget = tabFirstWidget;
            }
        }
        else
        {
            ((AbstractWidgetAccessor) this.tabLastSelectedWidget).NT$setFocus(false);

            if (Screen.hasShiftDown() && tabPreviousWidget != null)
            {
                scrollSelection = tabPreviousRow;
                ((AbstractWidgetAccessor) tabPreviousWidget).NT$setFocus(true);
                this.tabLastSelectedWidget = tabPreviousWidget;
            }
            else if (Screen.hasShiftDown() && tabLastWidget != null)
            {
                scrollSelection = tabLastRow;
                ((AbstractWidgetAccessor) tabLastWidget).NT$setFocus(true);
                this.tabLastSelectedWidget = tabLastWidget;
            }
            else if (tabNextWidget != null)
            {
                scrollSelection = tabNextRow;
                ((AbstractWidgetAccessor) tabNextWidget).NT$setFocus(true);
                this.tabLastSelectedWidget = tabNextWidget;
            }
            else if (tabFirstWidget != null)
            {
                scrollSelection = tabFirstRow;
                ((AbstractWidgetAccessor) tabFirstWidget).NT$setFocus(true);
                this.tabLastSelectedWidget = tabFirstWidget;
            }
        }

        // Focus scrollbar on selected row
        if (scrollSelection != null)
            this.setScrollOn(scrollSelection);

        return true;
    }

    /*
       Selection List Overrides

       The following methods assist override methods or are override methods for vanilla's container object selection
       list class.
     */

    /**
     * Centers the scrollbar on the given row entry.
     * @param entry A row entry instance.
     */
    @Override
    protected void centerScrollOn(R entry) { super.centerScrollOn(entry); }

    /**
     * @return Provides the row list's current width.
     */
    @Override
    public int getRowWidth() { return this.width; }

    /**
     * @return Provides the width of each row minus the scrollbar position.
     */
    public int getWidthMinusScrollbar() { return this.getScrollbarPosition(); }

    /**
     * Check if the given ending x-position goes past the list's scrollbar.
     * @param endX An ending x-position.
     * @return Whether the x-position is at or past the list's scrollbar position.
     */
    public boolean isTooLong(int endX) { return endX >= this.getScrollbarPosition(); }

    /**
     * This override changes the position of the vanilla scrollbar position.
     * @return An x-position that makes the scrollbar flush with the right side of the row list.
     */
    @Override
    protected int getScrollbarPosition() { return this.width - 4; }

    /**
     * Handler method for when the mouse clicks on an abstract row list.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the mouse click event.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        for (R row : this.children())
        {
            for (GuiEventListener listener : row.children())
            {
                if (listener instanceof EditBox box)
                {
                    if (!box.mouseClicked(mouseX, mouseY, button))
                        box.setFocused(false);
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Helper method that gets the y-position of the very bottom of a row.
     * @param index The index of a row entry within the row list.
     * @return The y-position that is flush with the bottom of a row entry.
     */
    protected int getRowBottom(int index) { return this.getRowTop(index) + this.itemHeight; }

    /**
     * Helper method for rendering the row list.
     * @param graphics The current GuiGraphics object.
     * @param topY The top of the row list.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param partialTick The change in frame time.
     */
    private void renderList(GuiGraphics graphics, int topY, int mouseX, int mouseY, float partialTick)
    {
        int itemCount = this.getItemCount();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        for (int i = 0; i < itemCount; i++)
        {
            AbstractSelectionList.Entry<?> entry = this.getEntry(i);
            int top = this.getRowTop(i);
            int bottom = this.getRowBottom(i);

            if (bottom < this.y0 || top > this.y1)
            {
                boolean isTextWidget = false;

                if (entry instanceof ConfigRowList.Row)
                {
                    for (AbstractWidget widget : ((ConfigRowList.Row) entry).children)
                    {
                        if (widget instanceof TextGroup.TextRow)
                        {
                            if (((TextGroup.TextRow) widget).isFirst())
                                isTextWidget = true;
                        }
                    }
                }

                if (!isTextWidget)
                    continue;
            }

            int left;
            int startY = topY + i * this.itemHeight + this.headerHeight;
            int height = this.itemHeight - 4;
            int width = this.getRowWidth();

            if (((AbstractSelectionListAccessor) this).NT$getRenderSelection() && this.isSelectedItem(i))
            {
                left = this.x0 + this.width / 2 - width / 2;
                int startX = this.x0 + this.width / 2 + width / 2;
                float brightness = this.isFocused() ? 1.0F : 0.5F;

                RenderSystem.setShader(GameRenderer::getPositionShader);
                RenderSystem.setShaderColor(brightness, brightness, brightness, 1.0F);

                builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                builder.vertex(left, startY + height + 2, 0.0).endVertex();
                builder.vertex(startX, startY + height + 2, 0.0).endVertex();
                builder.vertex(startX, startY - 2, 0.0).endVertex();
                builder.vertex(left, startY - 2, 0.0).endVertex();
                tesselator.end();

                RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
                builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                builder.vertex(left + 1, startY + height + 1, 0.0).endVertex();
                builder.vertex(startX - 1, startY + height + 1, 0.0).endVertex();
                builder.vertex(startX - 1, startY - 1, 0.0).endVertex();
                builder.vertex(left + 1, startY - 1, 0.0).endVertex();
                tesselator.end();
            }

            left = this.getRowLeft();

            entry.render(graphics, i, top, left, width, height, mouseX, mouseY, Objects.equals(this.getHovered(), entry), partialTick);
        }
    }

    /**
     * Handler method for rendering an abstract row list.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param partialTick The change in frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.isTransparentList)
        {
            if (this.minecraft.level == null && this.minecraft.screen != null)
                this.minecraft.screen.renderDirtBackground(graphics);
        }

        // Initialize container rows so that any extra rows generated by the container are added before rendering
        for (R row : this.children())
        {
            for (GuiEventListener listener : row.children())
            {
                if (listener instanceof ContainerButton container)
                    container.init();
            }
        }

        int scrollbarPosition = this.getScrollbarPosition();
        int scrollbarPositionOffset = scrollbarPosition + 6;
        int startY = this.y0 + 4 - (int) this.getScrollAmount();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        if (this.isTransparentList && this.minecraft.level == null)
            graphics.fillGradient(this.x0, this.y0, this.x1, this.y1, 0x68000000, 0x68000000);

        this.renderList(graphics, startY, mouseX, mouseY, partialTick);

        if (this.isTransparentList)
        {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, Screen.BACKGROUND_LOCATION);
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(519);

            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            builder.vertex(this.x0, this.y0, -100.0D).uv(0.0F, (float) this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
            builder.vertex((this.x0 + this.width), this.y0, -100.0D).uv((float) this.width / 32.0F, (float) this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
            builder.vertex((this.x0 + this.width), 0.0D, -100.0D).uv((float) this.width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
            builder.vertex(this.x0, 0.0D, -100.0D).uv(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
            builder.vertex(this.x0, this.height, -100.0D).uv(0.0F, (float) this.height / 32.0F).color(64, 64, 64, 255).endVertex();
            builder.vertex((this.x0 + this.width), this.height, -100.0D).uv((float) this.width / 32.0F, (float) this.height / 32.0F).color(64, 64, 64, 255).endVertex();
            builder.vertex((this.x0 + this.width), this.y1, -100.0D).uv((float) this.width / 32.0F, (float) this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
            builder.vertex(this.x0, this.y1, -100.0D).uv(0.0F, (float) this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
            tesselator.end();

            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            builder.vertex(this.x0, (this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
            builder.vertex(this.x1, (this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
            builder.vertex(this.x1, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(this.x0, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(this.x0, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(this.x1, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(this.x1, (this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
            builder.vertex(this.x0, (this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
            tesselator.end();
        }

        int maxScroll = this.getMaxScroll();

        if (maxScroll > 0)
        {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            int heightOffset = (int) ((float) ((this.y1 - this.y0) * (this.y1 - this.y0)) / (float) this.getMaxPosition());
            heightOffset = Mth.clamp(heightOffset, 32, (this.y1 - this.y0 - 8));
            int scrollOffset = (int) this.getScrollAmount() * (this.y1 - this.y0 - heightOffset) / maxScroll + this.y0;

            if (scrollOffset < this.y0)
                scrollOffset = this.y0;

            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            builder.vertex(scrollbarPosition, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(scrollbarPositionOffset, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(scrollbarPositionOffset, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(scrollbarPosition, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(scrollbarPosition, (scrollOffset + heightOffset), 0.0D).color(128, 128, 128, 255).endVertex();
            builder.vertex(scrollbarPositionOffset, (scrollOffset + heightOffset), 0.0D).color(128, 128, 128, 255).endVertex();
            builder.vertex(scrollbarPositionOffset, scrollOffset, 0.0D).color(128, 128, 128, 255).endVertex();
            builder.vertex(scrollbarPosition, scrollOffset, 0.0D).color(128, 128, 128, 255).endVertex();
            builder.vertex(scrollbarPosition, (scrollOffset + heightOffset - 1), 0.0D).color(192, 192, 192, 255).endVertex();
            builder.vertex((scrollbarPositionOffset - 1), (scrollOffset + heightOffset - 1), 0.0D).color(192, 192, 192, 255).endVertex();
            builder.vertex((scrollbarPositionOffset - 1), scrollOffset, 0.0D).color(192, 192, 192, 255).endVertex();
            builder.vertex(scrollbarPosition, scrollOffset, 0.0D).color(192, 192, 192, 255).endVertex();
            tesselator.end();
        }

        this.renderDecorations(graphics, mouseX, mouseY);
        RenderSystem.disableBlend();
    }
}