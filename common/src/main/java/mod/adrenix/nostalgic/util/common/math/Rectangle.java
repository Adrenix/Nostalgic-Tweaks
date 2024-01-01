package mod.adrenix.nostalgic.util.common.math;

/**
 * A simple record that defines the x/y positions of a rectangular shape.
 *
 * @param startX The starting x-position of the rectangle.
 * @param startY The starting y-position of the rectangle.
 * @param endX   The ending x-position of the rectangle.
 * @param endY   The ending y-position of the rectangle.
 */
public record Rectangle(int startX, int startY, int endX, int endY)
{
    /**
     * @return The absolute width of this rectangle.
     */
    public int getWidth()
    {
        return Math.abs(this.endX - this.startX);
    }

    /**
     * @return The absolute height of this rectangle.
     */
    public int getHeight()
    {
        return Math.abs(this.endY - this.startY);
    }

    /**
     * Check if the given mouse point is over this rectangle.
     *
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @return Whether the given mouse point is over this rectangle.
     */
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return MathUtil.isWithinBox(mouseX, mouseY, this.startX, this.startY, this.getWidth(), this.getHeight());
    }
}
