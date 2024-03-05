package mod.adrenix.nostalgic.util.client.gui;

import com.google.common.util.concurrent.AtomicDouble;
import mod.adrenix.nostalgic.tweak.enums.Corner;
import net.minecraft.client.gui.GuiGraphics;

/**
 * This utility manages and keeps track of where the overlay text should be rendered on the HUD. Use this utility when
 * drawing the overlay text to the screen.
 */
public class CornerManager
{
    private final float height = (float) GuiUtil.getGuiHeight();
    private final AtomicDouble topLeft = new AtomicDouble(2.0D);
    private final AtomicDouble topRight = new AtomicDouble(2.0D);
    private final AtomicDouble bottomLeft = new AtomicDouble(this.height - 10.0D);
    private final AtomicDouble bottomRight = new AtomicDouble(this.height - 10.0D);

    /**
     * Get where the next line for the given corner should be drawn and then add 10 units to the corner offset.
     *
     * @param corner The {@link Corner} context.
     * @return Where the next line for the given corner should be drawn.
     */
    public double getAndAdd(Corner corner)
    {
        return switch (corner)
        {
            case TOP_LEFT -> this.topLeft.getAndAdd(10.0D);
            case TOP_RIGHT -> this.topRight.getAndAdd(10.0D);
            case BOTTOM_LEFT -> this.bottomLeft.getAndAdd(-10.0D);
            case BOTTOM_RIGHT -> this.bottomRight.getAndAdd(-10.0D);
        };
    }

    /**
     * Calculates where text rendering from the right side of the screen should start.
     *
     * @param text The text to get a right side offset for.
     * @return The starting point of where the given text should render.
     */
    public int getRightOffset(String text)
    {
        return GuiUtil.getGuiWidth() - GuiUtil.font().width(text) - 2;
    }

    /**
     * Draws the given text to the screen using the given text and corner.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @param text     The text to draw.
     * @param corner   The {@link Corner} to draw to.
     */
    public void drawText(GuiGraphics graphics, String text, Corner corner)
    {
        DrawText.begin(graphics, text)
            .pos(corner.isLeft() ? 2 : this.getRightOffset(text), (int) this.getAndAdd(corner))
            .draw();
    }
}
