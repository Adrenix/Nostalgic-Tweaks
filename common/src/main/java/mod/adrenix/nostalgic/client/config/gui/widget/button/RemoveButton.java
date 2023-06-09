package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.gui.screen.list.ListScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.util.common.ItemCommonUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/**
 * The remove button is the last button to the right of all custom set list rows.
 *
 * This button is utilized only by the set list type.
 * When an entry is removed, this button will turn into an undo button.
 */

public class RemoveButton extends Button
{
    /* Fields */

    private static final int START_X = 0;
    private static final int START_Y = 0;

    private static final Component DISABLE_TITLE = Component.translatable(LangUtil.Gui.BUTTON_DISABLE);
    private static final Component ENABLE_TITLE = Component.translatable(LangUtil.Gui.BUTTON_ENABLE);
    private static final Component REMOVE_TITLE = Component.translatable(LangUtil.Gui.BUTTON_REMOVE);
    private static final Component UNDO_TITLE = Component.translatable(LangUtil.Gui.BUTTON_UNDO);

    private final Supplier<Boolean> isRemoved;
    private final RemoveType removeType;
    private final String resourceKey;

    /* Constructor Helpers */

    /**
     * Get the maximum width of the button based on the two possible titles.
     * @return The width of the entire button which includes padding.
     */
    private static int getRemoveWidth()
    {
        Font font = Minecraft.getInstance().font;

        int disableWidth = font.width(RemoveButton.DISABLE_TITLE.getString());
        int enableWidth = font.width(RemoveButton.ENABLE_TITLE.getString());
        int removeWidth = font.width(RemoveButton.REMOVE_TITLE.getString());
        int undoWidth = font.width(RemoveButton.UNDO_TITLE.getString());

        return MathUtil.getLargest(disableWidth, enableWidth, removeWidth, undoWidth) + 8;
    }

    /**
     * Get the title of the remove button based on whether the current resource key is deleted.
     * @param isRemoved A supplier that determines whether the resource key is deleted.
     * @return A component title for the remove button.
     */
    private static Component getRemoveTitle(RemoveType removeType, Supplier<Boolean> isRemoved)
    {
        return switch (removeType)
        {
            case SAVED -> isRemoved.get() ? RemoveButton.UNDO_TITLE : RemoveButton.REMOVE_TITLE;
            case DEFAULT -> isRemoved.get() ? RemoveButton.ENABLE_TITLE : RemoveButton.DISABLE_TITLE;
        };
    }

    /**
     * The on press handler.
     * @param isRemoved Whether the current entry is removed.
     * @param onRemove Instructions to perform when the key is deleted.
     * @param onUndo Instructions to perform when removal is undone.
     */
    private static void onPress(Supplier<Boolean> isRemoved, Runnable onRemove, Runnable onUndo)
    {
        if (isRemoved.get())
            onUndo.run();
        else
            onRemove.run();
    }

    /* Constructor */

    /**
     * Create a new remove button instance.
     * @param isRemoved A boolean supplier that determines if the resource key is deleted.
     * @param onRemove Instructions to perform when the resource key is removed.
     * @param onUndo Instructions to perform when removal is undone.
     */
    public RemoveButton(RemoveType removeType, String resourceKey, Supplier<Boolean> isRemoved, Runnable onRemove, Runnable onUndo)
    {
        super
        (
            RemoveButton.START_X,
            RemoveButton.START_Y,
            RemoveButton.getRemoveWidth(),
            ConfigRowList.BUTTON_HEIGHT,
            RemoveButton.getRemoveTitle(removeType, isRemoved),
            (button) -> onPress(isRemoved, onRemove, onUndo),
            DEFAULT_NARRATION
        );

        this.resourceKey = resourceKey;
        this.removeType = removeType;
        this.isRemoved = isRemoved;
    }

    /* Methods */

    /**
     * Update the starting x-position based on whether the button position is overlapping the config row list scrollbar.
     */
    private void updateX()
    {
        if (this.getX() == RemoveButton.START_X)
            this.setX(ConfigRowList.getInstance().getRowWidth() + 1);

        if (ConfigRowList.getInstance().isTooLong(this.getX() + RemoveButton.getRemoveWidth()))
        {
            int endX = this.getX() + this.width - 1;
            int startX = endX;

            while (ConfigRowList.getInstance().isTooLong(endX))
                endX--;

            this.setX(this.getX() - (startX - endX + 4));
        }
    }

    /**
     * Handler method for remove button rendering.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        this.setMessage(RemoveButton.getRemoveTitle(this.removeType, this.isRemoved));
        this.updateX();

        if (this.removeType == RemoveType.DEFAULT && Minecraft.getInstance().screen instanceof ListScreen listScreen)
            this.active = !listScreen.isItemSaved(ItemCommonUtil.getItem(this.resourceKey));

        if (Overlay.isOpened())
            this.active = false;

        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
