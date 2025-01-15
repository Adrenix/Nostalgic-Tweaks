package mod.adrenix.nostalgic.client.gui.widget.blank;

import net.minecraft.client.gui.GuiGraphics;

/**
 * This is a functional interface for providing custom rendering instructions to perform when a blank widget is
 * rendered.
 */
public interface BlankRenderer
{
    /**
     * Performs this operation on the given arguments.
     *
     * @param widget      The {@link BlankWidget} being rendered.
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    void accept(BlankWidget widget, GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    /* Static */

    /**
     * Empty renderer function.
     */
    BlankRenderer EMPTY = (widget, graphics, mouseX, mouseY, partialTick) -> { };
}
