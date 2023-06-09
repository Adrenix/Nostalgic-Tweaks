package mod.adrenix.nostalgic.client.config.gui.overlay;

import mod.adrenix.nostalgic.client.config.gui.overlay.template.AbstractWidgetProvider;
import mod.adrenix.nostalgic.client.config.gui.overlay.template.ListScreenOverlay;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.list.ListScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ItemButton;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

/**
 * This class provides options after an item button is clicked in an abstract list screen. The window will display the
 * item button that was clicked at the top and provide a list of options for managing an item within the user's custom
 * list.
 */

public class ManageItemOverlay extends ListScreenOverlay<ManageItemOverlay.WidgetProvider>
{
    /* Static Fields */

    public static final int OVERLAY_WIDTH = 155;
    public static final int OVERLAY_HEIGHT = 118;

    /* Overlay Fields */

    /**
     * This is the item stack that will be associated with this manager. When the add, edit, or remove button is pressed
     * it uses this field to determine which list entry to interact with.
     */
    private final ItemStack itemStack;

    /* Constructor & Initialize */

    /**
     * Start a new manage item overlay window instance.
     * @param itemStack The item stack instance associated with this manager instance.
     */
    public ManageItemOverlay(ItemStack itemStack)
    {
        super(Component.translatable(LangUtil.Gui.OVERLAY_ITEM), OVERLAY_WIDTH, OVERLAY_HEIGHT);

        this.itemStack = itemStack;

        this.setBackground(0xA0000000);
        this.setHeightPadding(38);
        this.init();
    }

    /* List Overlay Overrides */

    @Override
    protected void createWidgetProvider() { this.widgetProvider = new WidgetProvider(); }

    /* Overlay Methods */

    /**
     * Gets the centered x-axis position for the overlay's item button.
     * @return A centered horizontal position for the item button widget.
     */
    public int getCenteredX() { return (int) (this.x + (this.width / 2)) - 11; }

    /**
     * Get the item stack associated with this manager.
     * @return An item stack instance.
     */
    public ItemStack getItemStack() { return this.itemStack; }

    /* Widget Provider */

    /**
     * This class is responsible for creating and adding widgets to the overlay.
     * An item button will appear at the top of the window along with additional buttons for management below.
     */
    protected class WidgetProvider extends AbstractWidgetProvider
    {
        /* Widgets */

        public ItemButton itemButton;
        public Button addButton;
        public Button editButton;
        public Button removeButton;

        /* Methods */

        /**
         * Create and add widgets to the item manager overlay window.
         */
        public void generate()
        {
            this.itemButton = this.createItemButton();
            this.addButton = this.createAddButton();
            this.editButton = this.createEditButton();
            this.removeButton = this.createRemoveButton();

            ManageItemOverlay.this.widgets.add(this.itemButton);
            ManageItemOverlay.this.widgets.add(this.addButton);
            ManageItemOverlay.this.widgets.add(this.editButton);
            ManageItemOverlay.this.widgets.add(this.removeButton);

            this.children = Set.of(itemButton, addButton, editButton, removeButton);
        }

        /**
         * Calculates the minimum width allocated for buttons within this overlay.
         * @return A width size for a button.
         */
        public int getButtonWidth() { return ManageItemOverlay.this.getDrawWidth() + 12; }

        /**
         * This item button displays the currently selected item at the top of the overlay.
         * @return A new item button instance.
         */
        public ItemButton createItemButton()
        {
            ListScreen screen = ManageItemOverlay.this.listScreen;
            ItemStack itemStack = ManageItemOverlay.this.itemStack;
            int startX = ManageItemOverlay.this.getCenteredX();

            return new ItemButton(screen, itemStack, startX).forOverlay();
        }

        /**
         * Functional shortcut for creating a new item entry instance.
         * @param button A button instance.
         */
        private void onCreate(Button button)
        {
            Overlay.close();
            ManageItemOverlay.this.listScreen.addItem(ManageItemOverlay.this.itemStack);
            ManageItemOverlay.this.listScreen.highlightItem(ManageItemOverlay.this.itemStack);
            ManageItemOverlay.this.listScreen.getSearchBox().setValue("");
            ManageItemOverlay.this.listScreen.refreshSearchResults();
        }

        /**
         * This button will add a button to the item list.
         * @return A button instance.
         */
        public Button createAddButton()
        {
            return Button.builder(Component.translatable(LangUtil.Gui.OVERLAY_ITEM_ADD), this::onCreate)
                .pos(ManageItemOverlay.this.getOverlayStartX(), ManageItemOverlay.this.getOverlayStartY() + 22)
                .size(this.getButtonWidth(), SettingsScreen.BUTTON_HEIGHT)
                .build()
            ;
        }

        /**
         * Functional shortcut when the edit button is pressed.
         * @param button A button instance.
         */
        private void onEdit(Button button)
        {
            Overlay.close();
            ManageItemOverlay.this.listScreen.jumpToEntry(ManageItemOverlay.this.itemStack);
        }

        /**
         * This button will jump to the row in the saved entry list that is associated with the item stack.
         * If there is no saved entry associated with this entry stack then this button should not be active.
         *
         * @return A button instance.
         */
        public Button createEditButton()
        {
            return Button.builder(Component.translatable(LangUtil.Gui.OVERLAY_ITEM_EDIT), this::onEdit)
                .pos(ManageItemOverlay.this.getOverlayStartX(), ManageItemOverlay.this.getOverlayStartY() + 44)
                .size(this.getButtonWidth(), SettingsScreen.BUTTON_HEIGHT)
                .build()
            ;
        }

        /**
         * Functional shortcut for when an item entry is removed.
         * @param button A button instance.
         */
        private void onRemove(Button button)
        {
            Overlay.close();
            ManageItemOverlay.this.listScreen.deleteItem(ManageItemOverlay.this.itemStack);
            ManageItemOverlay.this.listScreen.jumpToEntry(ManageItemOverlay.this.itemStack);
        }

        /**
         * This button will mark the selected item stack for deletion. If there is no saved entry associated with this
         * entry stack then this button should not be active.
         *
         * @return A button instance.
         */
        public Button createRemoveButton()
        {
            return Button.builder(Component.translatable(LangUtil.Gui.OVERLAY_ITEM_REMOVE), this::onRemove)
                .pos(ManageItemOverlay.this.getOverlayStartX(), ManageItemOverlay.this.getOverlayStartY() + 66)
                .size(this.getButtonWidth(), SettingsScreen.BUTTON_HEIGHT)
                .build()
            ;
        }
    }

    /* Overlay Overrides */

    /**
     * Handler method for overlay main rendering.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick A change in game frame time.
     */
    @Override
    public void onMainRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        // Widget management
        boolean isSaved = this.listScreen.isItemSaved(this.itemStack);

        this.widgetProvider.itemButton.setX(this.getCenteredX());
        this.widgetProvider.editButton.active = isSaved;
        this.widgetProvider.removeButton.active = isSaved;
        this.widgetProvider.addButton.active = !this.listScreen.isItemSaved(this.itemStack);

        if (this.listScreen.isItemDeleted(this.itemStack))
            this.widgetProvider.removeButton.active = false;

        super.onMainRender(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Handler method for overlay post rendering.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void onPostRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        // Render item button tooltip
        if (this.widgetProvider.itemButton.isMouseOver(mouseX, mouseY))
            this.widgetProvider.itemButton.renderToolTip(graphics, mouseX, mouseY);
    }
}
