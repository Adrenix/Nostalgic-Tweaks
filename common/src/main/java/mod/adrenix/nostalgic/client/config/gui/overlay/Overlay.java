package mod.adrenix.nostalgic.client.config.gui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.util.common.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

/**
 * Overlays are not screens nor widgets, instead they render on top of screens and use widgets within the overlay.
 * All overlay instances will be instances of GUI components and will implement overlay event methods.
 */

public abstract class Overlay implements OverlayEvents
{
    /* Static Fields */

    /**
     * This field tracks the overlay that is currently visible. Only one overlay can be visible at a time.
     */
    @Nullable
    private static Overlay visible = null;

    /**
     * Getter for retrieving the current visible overlay. This will return <code>null</code> if no overlay is currently
     * visible.
     *
     * @return An overlay instance.
     */
    @CheckReturnValue
    public static Overlay getVisible() { return Overlay.visible; }

    /*
       Graphical User Interface Helpers

       These static methods are used to help other config screen utilities that change behavior if an overlay
       instance is active.
     */

    /**
     * Close the currently opened overlay.
     */
    public static void close()
    {
        if (Overlay.visible != null)
            Overlay.visible.onClose();
    }

    /**
     * Checks if an overlay session is active.
     * @return Whether an overlay instance is currently visible to the user.
     */
    public static boolean isOpened() { return Overlay.visible != null; }

    /**
     * Checks if the mouse is over the active overlay window title bar.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @return Whether the mouse is over an overlay title bar.
     */
    public static boolean isOverTitle(double mouseX, double mouseY)
    {
        if (Overlay.visible != null)
            return Overlay.visible.isMouseOverTitle(mouseX, mouseY);

        return false;
    }

    /*
       Event Management

       These static methods are handled by the config screen event overrides. Since these overlays will only appear
       when a config screen instance is active, they only need to be handled by a config screen instance.
     */

    /**
     * Sends a resize event to the current overlay session.
     */
    public static void resize()
    {
        if (Overlay.visible != null)
            Overlay.visible.onResize();
    }

    /**
     * Sense a mouse release event to the current overlay session.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The button that was held during dragging.
     */
    public static void onRelease(double mouseX, double mouseY, int button)
    {
        if (Overlay.visible != null)
            Overlay.visible.onMouseReleased(mouseX, mouseY, button);
    }

    /**
     * Sends a key press event to the current overlay session.
     * This method also handles escape requests.
     * @param keyCode The pressed key code.
     * @param scanCode A key scancode.
     * @param modifiers Any held modifiers.
     * @return Whether the active overlay session handled the event.
     */
    public static boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (Overlay.visible != null && !Overlay.visible.locked && keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            Overlay.visible.onClose();
            return true;
        }

