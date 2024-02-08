package mod.adrenix.nostalgic.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.MouseManager;
import mod.adrenix.nostalgic.client.gui.tooltip.TooltipManager;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetHolder;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class EnhancedScreen<T extends EnhancedScreen<T, W>, W extends WidgetManager> extends Screen
    implements WidgetHolder, ParentHolder, MouseManager, TooltipManager
{
    /* Fields */

    @Nullable protected final Screen parentScreen;
    protected final Minecraft minecraft;
    private final Function<T, W> widgetManager;
    private final UniqueArrayList<DynamicWidget<?, ?>> widgets;
    private int mouseX;
    private int mouseY;

    /* Constructor */

    public EnhancedScreen(Function<T, W> widgetManager, @Nullable Screen parentScreen, Component title)
    {
        super(title);

        this.minecraft = Minecraft.getInstance();
        this.widgets = new UniqueArrayList<>();
        this.widgetManager = widgetManager;
        this.parentScreen = parentScreen;
        this.mouseX = -1;
        this.mouseY = -1;
    }

    /* Abstraction */

    /**
     * A pointer to the extending screen instance is required so that this abstract enhanced screen can update widgets
     * properly. Below is a simple example of what an override self-method would look like.
     *
     * <pre>
     * &#64;Override
     * public T self()
     * {
     *     return this;
     * }
     * </pre>
     * Where {@code T} is replaced with the class extending this {@link EnhancedScreen}.
     *
     * @return A pointer to {@code this}.
     */
    protected abstract T self();

    /**
     * @return The current {@link W WidgetManager} instance for this screen.
     */
    public abstract W getWidgetManager();

    /**
     * Setter for the widget manager field in an enhanced screen.
     *
     * @param widgetManager A new {@link W WidgetManager} instance.
     */
    public abstract void setWidgetManager(W widgetManager);

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Screen getParentScreen()
    {
        return this.parentScreen;
    }

    /**
     * Replaces the current widgets on the screen with fresh instances.
     */
    @Override
    protected void init()
    {
        this.setWidgetManager(this.widgetManager.apply(this.self()));
        this.getWidgetManager().init();
    }

    /**
     * Ticks widgets that need ticked in the widget manager.
     */
    @Override
    public void tick()
    {
        this.getWidgetManager().tick();
        this.widgets.forEach(DynamicWidget::tick);
    }

    /**
     * Handler method for when the game window is resized.
     *
     * @param minecraft A singleton Minecraft instance.
     * @param width     The new game window width.
     * @param height    The new game window height.
     */
    @Override
    public void resize(Minecraft minecraft, int width, int height)
    {
        super.resize(minecraft, width, height);
    }

    /**
     * Handler method for when a key is pressed.
     *
     * @param keyCode   The key code that was pressed.
     * @param scanCode  A key scan code.
     * @param modifiers Key code modifiers.
     * @return Whether this method handled the key that was pressed.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (this.getFocused() != null && this.getFocused().keyPressed(keyCode, scanCode, modifiers))
            return true;

        if (KeyboardUtil.isEsc(keyCode) && this.shouldCloseOnEsc())
        {
            this.onFinish();
            return true;
        }

        if (Screen.hasShiftDown() && Screen.hasControlDown() && keyCode == InputConstants.KEY_D)
        {
            NostalgicTweaks.LOGGER.setDebug();
            return true;
        }

        if (Screen.hasShiftDown() && Screen.hasControlDown() && keyCode == InputConstants.KEY_T)
        {
            this.minecraft.reloadResourcePacks();
            return true;
        }

        if (Screen.hasShiftDown() && Screen.hasControlDown() && keyCode == InputConstants.KEY_F)
        {
            GuiUtil.toggleShowFps();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Handler method for when a key is released after being pressed.
     *
     * @param keyCode   The key code that was pressed.
     * @param scanCode  A key scancode.
     * @param modifiers Any held modifiers.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    /**
     * Handler method for when a char is typed.
     *
     * @param codePoint The char that was typed.
     * @param modifiers Any held modifiers.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        return super.charTyped(codePoint, modifiers);
    }

    /**
     * Handler method for when the mouse is moved within the screen.
     *
     * @param mouseX The x-position of the mouse.
     * @param mouseY the y-position of the mouse.
     */
    @Override
    public void mouseMoved(double mouseX, double mouseY)
    {
        super.mouseMoved(mouseX, mouseY);
    }

    /**
     * Handler method for when the mouse is clicked.
     *
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the mouse being clicked.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        NostalgicTweaks.LOGGER.debug(String.format("mouseX: %s | mouseY: %s", mouseX, mouseY));

        boolean isWidgetClicked = false;

        for (DynamicWidget<?, ?> widget : this.widgets)
        {
            if (widget.mouseClicked(mouseX, mouseY, button))
            {
                this.widgets.stream().filter(DynamicWidget::isFocused).forEach(DynamicWidget::setUnfocused);
                widget.setClickFocus();

                this.setFocused(widget);

                if (button == 0)
                    this.setDragging(true);

                isWidgetClicked = true;
                break;
            }
        }

        if (isWidgetClicked)
            return true;

        this.widgets.stream().filter(DynamicWidget::isFocused).forEach(DynamicWidget::setUnfocused);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Handler method for when the mouse is released.
     *
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (this.widgets.stream().anyMatch(widget -> widget.mouseReleased(mouseX, mouseY, button)))
            return true;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * Handler method for when the mouse scrolls.
     *
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param deltaX Vertical scroll amount.
     * @param deltaY Horizontal scroll amount.
     * @return Whether this method handled the mouse scroll.
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY)
    {
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    /**
     * Handler method for when the mouse drags on the screen.
     *
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @param dragX  The new dragged offset x-position from the mouse.
     * @param dragY  The new dragged offset y-position from the mouse.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (this.widgets.stream().anyMatch(widget -> widget.mouseDragged(mouseX, mouseY, button, dragX, dragY)))
            return true;

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    /**
     * Focus the first eligible widget.
     */
    @PublicAPI
    public void focusFirst()
    {
        this.widgets.stream().filter(DynamicWidget::canFocus).findFirst().ifPresent(this::setFocused);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocused(@Nullable GuiEventListener focused)
    {
        if (focused instanceof DynamicWidget<?, ?> dynamic)
        {
            if (!dynamic.canFocus())
                return;
        }

        this.widgets.stream().filter(DynamicWidget::isFocused).forEach(DynamicWidget::setUnfocused);

        super.setFocused(focused);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueArrayList<? extends GuiEventListener> children()
    {
        return this.widgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueArrayList<DynamicWidget<?, ?>> getTooltipWidgets()
    {
        return this.widgets;
    }

    /**
     * @return A {@link UniqueArrayList} of all {@link DynamicWidget} subscribed to this screen.
     */
    @Override
    public UniqueArrayList<DynamicWidget<?, ?>> getWidgets()
    {
        return this.widgets;
    }

    /**
     * Add a widget to this screen and set its parent screen to this.
     */
    @Override
    public void addWidget(DynamicWidget<?, ?> widget)
    {
        this.widgets.add(widget);
        widget.setScreen(this);
    }

    /**
     * Clears all screen widgets.
     */
    @Override
    protected void clearWidgets()
    {
        this.widgets.clear();
        super.clearWidgets();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMousePosition(int mouseX, int mouseY)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMouseX()
    {
        return this.mouseX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMouseY()
    {
        return this.mouseY;
    }

    /**
     * Handler method for rendering elements to the screen.
     *
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        DynamicWidget.render(this.widgets, graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Instructions to perform before this screen is closed.
     */
    protected void onFinish()
    {
        this.onClose();
    }

    /**
     * Handler method for when this screen is closed.
     */
    @Override
    public void onClose()
    {
        this.minecraft.setScreen(this.parentScreen);
    }
}
