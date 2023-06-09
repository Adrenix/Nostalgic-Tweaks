package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.slider.GenericSlider;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/**
 * The delete button is the last button to the right of all custom map list rows.
 *
 * This button is utilized only by the map list type.
 * When an entry is deleted, this button will turn into an undo button.
 */

public class DeleteButton extends Button
{
    /* Fields */

    private static final int START_X = 0;
    private static final int START_Y = 0;
    private static final Component DELETE_TITLE = Component.translatable(LangUtil.Gui.BUTTON_DELETE);
    private static final Component UNDO_TITLE = Component.translatable(LangUtil.Gui.BUTTON_UNDO);
    private final Supplier<Boolean> isDeleted;
    private final ResetButton resetButton;

    /* Constructor Helpers */

    /**
     * Get the maximum width of the button based on the two possible titles.
     * @return The width of the entire button which includes padding.
     */
    private static int getDeleteWidth()
    {
        Font font = Minecraft.getInstance().font;
        int deleteWidth = font.width(DeleteButton.DELETE_TITLE.getString());
        int undoWidth = font.width(DeleteButton.UNDO_TITLE.getString());

        return Math.max(deleteWidth, undoWidth) + 8;
    }

    /**
     * Get the title of the delete button based on whether the current list entry is deleted.
     * @param isDeleted A supplier that determines whether the entry is deleted.
     * @return A component title for the delete button.
     */
    private static Component getDeleteTitle(Supplier<Boolean> isDeleted)
    {
        return isDeleted.get() ? DeleteButton.UNDO_TITLE : DeleteButton.DELETE_TITLE;
    }

    /**
     * This on press handler.
     * @param isDeleted Whether the current entry is deleted.
     * @param onDelete Instructions to perform when the entry is deleted.
     * @param onUndo Instructions to perform when deletion is undone.
     */
    private static void onPress
    (
        Supplier<Boolean> isDeleted,
        Runnable onDelete,
        Runnable onUndo
    )
    {
        if (isDeleted.get())
            onUndo.run();
        else
            onDelete.run();
    }

    /* Constructor */

    /**
     * Create a new delete button instance.
     * @param resetButton The reset button that is the neighbor of this button.
     * @param isDeleted A boolean supplier that determines if the entry is deleted.
     * @param onDelete Instructions to perform when the entry is deleted.
     * @param onUndo Instructions to perform when deletion is undone.
     */
    public DeleteButton
    (
        ResetButton resetButton,
        Supplier<Boolean> isDeleted,
        Runnable onDelete,
        Runnable onUndo
    )
    {
        super
        (
            DeleteButton.START_X,
            DeleteButton.START_Y,
            DeleteButton.getDeleteWidth(),
            ConfigRowList.BUTTON_HEIGHT,
            DeleteButton.getDeleteTitle(isDeleted),
            (button) -> onPress(isDeleted, onDelete, onUndo),
            DEFAULT_NARRATION
        );

        this.resetButton = resetButton;
        this.isDeleted = isDeleted;

        this.updateX();
    }

    /* Methods */

    /**
     * Update the starting x-position based on the reset button position and standard row widget gap.
     */
    private void updateX()
    {
        if (this.getX() == DeleteButton.START_X)
            this.setX(this.resetButton.getX() + this.resetButton.getWidth() + ConfigRowList.ROW_WIDGET_GAP);

        int diffX = 0;

        if (ConfigRowList.getInstance().isTooLong(this.getX() + DeleteButton.getDeleteWidth()))
        {
            int endX = this.getX() + this.width - 1;
            int startX = endX;

            while (ConfigRowList.getInstance().isTooLong(endX))
                endX--;

            diffX = startX - endX + 4;
            this.setX(this.getX() - diffX);
        }

        if (diffX != 0)
        {
            this.resetButton.setX(this.resetButton.getX() - diffX);
            this.resetButton.getController().setX(this.resetButton.getController().getX() - diffX);
        }
    }

    /**
     * Handler method for delete button rendering.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        this.setMessage(DeleteButton.getDeleteTitle(this.isDeleted));
        this.updateX();

        this.active = !Overlay.isOpened();

        boolean isActive = !this.isDeleted.get();
        this.resetButton.active = isActive;
        this.resetButton.getController().active = isActive;

        if (this.resetButton.getController() instanceof GenericSlider slider)
            slider.updateMessage();

        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
