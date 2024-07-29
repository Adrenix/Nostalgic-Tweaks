package mod.adrenix.nostalgic.client.gui.widget.list;

import net.minecraft.client.gui.GuiGraphics;

/**
 * This is a functional interface for providing custom rendering instructions to perform when a row list widget is
 * rendered.
 */
public interface RowListRenderer
{
    /**
     * Performs this operation on the given arguments.
     *
     * @param rowList     The {@link RowList} being rendered.
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    void accept(RowList rowList, GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    /* Static */

    /**
     * Empty renderer function.
     */
    RowListRenderer EMPTY = (rowList, graphics, mouseX, mouseY, partialTick) -> { };
}
