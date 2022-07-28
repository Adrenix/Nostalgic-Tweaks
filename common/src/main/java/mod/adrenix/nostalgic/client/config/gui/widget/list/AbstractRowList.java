package mod.adrenix.nostalgic.client.config.gui.widget.list;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.client.config.gui.widget.group.TextGroup;
import mod.adrenix.nostalgic.mixin.widen.IMixinAbstractSelectionList;
import mod.adrenix.nostalgic.mixin.widen.IMixinAbstractWidget;
import net.minecraft.client.Minecraft;
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

public abstract class AbstractRowList<R extends ContainerObjectSelectionList.Entry<R>> extends ContainerObjectSelectionList<R>
{
    private boolean isTransparentList = true;
    private final int originalItemHeight;

    public AbstractRowList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight)
    {
        super(minecraft, width, height, y0, y1, itemHeight);
        this.centerListVertically = false;
        this.originalItemHeight = this.itemHeight;
    }

    public int getRowHeight() { return this.itemHeight; }
    public void resetRowHeight() { this.itemHeight = this.originalItemHeight; }
    public void setRowHeight(int height) { this.itemHeight = height; }
    public void setAsTransparentList() { this.isTransparentList = false; }
    public void setScrollOn(R entry) { this.centerScrollOn(entry); }

    /* Tab Key Utility */

    private AbstractWidget tabLastSelectedWidget = null;

    public AbstractWidget getLastSelection() { return this.tabLastSelectedWidget; }

    public void setLastSelection(AbstractWidget widget) { this.tabLastSelectedWidget = widget; }

    public void resetLastSelection() { this.tabLastSelectedWidget = null; }

    public boolean unsetFocus()
    {
        if (this.tabLastSelectedWidget != null)
        {
            ((IMixinAbstractWidget) this.tabLastSelectedWidget).NT$setFocus(false);
            this.tabLastSelectedWidget = null;
            return true;
        }

        return false;
    }

    public boolean getFocusKeyPress(int keyCode, int scanCode, int modifiers)
    {
        // This is required to avoid out of bounds exceptions in case a widget changes the list after being pressed
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

    public boolean getFocus(@Nullable BiFunction<R, AbstractWidget, Boolean> filterWidget)
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
                ((IMixinAbstractWidget) tabLastWidget).NT$setFocus(true);
                this.tabLastSelectedWidget = tabLastWidget;
            }
            else if (tabFirstWidget != null)
            {
                scrollSelection = tabFirstRow;
                ((IMixinAbstractWidget) tabFirstWidget).NT$setFocus(true);
                this.tabLastSelectedWidget = tabFirstWidget;
            }
        }
        else
        {
            ((IMixinAbstractWidget) this.tabLastSelectedWidget).NT$setFocus(false);

            if (Screen.hasShiftDown() && tabPreviousWidget != null)
            {
                scrollSelection = tabPreviousRow;
                ((IMixinAbstractWidget) tabPreviousWidget).NT$setFocus(true);
                this.tabLastSelectedWidget = tabPreviousWidget;
            }
            else if (Screen.hasShiftDown() && tabLastWidget != null)
            {
                scrollSelection = tabLastRow;
                ((IMixinAbstractWidget) tabLastWidget).NT$setFocus(true);
                this.tabLastSelectedWidget = tabLastWidget;
            }
            else if (tabNextWidget != null)
            {
                scrollSelection = tabNextRow;
                ((IMixinAbstractWidget) tabNextWidget).NT$setFocus(true);
                this.tabLastSelectedWidget = tabNextWidget;
            }
            else if (tabFirstWidget != null)
            {
                scrollSelection = tabFirstRow;
                ((IMixinAbstractWidget) tabFirstWidget).NT$setFocus(true);
                this.tabLastSelectedWidget = tabFirstWidget;
            }
        }

        // Focus scrollbar on selected row
        if (scrollSelection != null)
            this.setScrollOn(scrollSelection);

        return true;
    }

    /* Selection List Overrides */

    @Override
    protected void centerScrollOn(R entry) { super.centerScrollOn(entry); }

    @Override
    public int getRowWidth() { return this.width; }

    @Override
    protected int getScrollbarPosition() { return this.width - 4; }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        for (R row : this.children())
        {
            for (GuiEventListener listener : row.children())
            {
                if (listener instanceof EditBox box)
                    if (!box.mouseClicked(mouseX, mouseY, button))
                        box.setFocus(false);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (this.isTransparentList)
        {
            if (this.minecraft.level == null && this.minecraft.screen != null)
                this.minecraft.screen.renderDirtBackground(0);
            else
                this.fillGradient(poseStack, 0, 0, this.width, this.height, -1072689136, -804253680);
        }

        int scrollbarPosition = this.getScrollbarPosition();
        int scrollbarPositionOffset = scrollbarPosition + 6;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        int startX = this.getRowLeft();
        int startY = this.y0 + 4 - (int) this.getScrollAmount();

        if (this.isTransparentList)
            this.fillGradient(poseStack, this.x0, this.y0, this.x1, this.y1, 0x68000000, 0x68000000);
        this.renderList(poseStack, startX, startY, mouseX, mouseY, partialTick);

        if (this.isTransparentList)
        {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);
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
            RenderSystem.disableTexture();
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
            RenderSystem.disableTexture();
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

        this.renderDecorations(poseStack, mouseX, mouseY);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private int getRowBottom(int index)
    {
        return this.getRowTop(index) + this.itemHeight;
    }

    protected void renderList(PoseStack poseStack, int x, int y, int mouseX, int mouseY, float partialTick)
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
                            if (((TextGroup.TextRow) widget).isFirst())
                                isTextWidget = true;
                    }
                }

                if (!isTextWidget)
                    continue;
            }

            int left;
            int startY = y + i * this.itemHeight + this.headerHeight;
            int height = this.itemHeight - 4;
            int width = this.getRowWidth();

            if (((IMixinAbstractSelectionList) this).NT$getRenderSelection() && this.isSelectedItem(i))
            {
                left = this.x0 + this.width / 2 - width / 2;
                int startX = this.x0 + this.width / 2 + width / 2;
                float brightness = this.isFocused() ? 1.0F : 0.5F;

                RenderSystem.disableTexture();
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

                RenderSystem.enableTexture();
            }

            left = this.getRowLeft();
            entry.render(poseStack, i, top, left, width, height, mouseX, mouseY, Objects.equals(this.getHovered(), entry), partialTick);
        }
    }
}