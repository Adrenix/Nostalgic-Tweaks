package mod.adrenix.nostalgic.client.gui.widget.input;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.client.gui.tooltip.TooltipManager;
import mod.adrenix.nostalgic.client.gui.widget.blank.BlankWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.IconManager;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconTemplate;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.client.gui.DrawText;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.RecursionAvoidance;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public abstract class AbstractInput<Builder extends AbstractInputMaker<Builder, Input>, Input extends AbstractInput<Builder, Input>>
    extends DynamicWidget<Builder, Input> implements TooltipManager
{
    /* Fields */

    protected InputModule<Builder, Input> module;
    protected final UniqueArrayList<DynamicWidget<?, ?>> internal;
    protected final IconManager<Input> icon;
    protected final IconWidget controls;
    protected final BlankWidget printer;
    protected final Color borderColor;
    protected final Color backgroundColor;
    protected final RecursionAvoidance changingInput;
    protected boolean dragging;
    protected boolean editable;
    protected long focusedTime;
    protected int minCursorPos;
    protected int cursorPos;
    protected int displayPos;
    protected int highlightPos;
    protected String input;

    /* Constructor */

    protected AbstractInput(Builder builder)
    {
        super(builder);

        this.changingInput = RecursionAvoidance.create();
        this.internal = new UniqueArrayList<>();

        this.icon = new IconManager<>(this.self());
        this.icon.apply(this::setIconTooltip);
        this.icon.apply(this.internal::add);

        this.input = builder.startWith;
        this.editable = builder.editable;
        this.focusedTime = Util.getMillis();
        this.minCursorPos = 0;

        this.backgroundColor = new Color(() -> {
            Color background = this.isFocused() ? this.getBuilder().backgroundFocusColor : this.getBuilder().backgroundColor;

            if (this.isHoveredOrFocused())
            {
                if (this.getBuilder().hoverBackgroundColor != null)
                    background = this.getBuilder().hoverBackgroundColor;
                else
                    background = background.brighter();
            }

            return background.get();
        });

        this.borderColor = new Color(() -> {
            Color border = this.isFocused() ? this.getBuilder().borderFocusColor : this.getBuilder().borderColor;

            if (this.isHoveredOrFocused())
            {
                if (this.getBuilder().hoverBorderColor != null)
                    border = this.getBuilder().hoverBorderColor;
                else
                    border = border.brighter();
            }

            return border.get();
        });

        this.module = InputModule.generic(this.self());

        this.controls = IconTemplate.menu()
            .posX(() -> this.getEndX() - 12)
            .posY(() -> Math.round(MathUtil.center(this.getY(), 9, this.getHeight())))
            .onPress(() -> this.module.getOverlay().openOrClose())
            .disableIf(this::isControlDisabled)
            .build(this.internal::add);

        this.printer = BlankWidget.create()
            .posX(this::getIconEndX)
            .posY(() -> Math.round(MathUtil.center(this.getY(), GuiUtil.textHeight(), this.getHeight())))
            .height(GuiUtil.textHeight())
            .extendWidthTo(this.controls, 4)
            .renderer(this::renderText)
            .build(this.internal::add);

        this.getBuilder().addFunction(new ActiveSync<>());
        this.getBuilder().addFunction(new LayoutSync<>());
        this.getBuilder().addFunction(new IconSync<>());
        this.getBuilder().addFunction(new InputSync<>(this.self()));
    }

    /* Methods */

    /**
     * @return Whether the control button is disabled.
     */
    protected boolean isControlDisabled()
    {
        return this.isNotEditable() || this.getY() < 22;
    }

    /**
     * Clamps the icon size to this input widget's height.
     *
     * @param icon An {@link IconWidget} instance.
     */
    protected void setIconSize(IconWidget icon)
    {
        icon.setSize(Mth.clamp(icon.getHeight(), 0, this.getHeight() - 2));
    }

    /**
     * Sets tooltips for all input widget icons.
     *
     * @param icon An {@link IconWidget} instance.
     */
    protected void setIconTooltip(IconWidget icon)
    {
        MutableComponent tooltip = Lang.Input.TIP_CLICK.get();

        if (this.getBuilder().searchShortcut)
            tooltip.append(String.format(" %s", Lang.Input.TIP_SEARCH.getString()));

        icon.getBuilder().tooltip(Lang.Input.TIP, 500L, TimeUnit.MILLISECONDS).infoTooltip(tooltip, 45);
    }

    /**
     * @return The extra padding added around the edges of the current input widget icon.
     */
    protected int getIconPadding()
    {
        return this.icon.isEmpty() ? 0 : this.builder.iconPadding;
    }

    /**
     * @return Get width created by an icon using the icon's width and the set icon margin.
     */
    protected int getIconWidth()
    {
        if (this.icon.isEmpty())
            return 2;

        return this.icon.getWidth() + 2 + this.getIconPadding();
    }

    /**
     * @return The x-coordinate to use for the icon.
     */
    protected int getIconX()
    {
        return this.getX() + 2 + this.getIconPadding();
    }

    /**
     * @return The end x-coordinate of where the icon (if it is present) ends.
     */
    protected int getIconEndX()
    {
        return this.getIconX() + this.getIconWidth();
    }

    /**
     * @return The y-coordinate to use for the icon.
     */
    protected int getIconY()
    {
        return Math.round(MathUtil.center(this.getY(), this.icon.getHeight(), this.getHeight()));
    }

    /**
     * @return The maximum length allowed for the input text.
     */
    public int getMaxLength()
    {
        return this.getBuilder().maxLength;
    }

    /**
     * @return The width of the blank widget that renders the input text. This is adjusted for the font width of the
     * cursor character.
     */
    public int getPrinterWidth()
    {
        return this.printer.getWidth() - GuiUtil.font().width(this.getBuilder().cursor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueArrayList<DynamicWidget<?, ?>> getTooltipWidgets()
    {
        return this.internal;
    }

    /**
     * @return The current background {@link Color} for this widget.
     */
    @PublicAPI
    public Color getBackgroundColor()
    {
        return this.backgroundColor;
    }

    /**
     * @return The current border {@link Color} for this widget.
     */
    @PublicAPI
    public Color getBorderColor()
    {
        return this.borderColor;
    }

    /**
     * For the text to change in an input widget, it must be editable and focused.
     *
     * @return Whether the text in this input widget can be changed.
     */
    @PublicAPI
    public boolean isEditable()
    {
        return this.editable;
    }

    /**
     * @return Whether the text in this input widget cannot be changed.
     */
    @PublicAPI
    public boolean isNotEditable()
    {
        return !this.editable;
    }

    /**
     * Set whether this input widget is editable.
     *
     * @param isEditable The state to change to.
     */
    @PublicAPI
    public void setEditable(boolean isEditable)
    {
        this.editable = isEditable;
    }

    /**
     * @return The current cursor position.
     */
    @PublicAPI
    public int getCursorPosition()
    {
        return this.cursorPos;
    }

    /**
     * Sends changes on input to the builder's responder if it was defined.
     *
     * @param text The new text of this input widget.
     */
    protected void onInputChange(String text)
    {
        if (this.getBuilder().responder != null)
            this.getBuilder().responder.accept(this.self(), text);
    }

    /**
     * Set the text of the input widget. This will move the cursor to the end if the given text does not match the old
     * text.
     *
     * @param text The new text for this widget.
     */
    public void setInput(String text)
    {
        if (!this.getBuilder().filter.test(text))
            return;

        String lastInput = this.input;
        this.input = text.length() > this.getMaxLength() ? text.substring(0, this.getMaxLength()) : text;

        if (this.changingInput.isProcessing())
            return;

        if (!lastInput.equals(this.input))
        {
            this.changingInput.process(() -> this.onInputChange(text));
            this.moveCursorToEnd(false);
        }
    }

    /**
     * @return The current text set in this widget.
     */
    public String getInput()
    {
        return this.input;
    }

    /**
     * @return The text between the cursor and the end of the current highlight selection.
     */
    public String getHighlighted()
    {
        int min = Math.min(this.cursorPos, this.highlightPos);
        int max = Math.max(this.cursorPos, this.highlightPos);

        return this.input.substring(min, max);
    }

    /**
     * Adds the given text after the cursor, or replaces the currently selected text if there is one.
     *
     * @param text The text to insert.
     */
    public void insertText(String text)
    {
        int min = Math.min(this.cursorPos, this.highlightPos);
        int max = Math.max(this.cursorPos, this.highlightPos);
        int start = this.getMaxLength() - this.input.length() - (min - max);

        String filtered = SharedConstants.filterText(text);
        String insert = new StringBuilder(this.input).replace(min, max, filtered).toString();

        int end = filtered.length();

        if (start < end)
            end = start;

        if (!this.getBuilder().filter.test(insert))
            return;

        this.setInput(insert);
        this.setCursorPosition(min + end);
        this.setHighlightPos(this.cursorPos);
    }

    /**
     * Delete text in the given direction.
     *
     * @param direction Give {@code -1} to delete text to the left (backspace key) and {@code 1} to delete text to the
     *                  right (delete key).
     */
    protected void deleteText(int direction)
    {
        if (Screen.hasControlDown())
            this.deleteWords(direction);
        else
            this.deleteChars(direction);
    }

    /**
     * Delete words in the given direction.
     *
     * @param direction Give {@code -1} to delete text to the left (backspace key) and {@code 1} to delete text to the
     *                  right (delete key).
     */
    public void deleteWords(int direction)
    {
        if (this.input.isEmpty())
            return;

        if (this.highlightPos != this.cursorPos)
        {
            this.insertText("");
            return;
        }

        this.deleteChars(this.getWordPosition(direction) - this.cursorPos);
    }

    /**
     * Delete characters in the given direction.
     *
     * @param direction Give {@code -1} to delete text to the left (backspace key) and {@code 1} to delete text to the
     *                  right (delete key).
     */
    public void deleteChars(int direction)
    {
        if (this.input.isEmpty())
            return;

        if (this.highlightPos != this.cursorPos)
        {
            this.insertText("");
            return;
        }

        int directionPos = this.getCursorPos(direction);
        int minPos = Math.min(directionPos, this.cursorPos);
        int maxPos = Math.max(directionPos, this.cursorPos);

        if (minPos == maxPos)
            return;

        String deletion = new StringBuilder(this.input).delete(minPos, maxPos).toString();

        if (!this.getBuilder().filter.test(deletion))
            return;

        this.setInput(deletion);
        this.moveCursorTo(minPos, false);
    }

    /**
     * Get the starting index of the word at the specified number of words away from the cursor position.
     *
     * @param numberOfWords The number of words away to get a starting index.
     * @return The starting index of the desired word.
     */
    public int getWordPosition(int numberOfWords)
    {
        return this.getWordPosition(numberOfWords, this.cursorPos);
    }

    /**
     * Get the starting index of the word at the specified number of words away from the cursor position.
     *
     * @param numberOfWords The number of words away to get a starting index.
     * @param cursorPos     The cursor position.
     * @return The starting index of the desired word.
     */
    protected int getWordPosition(int numberOfWords, int cursorPos)
    {
        for (int i = 0; i < Math.abs(numberOfWords); ++i)
        {
            if (numberOfWords < 0)
            {
                while (cursorPos > 0 && this.input.charAt(cursorPos - 1) == ' ')
                    --cursorPos;

                while (cursorPos > 0 && this.input.charAt(cursorPos - 1) != ' ')
                    --cursorPos;

                continue;
            }

            int inputLength = this.input.length();
            cursorPos = this.input.indexOf(32, cursorPos);

            if (cursorPos == -1)
            {
                cursorPos = inputLength;
                continue;
            }

            while (cursorPos < inputLength && this.input.charAt(cursorPos) == ' ')
                ++cursorPos;
        }

        return cursorPos;
    }

    /**
     * Scroll the display position to the given position. This will set the input's {@code displayPos} which is the
     * current character index that is used as the start of the text to render.
     *
     * @param position The position to scroll to.
     */
    protected void scrollTo(int position)
    {
        this.displayPos = Math.min(this.displayPos, this.input.length());

        int printerWidth = this.getPrinterWidth();
        String displayString = GuiUtil.font().plainSubstrByWidth(this.input.substring(this.displayPos), printerWidth);
        int maxPos = displayString.length() + this.displayPos;

        if (position == this.displayPos)
            this.displayPos -= GuiUtil.font().plainSubstrByWidth(this.input, printerWidth, true).length();

        if (position > maxPos)
            this.displayPos += position - maxPos;
        else if (position <= this.displayPos)
            this.displayPos -= this.displayPos - position;

        this.displayPos = Mth.clamp(this.displayPos, 0, this.input.length());
    }

    /**
     * Set the cursor position.
     *
     * @param cursorPosition The new cursor position.
     */
    public void setCursorPosition(int cursorPosition)
    {
        this.cursorPos = Mth.clamp(cursorPosition, this.minCursorPos, this.input.length());
        this.scrollTo(this.cursorPos);
    }

    /**
     * Sets the position of the selection anchor (the selection anchor and the cursor position mark the edges of the
     * selection). If the anchor is set beyond the bounds of the current text, it will be put back inside.
     *
     * @param position The new highlight position.
     */
    public void setHighlightPos(int position)
    {
        this.highlightPos = Mth.clamp(position, this.minCursorPos, this.input.length());
        this.scrollTo(this.highlightPos);
    }

    /**
     * Get a cursor position from the given delta.
     *
     * @param delta Use {@code -1} to go left and {@code 1} to go right.
     * @return A cursor position that is offset by code points.
     */
    protected int getCursorPos(int delta)
    {
        return Util.offsetByCodepoints(this.input, this.cursorPos, delta);
    }

    /**
     * Move the cursor using the given delta.
     *
     * @param delta     Use {@code -1} to go left and {@code 1} to go right.
     * @param highlight Whether to highlight text to the new position.
     */
    public void moveCursor(int delta, boolean highlight)
    {
        this.moveCursorTo(this.getCursorPos(delta), highlight);
    }

    /**
     * Move the cursor to the given position.
     *
     * @param cursorPos The new cursor position.
     * @param highlight Whether to highlight text to the new position.
     */
    public void moveCursorTo(int cursorPos, boolean highlight)
    {
        this.setCursorPosition(cursorPos);

        if (!highlight)
            this.setHighlightPos(this.cursorPos);

        this.onInputChange(this.input);
    }

    /**
     * Move the cursor to the start of the input text.
     *
     * @param highlight Whether to highlight text to the new position.
     */
    public void moveCursorToStart(boolean highlight)
    {
        this.moveCursorTo(this.minCursorPos, highlight);
    }

    /**
     * Move the cursor to the end of the input text.
     *
     * @param highlight Whether to highlight text to the new position.
     */
    public void moveCursorToEnd(boolean highlight)
    {
        this.moveCursorTo(this.input.length(), highlight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocused(boolean focused)
    {
        super.setFocused(focused);

        if (focused)
            this.focusedTime = Util.getMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUnfocused()
    {
        return super.isUnfocused() || !this.isVisible() || !this.editable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent event)
    {
        if (this.isInvisible() || !this.editable)
            return null;

        return super.nextFocusPath(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.isInvalidClick(mouseX, mouseY, button))
            return false;

        this.dragging = true;

        if (this.controls.mouseClicked(mouseX, mouseY, button))
            return true;

        if (this.isUnfocused())
        {
            this.setFocused(true);
            return true;
        }

        if (this.icon.get().isMouseOver(mouseX, mouseY))
            this.setInput("");

        String text = GuiUtil.font().plainSubstrByWidth(this.input.substring(this.displayPos), this.getPrinterWidth());
        int maxWidth = Mth.floor(mouseX) - this.printer.getX();
        int cursorPos = GuiUtil.font().plainSubstrByWidth(text, maxWidth).length() + this.displayPos;

        this.moveCursorTo(cursorPos, Screen.hasShiftDown());
        this.setHighlightPos(this.cursorPos);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        this.dragging = false;

        return this.controls.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (!this.dragging)
            return false;

        int offset = Mth.floor(mouseX) - this.getX() - this.getIconWidth();
        String text = GuiUtil.font().plainSubstrByWidth(this.input.substring(this.displayPos), this.getPrinterWidth());

        this.setHighlightPos(GuiUtil.font().plainSubstrByWidth(text, offset).length() + this.displayPos);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (KeyboardUtil.isSearching(keyCode) && this.getBuilder().searchShortcut)
        {
            this.setFocused(true);
            this.moveCursorToEnd(false);

            if (this.getScreen() != null)
                this.getScreen().setFocused(this);

            return true;
        }

        if (this.isUnfocused())
            return false;

        if (KeyboardUtil.isEsc(keyCode))
        {
            this.setFocused(false);

            return true;
        }

        if (Screen.isSelectAll(keyCode))
        {
            this.moveCursorToEnd(false);
            this.setHighlightPos(0);

            return true;
        }

        if (Screen.isCopy(keyCode))
        {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());

            return true;
        }

        if (Screen.isPaste(keyCode))
        {
            if (this.editable)
                this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());

            return true;
        }

        if (Screen.isCut(keyCode))
        {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());

            if (this.editable)
                this.insertText("");

            return true;
        }

        return switch (keyCode)
        {
            case InputConstants.KEY_LEFT ->
            {
                if (this.cursorPos == this.minCursorPos)
                    yield false;

                if (Screen.hasControlDown())
                    this.moveCursorTo(this.getWordPosition(-1), Screen.hasShiftDown());
                else
                    this.moveCursor(-1, Screen.hasShiftDown());

                yield true;
            }

            case InputConstants.KEY_RIGHT ->
            {
                if (this.cursorPos == this.input.length())
                    yield false;

                if (Screen.hasControlDown())
                    this.moveCursorTo(this.getWordPosition(1), Screen.hasShiftDown());
                else
                    this.moveCursor(1, Screen.hasShiftDown());

                yield true;
            }

            case InputConstants.KEY_BACKSPACE ->
            {
                if (this.editable)
                    this.deleteText(-1);

                yield true;
            }

            case InputConstants.KEY_DELETE ->
            {
                if (this.editable)
                    this.deleteText(1);

                yield true;
            }

            case InputConstants.KEY_HOME ->
            {
                this.moveCursorToStart(Screen.hasShiftDown());
                yield true;
            }

            case InputConstants.KEY_END ->
            {
                this.moveCursorToEnd(Screen.hasShiftDown());
                yield true;
            }

            default -> false;
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        if (this.isUnfocused())
            return false;

        if (SharedConstants.isAllowedChatCharacter(codePoint))
        {
            if (this.editable)
                this.insertText(Character.toString(codePoint));

            return true;
        }

        return false;
    }

    /**
     * Render the input text of this input widget.
     *
     * @param printer     The {@link BlankWidget} text printer instance.
     * @param graphics    The current {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    protected void renderText(BlankWidget printer, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        int beginIndex = this.cursorPos - this.displayPos;
        Color color = this.isFocused() ? this.getBuilder().textColor : this.getBuilder().textUnfocusedColor;
        String text = GuiUtil.font().plainSubstrByWidth(this.input.substring(this.displayPos), this.getPrinterWidth());
        boolean isAllInputVisible = beginIndex >= 0 && beginIndex <= text.length();
        boolean isFlashing = this.isFocused() && (Util.getMillis() - this.focusedTime) / 300L % 2L == 0L && isAllInputVisible;
        int startX = printer.getX();
        int startY = printer.getY();
        int textEndX = startX;
        int highlightPos = Mth.clamp(this.highlightPos - this.displayPos, 0, text.length());

        if (text.isEmpty())
        {
            text = this.getBuilder().whenEmpty;
            color = this.getBuilder().textEmptyColor;
        }

        if (!text.isEmpty())
        {
            String displayText = isAllInputVisible ? text.substring(0, beginIndex) : text;

            textEndX = DrawText.begin(graphics, this.getBuilder().formatter.apply(displayText, this.displayPos))
                .pos(textEndX, startY)
                .color(color)
                .draw();
        }

        boolean isVerticalCursor = this.cursorPos < this.input.length() || this.input.length() >= this.getMaxLength();
        int verticalX = textEndX;

        if (!isAllInputVisible)
            verticalX = beginIndex > 0 ? startX + printer.getWidth() : startX;
        else if (isVerticalCursor)
        {
            --verticalX;
            --textEndX;
        }

        if (!text.isEmpty() && isAllInputVisible && beginIndex < text.length())
        {
            DrawText.begin(graphics, this.getBuilder().formatter.apply(text.substring(beginIndex), this.cursorPos))
                .pos(textEndX, startY)
                .color(color)
                .draw();
        }

        if (isFlashing)
        {
            if (isVerticalCursor)
            {
                int x0 = verticalX;
                int x1 = verticalX + 1;
                int y0 = startY - 1;
                int y1 = startY + 1 + GuiUtil.textHeight();
                Color cursorColor = this.getBuilder().cursorVerticalColor;

                RenderUtil.deferredRenderer(() -> {
                    RenderUtil.setFillZOffset(1);
                    RenderUtil.setRenderType(RenderType.guiOverlay());
                    RenderUtil.fill(graphics, x0, y0, x1, y1, cursorColor.get());
                });
            }
            else
            {
                DrawText.begin(graphics, this.getBuilder().cursor)
                    .pos(verticalX, startY)
                    .color(this.getBuilder().cursorColor)
                    .draw();
            }
        }

        if (highlightPos != beginIndex)
        {
            int endX = startX + GuiUtil.font().width(text.substring(0, highlightPos)) - 1;
            this.renderHighlight(graphics, verticalX, startY - 1, endX, startY + 1 + GuiUtil.textHeight());
        }
    }

    /**
     * Render highlighted text.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @param minX     The minimum x-coordinate.
     * @param minY     The minimum y-coordinate.
     * @param maxX     The maximum x-coordinate.
     * @param maxY     The maximum y-coordinate.
     */
    protected void renderHighlight(GuiGraphics graphics, int minX, int minY, int maxX, int maxY)
    {
        int delta;

        if (minX < maxX)
        {
            delta = minX;
            minX = maxX;
            maxX = delta;
        }

        if (minY < maxY)
        {
            delta = minY;
            minY = maxY;
            maxY = delta;
        }

        if (maxX > this.getX() + this.width)
            maxX = this.getX() + this.width;

        if (minX > this.getX() + this.width)
            minX = this.getX() + this.width;

        int x0 = minX;
        int y0 = minY;

        int x1 = maxX;
        int y1 = maxY;

        RenderUtil.deferredRenderer(() -> {
            RenderUtil.setFillZOffset(1);
            RenderUtil.setRenderType(RenderType.guiTextHighlight());
            RenderUtil.fill(graphics, x0, y0, x1, y1, 0xFF0000FF);
        });
    }

    /**
     * Render the border and background of this input widget.
     *
     * @param graphics A {@link GuiGraphics} instance.
     */
    protected void renderBox(GuiGraphics graphics)
    {
        Color border = this.getBorderColor();
        Color background = this.getBackgroundColor();

        int x0 = this.getX();
        int y0 = this.getY();
        int x1 = this.getEndX();
        int y1 = this.getEndY();

        RenderUtil.vLine(graphics, x0, y0, y1, border);
        RenderUtil.vLine(graphics, x1 - 1, y0, y1, border);
        RenderUtil.hLine(graphics, x0 + 1, y0, x1 - 1, border);
        RenderUtil.hLine(graphics, x0 + 1, y1 - 1, x1 - 1, border);
        RenderUtil.fill(graphics, x0 + 1, y0 + 1, x1 - 1, y1 - 1, background);
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

        RenderUtil.beginBatching();

        this.renderBox(graphics);

        this.icon.apply(this.internal::remove);
        this.icon.pushCache();
        this.icon.pos(this.getIconX(), this.getIconY());
        this.icon.render(graphics, mouseX, mouseY, partialTick);
        this.icon.popCache();

        DynamicWidget.render(this.internal, graphics, mouseX, mouseY, partialTick);
        RenderUtil.endBatching();

        this.icon.apply(this.internal::add);

        this.renderDebug(graphics);
    }
}
