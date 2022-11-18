package mod.adrenix.nostalgic.client.config.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.client.config.CustomSwings;
import mod.adrenix.nostalgic.client.config.gui.ItemSuggestionHelper;
import mod.adrenix.nostalgic.client.config.gui.widget.list.SpeedRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.ToggleCheckbox;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

/**
 * This screen is accessed by a button that is near the top of the swing group tab.
 * All widgets for this screen is handled by {@link SpeedRowList}.
 */

public class SwingScreen extends SettingsScreen
{
    /* Widget Constants */

    protected static final int TOP_HEIGHT = 24;
    protected static final int BOTTOM_OFFSET = 32;
    protected static final int ITEM_HEIGHT = 25;
    protected static final int SEARCH_BOX_W = 226;
    protected static final int SEARCH_BOX_H = 18;
    protected static final int TOP_ROW_Y = 23;

    /* Swing Screen Fields */

    /**
     * The root of the client config cache contains a hash map that holds all custom swing speeds and the item they are
     * connected to.
     */
    private static final ClientConfig CLIENT_CONFIG = ClientConfigCache.getRoot();

    /* Checkbox Filters */

    private final Checkbox toolsCheckbox;
    private final Checkbox blocksCheckbox;
    private final Checkbox itemsCheckbox;
    private final Checkbox resetCheckbox;

    /* Screen Widgets */

    private Button resetButton;
    private Button addItemButton;
    private Button autofillButton;
    private EditBox searchBox;
    private ItemSuggestionHelper itemSuggestions;
    private SpeedRowList speedRowList;

    /* Uncategorized Fields */

    private final Screen parent;
    private final Map<String, Integer> undo;
    private final List<Widget> renderables = Lists.newArrayList();

    /* Constructor */

    /**
     * Create a new customized swing speed screen instance.
     * @param parent The parent screen to return to.
     */
    public SwingScreen(Screen parent)
    {
        super(parent, Component.translatable(LangUtil.Gui.SWING), false);

        this.parent = parent;
        this.undo = Maps.newHashMap(CLIENT_CONFIG.custom);

        int x = 2;
        int y = TOP_ROW_Y;

        int resetY = y - 1;
        int toolsY = y + 27;
        int blocksY = y + 52;
        int itemsY = y + 77;

        boolean on = ToggleCheckbox.ON;
        boolean off = ToggleCheckbox.OFF;

        this.toolsCheckbox = new ToggleCheckbox(x, toolsY, Component.translatable(LangUtil.Gui.SWING_TOOL), on);
        this.blocksCheckbox = new ToggleCheckbox(x, blocksY, Component.translatable(LangUtil.Gui.SWING_BLOCK), on);
        this.itemsCheckbox = new ToggleCheckbox(x, itemsY, Component.translatable(LangUtil.Gui.SWING_ITEM), on);
        this.resetCheckbox = new ToggleCheckbox(x, resetY, Component.translatable(LangUtil.Gui.SWING_RESET), off);
    }

    /* Getters */

    public Button getAddItemButton() { return addItemButton; }
    public Minecraft getMinecraft() { return minecraft; }
    public ItemRenderer getItemRenderer() { return this.itemRenderer; }

    /* Helpers */

    /**
     * Checks if the item suggestions window is closed.
     * @return Whether the item suggestions helper is null or the helper is not suggesting anything.
     */
    public boolean suggestionsAreClosed()
    {
        return this.itemSuggestions == null || !this.itemSuggestions.isSuggesting();
    }

    /**
     * Change the search box focus.
     * @param state The new focus state.
     */
    public void setSearchBoxFocus(boolean state) { this.searchBox.setFocus(state); }

    /**
     * Gets the speed value from the row list and saves that value to the config from the provided item.
     * @param item The item to save the speed from.
     */
    private void getAndSaveSpeedFromItem(Item item)
    {
        CustomSwings.addItem(item);
        AutoConfig.getConfigHolder(ClientConfig.class).save();

        SpeedRowList.added = CustomSwings.getEntryFromItem(item);

        this.refresh();
        this.openToast(item);
    }

