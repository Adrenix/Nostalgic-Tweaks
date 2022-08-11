package mod.adrenix.nostalgic.client.config.gui.widget.list;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.client.config.gui.widget.group.TextGroup;
import mod.adrenix.nostalgic.mixin.widen.IMixinAbstractSelectionList;
import mod.adrenix.nostalgic.mixin.widen.IMixinAbstractWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class AbstractRowList<R extends ElementListWidget.Entry<R>> extends ElementListWidget<R>
{
    private boolean isTransparentList = true;
    private final int originalItemHeight;

    public AbstractRowList(MinecraftClient minecraft, int width, int height, int y0, int y1, int itemHeight)
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

    private ClickableWidget tabLastSelectedWidget = null;

    public ClickableWidget getLastSelection() { return this.tabLastSelectedWidget; }

    public void setLastSelection(ClickableWidget widget) { this.tabLastSelectedWidget = widget; }

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
            for (Element listener : row.children())
            {
                if (listener instanceof ClickableWidget widget)
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

    public boolean getFocus(@Nullable BiFunction<R, ClickableWidget, Boolean> filterWidget)
    {
        R scrollSelection = null;
        R tabFirstRow = null;
        R tabLastRow = null;
        R tabPreviousRow = null;
        R tabNextRow = null;

        ClickableWidget tabFirstWidget = null;
        ClickableWidget tabLastWidget = null;
        ClickableWidget tabPreviousWidget = null;
        ClickableWidget tabNextWidget = null;

        // Find the first eligible selectable widget
        for (R row : this.children())
        {
            for (Element listener : row.children())
            {
                if (listener instanceof ClickableWidget widget)
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
                Element listener = row.children().get(j);

                if (listener instanceof ClickableWidget widget)
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
                for (Element listener : row.children())
                {
                    if (listener instanceof ClickableWidget widget)
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
    protected int getScrollbarPositionX() { return this.width - 4; }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        for (R row : this.children())
        {
            for (Element listener : row.children())
            {
                if (listener instanceof TextFieldWidget box)
                    if (!box.mouseClicked(mouseX, mouseY, button))
                        box.setTextFieldFocused(false);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (this.isTransparentList)
        {
            if (this.client.world == null && this.client.currentScreen != null)
                this.client.currentScreen.renderBackgroundTexture(0);
            else
                this.fillGradient(poseStack, 0, 0, this.width, this.height, -1072689136, -804253680);
        }

        int scrollbarPosition = this.getScrollbarPositionX();
        int scrollbarPositionOffset = scrollbarPosition + 6;
        Tessellator tesselator = Tessellator.getInstance();
        BufferBuilder builder = tesselator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        int startX = this.getRowLeft();
        int startY = this.top + 4 - (int) this.getScrollAmount();

        if (this.isTransparentList)
            this.fillGradient(poseStack, this.left, this.top, this.right, this.bottom, 0x68000000, 0x68000000);
        this.renderList(poseStack, startX, startY, mouseX, mouseY, partialTick);

        if (this.isTransparentList)
        {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(519);

            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            builder.vertex(this.left, this.top, -100.0D).texture(0.0F, (float) this.top / 32.0F).color(64, 64, 64, 255).next();
            builder.vertex((this.left + this.width), this.top, -100.0D).texture((float) this.width / 32.0F, (float) this.top / 32.0F).color(64, 64, 64, 255).next();
            builder.vertex((this.left + this.width), 0.0D, -100.0D).texture((float) this.width / 32.0F, 0.0F).color(64, 64, 64, 255).next();
            builder.vertex(this.left, 0.0D, -100.0D).texture(0.0F, 0.0F).color(64, 64, 64, 255).next();
            builder.vertex(this.left, this.height, -100.0D).texture(0.0F, (float) this.height / 32.0F).color(64, 64, 64, 255).next();
            builder.vertex((this.left + this.width), this.height, -100.0D).texture((float) this.width / 32.0F, (float) this.height / 32.0F).color(64, 64, 64, 255).next();
            builder.vertex((this.left + this.width), this.bottom, -100.0D).texture((float) this.width / 32.0F, (float) this.bottom / 32.0F).color(64, 64, 64, 255).next();
            builder.vertex(this.left, this.bottom, -100.0D).texture(0.0F, (float) this.bottom / 32.0F).color(64, 64, 64, 255).next();
            tesselator.draw();

            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            builder.vertex(this.left, (this.top + 4), 0.0D).color(0, 0, 0, 0).next();
            builder.vertex(this.right, (this.top + 4), 0.0D).color(0, 0, 0, 0).next();
            builder.vertex(this.right, this.top, 0.0D).color(0, 0, 0, 255).next();
            builder.vertex(this.left, this.top, 0.0D).color(0, 0, 0, 255).next();
            builder.vertex(this.left, this.bottom, 0.0D).color(0, 0, 0, 255).next();
            builder.vertex(this.right, this.bottom, 0.0D).color(0, 0, 0, 255).next();
            builder.vertex(this.right, (this.bottom - 4), 0.0D).color(0, 0, 0, 0).next();
            builder.vertex(this.left, (this.bottom - 4), 0.0D).color(0, 0, 0, 0).next();
            tesselator.draw();
        }

        int maxScroll = this.getMaxScroll();
        if (maxScroll > 0)
        {
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            int heightOffset = (int) ((float) ((this.bottom - this.top) * (this.bottom - this.top)) / (float) this.getMaxPosition());
            heightOffset = MathHelper.clamp(heightOffset, 32, (this.bottom - this.top - 8));
            int scrollOffset = (int) this.getScrollAmount() * (this.bottom - this.top - heightOffset) / maxScroll + this.top;

            if (scrollOffset < this.top)
                scrollOffset = this.top;

            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            builder.vertex(scrollbarPosition, this.bottom, 0.0D).color(0, 0, 0, 255).next();
            builder.vertex(scrollbarPositionOffset, this.bottom, 0.0D).color(0, 0, 0, 255).next();
            builder.vertex(scrollbarPositionOffset, this.top, 0.0D).color(0, 0, 0, 255).next();
            builder.vertex(scrollbarPosition, this.top, 0.0D).color(0, 0, 0, 255).next();
            builder.vertex(scrollbarPosition, (scrollOffset + heightOffset), 0.0D).color(128, 128, 128, 255).next();
            builder.vertex(scrollbarPositionOffset, (scrollOffset + heightOffset), 0.0D).color(128, 128, 128, 255).next();
            builder.vertex(scrollbarPositionOffset, scrollOffset, 0.0D).color(128, 128, 128, 255).next();
            builder.vertex(scrollbarPosition, scrollOffset, 0.0D).color(128, 128, 128, 255).next();
            builder.vertex(scrollbarPosition, (scrollOffset + heightOffset - 1), 0.0D).color(192, 192, 192, 255).next();
            builder.vertex((scrollbarPositionOffset - 1), (scrollOffset + heightOffset - 1), 0.0D).color(192, 192, 192, 255).next();
            builder.vertex((scrollbarPositionOffset - 1), scrollOffset, 0.0D).color(192, 192, 192, 255).next();
            builder.vertex(scrollbarPosition, scrollOffset, 0.0D).color(192, 192, 192, 255).next();
            tesselator.draw();
        }

        this.renderDecorations(poseStack, mouseX, mouseY);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private int getRowBottom(int index)
    {
        return this.getRowTop(index) + this.itemHeight;
    }

    protected void renderList(MatrixStack poseStack, int x, int y, int mouseX, int mouseY, float partialTick)
    {
        int itemCount = this.getEntryCount();
        Tessellator tesselator = Tessellator.getInstance();
        BufferBuilder builder = tesselator.getBuffer();

        for (int i = 0; i < itemCount; i++)
        {
            EntryListWidget.Entry<?> entry = this.getEntry(i);
            int top = this.getRowTop(i);
            int bottom = this.getRowBottom(i);
            if (bottom < this.top || top > this.bottom)
            {
                boolean isTextWidget = false;
                if (entry instanceof ConfigRowList.Row)
                {
                    for (ClickableWidget widget : ((ConfigRowList.Row) entry).children)
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

            if (((IMixinAbstractSelectionList) this).NT$getRenderSelection() && this.isSelectedEntry(i))
            {
                left = this.left + this.width / 2 - width / 2;
                int startX = this.left + this.width / 2 + width / 2;
                float brightness = this.isFocused() ? 1.0F : 0.5F;

                RenderSystem.disableTexture();
                RenderSystem.setShader(GameRenderer::getPositionShader);
                RenderSystem.setShaderColor(brightness, brightness, brightness, 1.0F);

                builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
                builder.vertex(left, startY + height + 2, 0.0).next();
                builder.vertex(startX, startY + height + 2, 0.0).next();
                builder.vertex(startX, startY - 2, 0.0).next();
                builder.vertex(left, startY - 2, 0.0).next();
                tesselator.draw();

                RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
                builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
                builder.vertex(left + 1, startY + height + 1, 0.0).next();
                builder.vertex(startX - 1, startY + height + 1, 0.0).next();
                builder.vertex(startX - 1, startY - 1, 0.0).next();
                builder.vertex(left + 1, startY - 1, 0.0).next();
                tesselator.draw();

                RenderSystem.enableTexture();
            }

            left = this.getRowLeft();
            entry.render(poseStack, i, top, left, width, height, mouseX, mouseY, Objects.equals(this.getHoveredEntry(), entry), partialTick);
        }
    }
}