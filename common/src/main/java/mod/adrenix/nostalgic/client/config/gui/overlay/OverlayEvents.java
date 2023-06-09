package mod.adrenix.nostalgic.client.config.gui.overlay;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Each overlay may choose to handle these events and perform extra logic on top of the abstract overlay class.
 * These events are only emitted to overlays that have an active session visible to the user.
 */

public interface OverlayEvents
{
    /**
     * Initialize the overlay window. Typically, this method is used to define overlay fields based on current screen
     * properties. When the game window resizes, this method is called since the game's window properties has changed.
     */
    void init();

    /**
     * Setup widgets for a new overlay session.
     */
    void generateWidgets();

    /**
     * Handler method for when the game window is resized.
     */
    void onResize();

    /**
     * Handler method for when the overlay session is closed by the user.
     */
    void onClose();

    /**
     * Handler method that informs the caller of whether an overlay session is active.
     * @return Whether this overlay should be considered open and visible to the user.
     */
    boolean isOpen();

    /**
     * Handler method for when the overlay window is being dragged by the mouse.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @param dragX The new dragged x-position of the mouse.
     * @param dragY The new dragged y-position of the mouse.
     * @return Whether the override handler handled this event.
     */
    boolean onDrag(double mouseX, double mouseY, int button, double dragX, double dragY);

    /**
     * Handler method for when the mouse is clicked inside an overlay window.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether the override handler handled this event.
     */
    boolean onClick(double mouseX, double mouseY, int button);

    /**
     * Handler method for when a key is pressed.
     * @param keyCode The key code that was pressed.
     * @param scanCode A key scancode.
     * @param modifiers Any held modifiers.
     * @return Whether the override handler handled this event.
     */
    boolean onKeyPressed(int keyCode, int scanCode, int modifiers);

    /**
     * Handler method for when the mouse is scrolled.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param delta The change in scroll direction.
     * @return Whether the override handler handled this event.
     */
    boolean onMouseScrolled(double mouseX, double mouseY, double delta);

    /**
     * Handler method for when the mouse is released after dragging.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     */
    void onMouseReleased(double mouseX, double mouseY, int button);

    /**
     * Handler method for when the overlay is rendered.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    void onRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
}