        return Overlay.visible != null && Overlay.visible.onKeyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Sends a mouse click event to the current overlay session.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The clicked mouse button.
     * @return Whether the active overlay session handled the event.
     */
    public static boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return Overlay.visible != null && Overlay.visible.onClick(mouseX, mouseY, button);
    }

    /**
     * Sends a mouse drag event to the current overlay session.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The clicked mouse button.
     * @param dragX The new dragged x-position of the mouse.
     * @param dragY The new dragged y-position of the mouse.
     * @return Whether the active overlay session handled the event.
     */
    public static boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        return Overlay.visible != null && Overlay.visible.onDrag(mouseX, mouseY, button, dragX, dragY);
    }

    /**
     * Sends a mouse scroll event to the current overlay session.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param delta The change in scroll direction.
     * @return Whether the active overlay session handled the event.
     */
    public static boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        return Overlay.visible != null && Overlay.visible.onMouseScrolled(mouseX, mouseY, delta);
    }

    /**
     * Sends a render event to the current overlay session.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick A change in game frame time.
     */
    public static void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (Overlay.visible != null)
            Overlay.visible.onRender(graphics, mouseX, mouseY, partialTick);
    }

    /*
       Utility Methods

       These methods provide helper methods for sound and text drawing.

       Since text shadows are broken in overlay windows, overlays need to use a special string rendering override for
       when text needs displayed.
     */

    /**
     * Play a widget click sound.
     */
    protected static void playClickSound()
    {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    /**
     * A custom draw string method is needed for overlays since text shadows will collide due to shadow rendering being
     * on the same z-translation.
     * @param component A component that will be drawn to the screen.
     * @param x The starting x-position of text rendering.
     * @param y The starting y-position of text rendering.
     * @param color The default base color for text rendering.
     */
    protected void drawString(GuiGraphics graphics, Component component, int x, int y, int color)
    {
        PoseStack poseStack = new PoseStack();

        poseStack.pushPose();
        poseStack.last().pose().translate(new Vector3f(0.0F, 0.0F, Z_OFFSET + 1.0F));

        graphics.drawString(Minecraft.getInstance().font, component, x, y, color, true);

        poseStack.popPose();
    }

    /**
     * Override method {@link Overlay#drawString(GuiGraphics, Component, int, int, int)} that uses white (0xFFFFFF) as the base color.
     * @param component A component that will be drawn to the screen.
     * @param x The starting x-position of text rendering.
     * @param y The starting y-position of text rendering.
     */
    protected void drawString(GuiGraphics graphics, Component component, int x, int y) { drawString(graphics, component, x, y, 0xFFFFFF); }

    /*
       Overlay Extension & Interface Overrides

       This part of the class defines overlay fields and default overlay event overrides.
       All overlays will be singleton instances since only one overlay can be visible at a time.
     */

    /* Overlay Fields */

    /**
     * A list of widgets to be rendered and handled.
     */
    protected final ArrayList<AbstractWidget> widgets = new ArrayList<>();

    /**
     * The current active screen when this overlay is created.
     */
    protected final Screen screen = Minecraft.getInstance().screen;

    /**
     * Checks if the overlay session is visible to the user.
     */
    protected boolean isOverlayOpen = false;

    /**
     * This is necessary to prevent immediate event handling once an overlay is opened.
     */
    protected boolean isJustOpened = false;

    /**
     * This field tracks whether the mouse is over the default overlay close button.
     */
    protected boolean isOverClose = false;

    /**
     * Tracks the overlay starting x-position.
     * This must be a double so mouse dragging provides fluid window movement.
     */
    protected double x;

    /**
     * Tracks the overlay starting y-position.
     * This must be a double so mouse dragging provides fluid window movement.
     */
    protected double y;

    /**
     * The overlay window widget.
     * This must be defined by the singleton instance.
     */
    protected int width;

    /**
     * The overlay window height.
     * This must be defined by the singleton instance.
     */
    protected int height;

    /* Flags */

    /**
     * When this flag is active, the overlay cannot be closed using normal means.
     * There must be a button somewhere on the overlay that closes the overlay when clicked.
     */
    protected boolean locked = false;

    /**
     * When this flag is active, the overlay will display a tooltip when the mouse is over a hint button.
     * Rendering and mouse checks must be done within the overlay.
     */
    protected boolean hint = false;

    /* Rendering Constants */

    public static final float Z_OFFSET = 400.0F;
    protected static final int LEFT_CLICK = GLFW.GLFW_MOUSE_BUTTON_LEFT;
    protected static final int CLOSE_WIDTH = 9;
    protected static final int CLOSE_HEIGHT = 9;

    /**
     * The default overlay constructor.
     * @param width The width of this overlay.
     * @param height The height of this overlay.
     */
    @SuppressWarnings("SwitchStatementWithTooFewBranches") // This will be expanded when more flags are made
    protected Overlay(int width, int height, OverlayFlag ...flags)
    {
        this.width = width;
        this.height = height;

        if (Overlay.visible != null && Overlay.visible.isOpen())
            Overlay.visible.onClose();

        Overlay.visible = this;
        Overlay.visible.isOverlayOpen = true;
        Overlay.visible.isJustOpened = true;

        for (OverlayFlag flag : flags)
        {
            switch (flag)
            {
                case LOCKED -> this.locked = true;
            }
        }
    }

    /**
     * Checks if the mouse is over the overlay window title bar.
     * This may be overridden by a singleton instance if additional logic is needed.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @return Whether the mouse is over an overlay title bar.
     */
    protected boolean isMouseOverTitle(double mouseX, double mouseY)
    {
        return MathUtil.isWithinBox(mouseX, mouseY, this.x, this.y, this.width, 15);
    }

    /**
     * Generates widgets that will be tracked for rendering and event handling.
     * All overlays must override this, there will be no default widgets.
     */
    @Override
    public void generateWidgets() {}

    /**
     * Provides logic that will be performed when the game window is resized.
     * All overlays must override this, there will be no default resizing logic.
     */
    @Override
    public void onResize() { this.init(); }

    /**
     * Provides logic that will be performed when the overlay is closed by the user.
     * Overlays may override this. The default behavior is to set the overlay open field to false.
     */
    @Override
    public void onClose()
    {
        this.widgets.clear();
        this.isOverlayOpen = false;

        Overlay.visible = null;
    }

    /**
     * Provides logic that will be performed when the caller is checking if an overlay instance is visible to the user.
     * Overlays may override this. The default behavior is to return the overlay open field flag.
     * @return Whether the current overlay is visible to the user.
     */
    @Override
    public boolean isOpen() { return this.isOverlayOpen; }

    /**
     * Provides logic that will be performed when the mouse is scrolled.
     * Overlays may override this. The default behavior is to send mouse scroll events to widgets that the mouse is over.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param delta The change in scroll direction.
     * @return Whether this overlay handled the mouse scroll event.
     */
    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double delta)
    {
        // Don't process widgets if this overlay closed
        if (!this.isOverlayOpen)
            return false;

        this.widgets.forEach((widget) ->
        {
            if (MathUtil.isWithinBox(mouseX, mouseY, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight()))
                widget.mouseScrolled(mouseX, mouseY, delta);
        });

        return true;
    }

    /**
     * Provides logic that will be performed when the mouse is dragging an overlay window.
     * Overlays may override this. The default behavior is to send mouse drag events to widgets that the mouse is over.
     *
     * If the mouse is over the overlay title bar, then the default behavior is to drag the entire overlay window so
     * that it follows the new mouse drag position.
     *
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @param dragX The new dragged x-position of the mouse.
     * @param dragY The new dragged y-position of the mouse.
     * @return Whether this overlay handled the mouse drag event.
     */
    @Override
    public boolean onDrag(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        // Don't process widgets if this overlay closed
        if (!this.isOverlayOpen)
            return false;

        this.widgets.forEach((widget) ->
        {
            if (MathUtil.isWithinBox(mouseX, mouseY, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight()))
                widget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        });

        if (button == LEFT_CLICK && this.isMouseOverTitle(mouseX, mouseY))
        {
            this.x += dragX;
            this.y += dragY;
            this.generateWidgets();

            return true;
        }

        return false;
    }

    /**
     * Provides logic that will be performed when the mouse is clicked.
     * Overlays may override this. The default behavior is to send mouse click events to <b>all</b> widgets.
     *
     * There is <b>no default behavior</b> defined for <b>non-left-click</b> events.
     * Other default behavior for left-click events is defined below.
     *
     * <ul>
     *   <li>If the mouse is clicked outside the overlay window, then the overlay will close.</li>
     *   <li>If the mouse is clicked on the overlay title bar, then the widget click sound will play.</li>
     *   <li>If the mouse is clicked on the overlay close button, then the overlay will close.</li>
     * </ul>
     *
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this overlay handled the mouse click event.
     */
    @Override
    public boolean onClick(double mouseX, double mouseY, int button)
    {
        // Don't process widgets if this overlay closed
        if (!this.isOverlayOpen)
            return false;

        // If a click action clears the widget list, then the for loop must stop
        for (AbstractWidget widget : this.widgets)
        {
            widget.mouseClicked(mouseX, mouseY, button);

            if (this.widgets.size() == 0)
                break;
        }

        if (button != LEFT_CLICK)
            return false;

        boolean isOutOfBounds = !MathUtil.isWithinBox(mouseX, mouseY, this.x, this.y, this.width, this.height);
        boolean isClosing = isOutOfBounds || this.isOverClose;

        if (isClosing && !this.locked)
            this.onClose();

        if (this.isMouseOverTitle(mouseX, mouseY) && !(this.locked && isClosing))
            Overlay.playClickSound();

        return false;
    }

    /**
     * Provides logic that will be performed when a key is pressed while an overlay is open.
     * Overlays may override this. The default behavior is to send key press events to <b>all</b> widgets.
     * @param keyCode The key code that was pressed.
     * @param scanCode A key scancode.
     * @param modifiers Any held modifiers.
     * @return Whether this overlay handled the key press event.
     */
    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers)
    {
        // Don't process widgets if this overlay closed
        if (!this.isOverlayOpen)
            return false;

        this.widgets.forEach((widget) -> widget.keyPressed(keyCode, scanCode, modifiers));

        return false;
    }

    /**
     * Provides logic that will be performed when the mouse is released after a click event.
     * Overlays may override this. The default behavior is to send mouse release events to widgets that the mouse is over.
     *
     * No mouse release events are sent when the overlay is first opened. This prevents release events from activating
     * overlay widgets that appear above the mouse position where the release happened.
     *
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     */
    @Override
    public void onMouseReleased(double mouseX, double mouseY, int button)
    {
        // Don't send the mouse release event to overlays if the window just opened
        if (this.isJustOpened)
        {
            this.isJustOpened = false;
            return;
        }

        // Don't render widgets if this overlay closed
        if (!this.isOverlayOpen)
            return;

        // Check if mouse dragging the window moved it's title bar off the screen.
        double lastX = this.x;
        double lastY = this.y;

        if (this.x + this.width <= 0.0D)
            this.x = 0.0D;

        if (this.x > Minecraft.getInstance().getWindow().getGuiScaledWidth() - 5.0D)
            this.x = Minecraft.getInstance().getWindow().getGuiScaledWidth() - 25.0D;

        if (this.y < 0.0D)
            this.y = 0.0D;

        if (this.y > Minecraft.getInstance().getWindow().getGuiScaledHeight())
            this.y = Minecraft.getInstance().getWindow().getGuiScaledHeight() - 15.0D;

        if (lastY != this.y || lastX != this.x)
            this.generateWidgets();

        // Send mouse release to widgets the mouse is over
        this.widgets.forEach((widget) ->
        {
            if (MathUtil.isWithinBox(mouseX, mouseY, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight()))
                widget.mouseReleased(mouseX, mouseY, button);
        });
    }
}
