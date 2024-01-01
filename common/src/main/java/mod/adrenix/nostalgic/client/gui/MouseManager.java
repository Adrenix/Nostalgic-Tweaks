package mod.adrenix.nostalgic.client.gui;

public interface MouseManager
{
    /**
     * Set the mouse position during a render pass.
     *
     * @param mouseX The x-coordinate of the mouse cursor.
     * @param mouseY The y-coordinate of the mouse cursor.
     */
    void setMousePosition(int mouseX, int mouseY);

    /**
     * @return The x-coordinate of the mouse set stored by this manager.
     */
    int getMouseX();

    /**
     * @return The y-coordinate of the mouse set stored by this manager.
     */
    int getMouseY();
}
