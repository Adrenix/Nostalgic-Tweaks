package mod.adrenix.nostalgic.client.config.gui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import mod.adrenix.nostalgic.util.common.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Overlays are not screens nor widgets, instead they render on top of screens and use widgets within the overlay.
 * All overlay instances will be instances of GUI components and will implement overlay event methods.
 */

public abstract class Overlay extends GuiComponent implements OverlayEvents
{
    /*
       Singleton Registration

       These static methods are used to register new singleton instance of an overlay.
       Typically, registration is performed within a static block within an extended overlay class.

       See the color picker or category list overlay windows for examples of registration.
     */

    /**
     * This set keeps track of all singleton overlay instances.
     * There will not be multiple instances of overlays since only one overlay can be visible at a time.
     */
    private static final Set<Overlay> OVERLAYS = new HashSet<>();

    /**
     * Register an overlay singleton.
     * @param overlay An overlay instance.
     */
    public static void register(Overlay overlay) { OVERLAYS.add(overlay); }

    /*
       Graphical User Interface Helpers

       These static methods are used to help other config screen utilities that change behavior if an overlay
       instance is active.
     */

    /**
     * Get the overlay that is visible to the user.
     * @return An overlay singleton instance if that overlay is opened.
     */
    @Nullable
    private static Overlay getOverlay()
    {
        for (Object obj : OVERLAYS.toArray())
        {
            if (obj instanceof Overlay overlay && overlay.isOpen())
                return overlay;
        }

        return null;
    }

    /**
     * Start a new overlay session.
     * @param starting The overlay instance that is starting a new session.
     */
    protected static void start(Overlay starting)
    {
        Overlay visible = getOverlay();

        if (visible != null && visible.isOpen())
            visible.onClose();

        starting.isOverlayOpen = true;
    }

    /**
     * Checks if an overlay session is active.
     * @return Whether an overlay instance is currently visible to the user.
     */
    public static boolean isOpened() { return getOverlay() != null; }

    /**
     * Checks if the mouse is over the active overlay window title bar.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @return Whether the mouse is over an overlay title bar.
     */
    public static boolean isOverTitle(double mouseX, double mouseY)
    {
        Overlay overlay = getOverlay();

        if (overlay != null)
            return overlay.isMouseOverTitle(mouseX, mouseY);

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
        Overlay overlay = getOverlay();

        if (overlay != null)
            overlay.onResize();
    }

    /**
     * Sense a mouse release event to the current overlay session.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The button that was held during dragging.
     */
    public static void onRelease(double mouseX, double mouseY, int button)
    {
        Overlay overlay = getOverlay();

        if (overlay != null)
            overlay.onMouseReleased(mouseX, mouseY, button);
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
        Overlay overlay = getOverlay();

        if (overlay != null && keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            overlay.onClose();
            return true;
        }

        return overlay != null && overlay.onKeyPressed(keyCode, scanCode, modifiers);
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
        Overlay overlay = getOverlay();
        return overlay != null && overlay.onClick(mouseX, mouseY, button);
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
        Overlay overlay = getOverlay();
        return overlay != null && overlay.onDrag(mouseX, mouseY, button, dragX, dragY);
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
        Overlay overlay = getOverlay();
        return overlay != null && overlay.onMouseScrolled(mouseX, mouseY, delta);
    }

    /**
     * Sends a render event to the current overlay session.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick A change in game frame time.
     */
    public static void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        Overlay overlay = getOverlay();

        if (overlay != null)
            overlay.onRender(poseStack, mouseX, mouseY, partialTick);
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
    protected static void drawString(Component component, int x, int y, int color)
    {
        PoseStack overlay = new PoseStack();
        overlay.last().pose().translate(new Vector3f(0.0F, 0.0F, 0.03F));
        drawString(overlay, Minecraft.getInstance().font, component, x, y, color);
    }

    /**
     * Override method {@link Overlay#drawString(Component, int, int, int)} that uses white (0xFFFFFF) as the base color.
     * @param component A component that will be drawn to the screen.
     * @param x The starting x-position of text rendering.
     * @param y The starting y-position of text rendering.
     */
    protected static void drawString(Component component, int x, int y) { drawString(component, x, y, 0xFFFFFF); }

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

    /* Rendering Constants */

    protected static final int LEFT_CLICK = GLFW.GLFW_MOUSE_BUTTON_LEFT;
    protected static final int CLOSE_WIDTH = 9;
    protected static final int CLOSE_HEIGHT = 9;

    /**
     * The default overlay constructor.
     * @param width The width of this overlay.
     * @param height The height of this overlay.
     */
    protected Overlay(int width, int height)
    {
        this.width = width;
        this.height = height;
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
        return ModUtil.Numbers.isWithinBox(mouseX, mouseY, this.x, this.y, this.width, 15);
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
    public void onResize() {}

    /**
     * Provides logic that will be performed when the overlay is closed by the user.
     * Overlays may override this. The default behavior is to set the overlay open field to false.
     */
    @Override
    public void onClose() { this.isOverlayOpen = false; }

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
        this.widgets.forEach((widget) ->
        {
            if (ModUtil.Numbers.isWithinBox(mouseX, mouseY, widget.x, widget.y, widget.getWidth(), widget.getHeight()))
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
        this.widgets.forEach((widget) ->
        {
            if (ModUtil.Numbers.isWithinBox(mouseX, mouseY, widget.x, widget.y, widget.getWidth(), widget.getHeight()))
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
        this.widgets.forEach((widget) -> widget.mouseClicked(mouseX, mouseY, button));

        if (button != LEFT_CLICK)
            return false;

        boolean isOutOfBounds = !ModUtil.Numbers.isWithinBox(mouseX, mouseY, this.x, this.y, this.width, this.height);

        if (isOutOfBounds || this.isOverClose)
            this.onClose();

        if (this.isMouseOverTitle(mouseX, mouseY))
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

        // Send mouse release to widgets the mouse is over
        this.widgets.forEach((widget) ->
        {
            if (ModUtil.Numbers.isWithinBox(mouseX, mouseY, widget.x, widget.y, widget.getWidth(), widget.getHeight()))
                widget.mouseReleased(mouseX, mouseY, button);
        });
    }
}