    /**
     * Display a pop-up toast that notifies the user a new swing speed was saved and added.
     * @param item The item to get display information from.
     */
    private void openToast(Item item)
    {
        Component message = Component.translatable(LangUtil.Gui.SWING_ADD).withStyle(ChatFormatting.WHITE);
        Component display = Component.translatable(item.getName(item.getDefaultInstance()).getString()).withStyle(ChatFormatting.GREEN);
        this.minecraft.getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.TUTORIAL_HINT, message, display));
    }

    /**
     * Clears any input that is typed inside the search box.
     */
    private void clearSearchBox() { this.itemSuggestions.resetInputBox(); }

    /**
     * Clears the search box, resets the screen, unsets search box focus, and sets the state for the reset button.
     * This is called when a new item has been saved and added to the list.
     */
    private void refresh()
    {
        this.clearSearchBox();
        this.minecraft.setScreen(this);
        this.searchBox.setFocus(false);
        this.resetButton.active = this.resetCheckbox.selected();
    }

    /**
     * Clears all saved items from the config.
     * This will not be saved until confirmed by the user.
     */
    private void resetSwingSpeedList()
    {
        CLIENT_CONFIG.custom.clear();
        SpeedRowList.added = null;

        this.resetCheckbox.onPress();
        this.clearSearchBox();
        this.minecraft.setScreen(this);
    }

    /**
     * Checks if the list has been changed and can be saved by the user.
     * @return Whether changes are savable.
     */
    private boolean isSavable()
    {
        if (this.undo.size() != CLIENT_CONFIG.custom.size())
            return true;
        else if (SpeedRowList.DELETED_ROWS.size() > 0)
            return true;

        for (Map.Entry<String, Integer> entry : CLIENT_CONFIG.custom.entrySet())
        {
            if (this.undo.get(entry.getKey()).intValue() != entry.getValue().intValue())
                return true;
        }

        return false;
    }

    /**
     * Sorts the swing speed list based on the state of the filter checkboxes.
     * This method does add items to the swing speed row list.
     */
    private void sortSwingSpeedList()
    {
        boolean addTools = this.toolsCheckbox.selected();
        boolean addBlocks = this.blocksCheckbox.selected();
        boolean addItems = this.itemsCheckbox.selected();

        List<Map.Entry<String, Integer>> sorted = CustomSwings.getSortedItems(addTools, addBlocks, addItems);

        for (Map.Entry<String, Integer> entry : sorted)
            this.speedRowList.addRow(entry);
    }

    /**
     * Informs the renderer where the save button starts.
     * @return The starting x-position of the save button.
     */
    private int getSavePosition() { return this.width / 2 + 3; }

    /**
     * Renders a widget.
     * @param widget The widget to render.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    private void renderWidget(Widget widget, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (widget instanceof Button && ((Button) widget).x == this.getSavePosition())
            ((Button) widget).active = this.isSavable();

        widget.render(poseStack, mouseX, mouseY, partialTick);
    }

    /* Overrides */

    /**
     * Handler method for tick events.
     * This method ticks widgets and updates checkbox states.
     */
    @Override
    public void tick()
    {
        this.searchBox.tick();

        if (this.resetButton.active != this.resetCheckbox.selected())
            this.resetButton.active = this.resetCheckbox.selected();
    }

    /**
     * Handler method for when the game window resizes.
     * The search box and item suggestions window needs reset when this event occurs.
     * @param minecraft The game instance.
     * @param width The new game window width.
     * @param height The new game window height.
     */
    @Override
    public void resize(Minecraft minecraft, int width, int height)
    {
        String searching = this.searchBox.getValue();

        this.init(minecraft, width, height);
        this.searchBox.setValue(searching);
        this.itemSuggestions.updateItemSuggestions();
    }

    /**
     * Handler method for when a key is pressed.
     * @param key A key code.
     * @param scanCode A key scancode.
     * @param modifiers Any held key modifiers.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers)
    {
        if (this.itemSuggestions.keyPressed(key))
            return true;
        else if (key == 257 && this.addItemButton.active)
        {
            this.getAndSaveSpeedFromItem(this.itemSuggestions.getItem());
            return true;
        }
        else if (key == 256 && this.shouldCloseOnEsc())
        {
            this.onCancel();
            return true;
        }
        else if (super.keyPressed(key, scanCode, modifiers))
            return true;
        else
            return key == 257 || key == 335;
    }

    /**
     * Handler method for when the mouse is scrolled.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param delta The change in scrolling direction.
     * @return Whether this method handled the scroll event.
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        return this.itemSuggestions.mouseScrolled(delta) || super.mouseScrolled(mouseX, mouseY, delta);
    }

    /**
     * Handler method for when the mouse is clicked.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the mouse click event.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return this.itemSuggestions.mouseClicked(mouseX, mouseY) || super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Override for renderable widget additions.
     * @param widget The widget to add.
     * @return The widget that was added.
     * @param <T> The widget type.
     */
    @Override
    protected <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T widget)
    {
        this.renderables.add(widget);
        return this.addWidget(widget);
    }

    /**
     * Override for widget removals.
     * @param widget The widget to remove.
     */
    @Override
    protected void removeWidget(GuiEventListener widget)
    {
        if (widget instanceof Widget)
            this.renderables.remove(widget);

        super.removeWidget(widget);
    }

    /**
     * Override for widget clearing.
     */
    @Override
    protected void clearWidgets()
    {
        this.renderables.clear();
        super.clearWidgets();
    }

    /**
     * Override for screen initialization.
     *
     * This method defines renderable widgets, sorts the swing speed list, sets states, and defines the item suggestion
     * helper and runs suggestion updates.
     */
    @Override
    protected void init()
    {
        this.addRenderableWidget(this.toolsCheckbox);
        this.addRenderableWidget(this.blocksCheckbox);
        this.addRenderableWidget(this.itemsCheckbox);
        this.addRenderableWidget(this.resetCheckbox);

        WidgetProvider widget = new WidgetProvider();

        this.speedRowList = widget.createSpeedRowList();
        this.searchBox = widget.createSearchBox();
        this.searchBox.setResponder(this::onEdited);

        this.sortSwingSpeedList();

        this.addItemButton = widget.createAddButton();
        this.autofillButton = widget.createAutofillButton();
        this.resetButton = widget.createResetButton();
        this.resetButton.active = false;
        this.autofillButton.active = false;
        this.addItemButton.active = false;

        this.addWidget(this.speedRowList);
        this.addRenderableWidget(this.searchBox);
        this.addRenderableWidget(this.addItemButton);
        this.addRenderableWidget(this.autofillButton);
        this.addRenderableWidget(this.resetButton);
        this.addRenderableWidget(widget.createCancelButton());
        this.addRenderableWidget(widget.createSaveButton());

        this.setInitialFocus(this.searchBox);

        this.itemSuggestions = new ItemSuggestionHelper(this, this.searchBox, this.font, 7, -16777216);
        this.itemSuggestions.setAllowSuggestions(true);
        this.itemSuggestions.updateItemSuggestions();
    }

    /**
     * Override for rendering the customizable swing speed screen.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        this.autofillButton.active = this.minecraft.level != null;

        if (this.minecraft.level != null)
            this.fillGradient(poseStack, 0, 0, this.width, this.height, 839913488, 16777216);
        else
            this.renderDirtBackground(0);

        this.speedRowList.render(poseStack, mouseX, mouseY, partialTick);
        this.renderScreenTitle(poseStack, 7);
        this.searchBox.render(poseStack, mouseX, mouseY, partialTick);
        this.renderables.forEach(widget -> this.renderWidget(widget, poseStack, mouseX, mouseY, partialTick));
        this.itemSuggestions.render(poseStack, mouseX, mouseY);
        this.itemRenderer.renderGuiItem(new ItemStack(Items.TNT), this.resetButton.x + 2, this.resetButton.y + 2);
    }

    /* Cancelling Consumer */

    private class CancelConsumer implements BooleanConsumer
    {
        @Override
        public void accept(boolean understood)
        {
            if (understood)
            {
                SwingScreen.this.onClose(true);
                SwingScreen.this.minecraft.setScreen(SwingScreen.this.parent);
            }
            else
                SwingScreen.this.minecraft.setScreen(SwingScreen.this);
        }
    }

    /* On-Click Handlers */

    /**
     * Search box responder for when characters are typed in the search box.
     * @param ignored Current input is ignored since the item suggestions helper handles this.
     */
    private void onEdited(String ignored) { this.itemSuggestions.updateItemSuggestions(); }

    /**
     * Instructions to run when a new item swing speed is to be added.
     */
    private void onAddSwingSpeed() { this.getAndSaveSpeedFromItem(this.itemSuggestions.getItem()); }

    /**
     * Instructions to run when the user wants to quickly add the item their player is holding.
     */
    private void onAutofill()
    {
        if (this.minecraft.player != null)
            this.getAndSaveSpeedFromItem(this.minecraft.player.getMainHandItem().getItem());
    }

    /**
     * Instructions to run when the changes made are to be cancelled.
     */
    private void onCancel()
    {
        if (!this.isSavable())
        {
            this.onClose(true);
            return;
        }

        this.minecraft.setScreen
        (
            new ConfirmScreen
            (
                new CancelConsumer(),
                Component.translatable(LangUtil.Cloth.QUIT_CONFIG),
                Component.translatable(LangUtil.Cloth.QUIT_CONFIG_SURE),
                Component.translatable(LangUtil.Cloth.QUIT_DISCARD),
                Component.translatable(LangUtil.Vanilla.GUI_CANCEL)
            )
        );
    }

    /**
     * Instructions to run when the screen is closed.
     * @param isCancelled Whether changes made are cancelled.
     */
    private void onClose(boolean isCancelled)
    {
        if (!isCancelled)
        {
            for (Map.Entry<String, Integer> entry : SpeedRowList.DELETED_ROWS)
                CLIENT_CONFIG.custom.remove(entry.getKey());
        }
        else
        {
            CLIENT_CONFIG.custom.clear();
            CLIENT_CONFIG.custom.putAll(this.undo);
        }

        SpeedRowList.DELETED_ROWS.clear();
        SpeedRowList.added = null;

        super.onClose();
    }

    /*
       Widget Provider

       This class provides methods that returns widgets for the custom swing speed screen to use.
       These methods are used during screen initialization setup.
     */

    private class WidgetProvider
    {
        /**
         * The speed row list extends the vanilla abstract row list. All item swing speeds are controlled by this class.
         * @return A new speed row list instance.
         */
        public SpeedRowList createSpeedRowList()
        {
            return new SpeedRowList
            (
                SwingScreen.this,
                SwingScreen.this.width,
                SwingScreen.this.height,
                TOP_HEIGHT + 22,
                SwingScreen.this.height - BOTTOM_OFFSET,
                ITEM_HEIGHT
            );
        }

        /**
         * The search box is used to search for items to add to the custom swing speed row list.
         * @return A new edit box widget instance.
         */
        public EditBox createSearchBox()
        {
            return new EditBox
            (
                SwingScreen.this.font,
                SwingScreen.this.width / 2 - 112,
                TOP_ROW_Y,
                SEARCH_BOX_W,
                SEARCH_BOX_H,
                SwingScreen.this.searchBox,
                Component.empty()
            );
        }

        /**
         * The add button is active when a valid item identifier is found in the search box.
         * When clicked, the item found is added to the custom swing speed list.
         *
         * @return A new button instance.
         */
        public Button createAddButton()
        {
            Component tooltip = Component.translatable(LangUtil.Gui.SWING_ADD_TOOLTIP).withStyle(ChatFormatting.GREEN);

            return new Button
            (
                SwingScreen.this.width / 2 + 116,
                TOP_ROW_Y - 1,
                ToggleCheckbox.WIDTH,
                ToggleCheckbox.HEIGHT,
                Component.literal("+").withStyle(ChatFormatting.GREEN),
                (button) -> SwingScreen.this.onAddSwingSpeed(),
                (button, stack, mouseX, mouseY) -> SwingScreen.this.renderTooltip(stack, tooltip, mouseX, mouseY)
            );
        }

        /**
         * The autofill button is active when the player is in a valid level.
         *
         * When clicked, the item within the player's hand (or air if nothing is in the hand) is added to the custom
         * swing speed list.
         *
         * @return A new button instance.
         */
        public Button createAutofillButton()
        {
            Component tooltip = Component.translatable(LangUtil.Gui.SWING_AUTOFILL_TOOLTIP).withStyle(ChatFormatting.YELLOW);

            return new Button
            (
                SwingScreen.this.width / 2 - 134,
                TOP_ROW_Y - 1,
                ToggleCheckbox.WIDTH,
                ToggleCheckbox.HEIGHT,
                Component.literal("\u26a1").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD),
                (button) -> SwingScreen.this.onAutofill(),
                (button, stack, mouseX, mouseY) -> SwingScreen.this.renderTooltip(stack, tooltip, mouseX, mouseY)
            );
        }

        /**
         * The reset button clears all custom swing speeds. Nothing is saved until the user confirms the changes.
         * @return A new button instance.
         */
        public Button createResetButton()
        {
            List<Component> tooltip = Lists.newArrayList
            (
                Component.translatable(LangUtil.Gui.SWING_RESET_TOOLTIP_0).withStyle(ChatFormatting.RED),
                Component.translatable(LangUtil.Gui.SWING_RESET_TOOLTIP_1).withStyle(ChatFormatting.WHITE)
            );

            return new Button
            (
                SwingScreen.this.autofillButton.x - 21,
                TOP_ROW_Y - 1,
                ToggleCheckbox.WIDTH,
                ToggleCheckbox.HEIGHT,
                Component.empty(),
                (button) -> SwingScreen.this.resetSwingSpeedList(),
                (button, stack, mouseX, mouseY) -> SwingScreen.this.renderComponentTooltip(stack, tooltip, mouseX, mouseY)
            );
        }

        /**
         * The save button is active when changes have been made. When pressed, all changes are saved to disk.
         * @return A new button instance.
         */
        public Button createSaveButton()
        {
            return new Button
            (
                SwingScreen.this.getSavePosition(),
                SwingScreen.this.height - DONE_BUTTON_TOP_OFFSET,
                this.getSmallWidth(),
                BUTTON_HEIGHT,
                Component.translatable(LangUtil.Cloth.SAVE_AND_DONE),
                (button) -> SwingScreen.this.onClose(false)
            );
        }

        /**
         * The cancel button is always active, and when pressed, cancels any changes made to the custom swing speed list.
         * A confirmation window is presented before leaving the custom swing speed screen.
         *
         * @return A new button instance.
         */
        public Button createCancelButton()
        {
            return new Button
            (
                SwingScreen.this.width / 2 - getSmallWidth() - 3,
                SwingScreen.this.height - DONE_BUTTON_TOP_OFFSET,
                this.getSmallWidth(),
                BUTTON_HEIGHT,
                Component.translatable(LangUtil.Vanilla.GUI_CANCEL),
                (button) -> SwingScreen.this.onCancel()
            );
        }

        /**
         * A helper method that defines the width of a small button.
         * @return Small button widget.
         */
        private int getSmallWidth() { return Math.min(200, (SwingScreen.this.width - 50 - 12) / 3); }
    }
}
