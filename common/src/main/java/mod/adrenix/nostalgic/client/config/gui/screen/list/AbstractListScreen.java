package mod.adrenix.nostalgic.client.config.gui.screen.list;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.gui.overlay.*;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ContainerButton;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ContainerId;
import mod.adrenix.nostalgic.client.config.gui.widget.button.StateWidget;
import mod.adrenix.nostalgic.client.config.gui.widget.button.StateButton;
import mod.adrenix.nostalgic.client.config.gui.widget.group.ItemGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.group.TextGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.list.row.ConfigRowGroup;
import mod.adrenix.nostalgic.common.config.auto.AutoConfig;
import mod.adrenix.nostalgic.common.config.list.ListId;
import mod.adrenix.nostalgic.util.client.ItemClientUtil;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.function.TriConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import org.lwjgl.glfw.GLFW;

import java.util.*;

/**
 * A list screen contains a list of every item in the game, and those items can be assigned data by special instances of
 * the screen. Some examples include the custom swing speed list, the old item holding blacklist, food health points,
 * and other tweaks utilize this screen.
 */

public abstract class AbstractListScreen extends ConfigScreen
{
    /* Fields */

    public final ArrayList<TriConsumer<PoseStack, Integer, Integer>> renderOverlayTooltips = new ArrayList<>();
    protected final Set<ListFilter> filters = new HashSet<>();
    protected final ListFilter originalFilter;
    protected final ListId listId;
    protected final Screen parentScreen;
    private final Minecraft minecraft;
    private final NonNullList<ItemStack> items;
    private final Set<Widget> listWidgets;
    private WidgetProvider widgetProvider;
    private ItemStack highlightItem;

    /* Flags */

    private boolean permissionWatch = false;

    /* Constructor */

    /**
     * Constructor for an abstract list screen.
     * @param parentScreen The parent configuration screen.
     * @param title The list screen title.
     * @param listId A list screen identifier.
     * @param listFilter A list filter that controls what items appear in the all items list.
     * @param flags A var args list of list flags for this screen.
     */
    public AbstractListScreen
    (
        ConfigScreen parentScreen,
        Component title,
        ListId listId,
        ListFilter listFilter,
        ListFlags ...flags
    )
    {
        super(Minecraft.getInstance().screen, title);

        this.listId = listId;
        this.originalFilter = listFilter;
        this.parentScreen = parentScreen;
        this.minecraft = Minecraft.getInstance();
        this.items = NonNullList.create();
        this.listWidgets = new HashSet<>();
        this.highlightItem = null;

        this.updateItemTags();
        this.setupFlags(flags);
        this.filters.add(listFilter);
    }

    /* List Flags */

    /**
     * Set up the list flags for this screen.
     * @param flags A var args list of flags.
     */
    @SuppressWarnings("SwitchStatementWithTooFewBranches") // This will be expanded when more flags are made
    private void setupFlags(ListFlags ...flags)
    {
        for (ListFlags flag : flags)
        {
            switch (flag)
            {
                case WATCH_PERMISSIONS -> this.watchPermissions();
            }
        }
    }

    /**
     * Instructs the list screen that this list is used by the server and should therefore monitor the player's current
     * permissions. If the user loses permissions while changing this list, a pop-up window will inform the user of what
     * happened and will provide two options for managing changed list data.
     */
    private void watchPermissions() { this.permissionWatch = true; }

    /* Flag Getters */

    /**
     * @return Whether this list is monitoring the player's current permissions.
     */
    public boolean isPermissionWatched() { return this.permissionWatch; }

    /* Item Tag Registration */

    /**
     * If a list screen is being created before the player has joined a world, then the game's search trees must be
     * populated. Normally, these trees are populated when joining a world. Item tags can only be loaded by data sent
     * from a server (or integrated server).
     */
    private void updateItemTags()
    {
        if (this.minecraft.level != null)
            return;

        NonNullList<ItemStack> allItems = NonNullList.create();

        for (Item item : Registry.ITEM)
            item.fillItemCategory(CreativeModeTab.TAB_SEARCH, allItems);

        this.minecraft.populateSearchTree(SearchRegistry.CREATIVE_NAMES, allItems);
        this.minecraft.populateSearchTree(SearchRegistry.CREATIVE_TAGS, allItems);
    }

    /* Abstractions */

    /**
     * Instructions to run when the screen is closed.
     * @param isCancelled Whether changes made are cancelled.
     */
    protected abstract void closeList(boolean isCancelled);

    /**
     * Checks if the list has been changed and can be saved by the user.
     * @return Whether changes are savable.
     */
    protected abstract boolean isListSavable();

    /**
     * Get the rows associated with a list's saved items. If no items are saved, then a row with a message stating
     * that no items are saved will be shown.
     *
     * @return A list of config row list rows.
     */
    protected abstract ArrayList<ConfigRowList.Row> getSavedItems();

    /**
     * Adds an item to the list's saved entries. No changes are saved to the disk until confirmed by the user.
     * @param item The item to save.
     */
    public abstract void addItem(Item item);

    /**
     * Remove an item from the list's saved entries. No changes are saved to the disk until confirmed by the user.
     * @param item The item to remove.
     */
    public abstract void deleteItem(Item item);

    /**
     * Removes all entries that are within a list's saved entries.
     */
    public abstract void clearAllSaved();

    /**
     * Checks if the given item is saved to the list's saved entries.
     * @param item The item to check.
     */
    public abstract boolean isItemSaved(Item item);

    /**
     * Checks if the given item has been added to the list entries.
     * @param item The item to check to see if it is added to the list.
     * @return Whether the item is in the list's saved entries.
     */
    public abstract boolean isItemAdded(Item item);

    /**
     * Checks if the given item has been marked for deletion.
     * @param item The item to check.
     * @return Whether the item is marked for deletion in the current list.
     */
    public abstract boolean isItemDeleted(Item item);

    /* Shortcuts */

    /**
     * Shortcut for adding an item via an item stack. No changes are saved to the disk until confirmed by the user.
     * @param item The item stack to get an item from to save.
     */
    public void addItem(ItemStack item) { this.addItem(item.getItem()); }

    /**
     * Shortcut for removing an item stack from the saved entries list. No changes are saved to disk until confirmed by
     * the user.
     *
     * @param itemStack The item stack to get item data from.
     */
    public void deleteItem(ItemStack itemStack) { this.deleteItem(itemStack.getItem()); }

    /**
     * Shortcut for checking if the item stack is saved in the list's saved entries.
     * @param itemStack The item stack to get item data from.
     * @return Whether the given item stack is saved within this list's saved entries.
     */
    public boolean isItemSaved(ItemStack itemStack) { return this.isItemSaved(itemStack.getItem()); }

    /**
     * Shortcut for checking if the item stack is added to the list's saved entries.
     * @param itemStack The item stack to get item data from.
     * @return Whether the given item stack is in the list's saved entries.
     */
    public boolean isItemAdded(ItemStack itemStack) { return this.isItemAdded(itemStack.getItem()); }

    /**
     * Shortcut for checking if the item stack is marked for deletion.
     * @param itemStack The item stack to get item data from.
     * @return Whether the given item stack is marked for deletion in the current list.
     */
    public boolean isItemDeleted(ItemStack itemStack) { return this.isItemDeleted(itemStack.getItem()); }

    /* Getters */

    /**
     * A shortcut that gets the list screen's search box.
     * @return An edit box widget instance.
     */
    public EditBox getSearchBox() { return this.widgetProvider.searchBox; }

    /**
     * A shortcut that gets the configuration screen's configuration row list instance.
     * @return A config row list instance.
     */
    public ConfigRowList getConfigRowList() { return this.getWidgets().getConfigRowList(); }

    /**
     * A getter method for getting the items associated with this list screen.
     * @return A non-null list of item stacks.
     */
    public NonNullList<ItemStack> getItems() { return this.items; }

    /**
     * A getter method for getting the widgets associated with this list screen.
     * @return A set of widgets.
     */
    public Set<Widget> getListWidgets() { return this.listWidgets; }

    /**
     * A getter method for getting the list identifier associated with this list screen.
     * @return A list id enumeration value.
     */
    public ListId getListId() { return this.listId; }

    /* Methods */

    /**
     * Override for screen initialization.
     *
     * This method defines renderable widgets, sets up the item row list, sets states, and defines any helpers and
     * suggestion updates.
     */
    @Override
    protected void init()
    {
        // Creates required widgets needed from the parent config screen
        super.init();

        // Removes any widgets created by the parent config screen
        this.clearWidgets();

        // Removes any previous widgets created by this list screen
        this.listWidgets.clear();

        // Adds the config row list from the parent config screen back to the widget list
        this.addRenderableWidget(this.getConfigRowList());

        // Create a new widget provider
        this.widgetProvider = new WidgetProvider();

        // Search box setup
        this.getSearchBox().setMaxLength(50);
        this.getSearchBox().setBordered(true);
        this.getSearchBox().setVisible(true);
        this.getSearchBox().setTextColor(0xFFFFFF);

        this.setInitialFocus(this.getSearchBox());
        this.refreshSearchResults();

        // Initial container expansion
        ContainerButton.expand(ContainerId.LIST_HELP);
        ContainerButton.expand(ContainerId.SAVED_ITEMS);
        ContainerButton.expand(ContainerId.ALL_ITEMS);

        // Add renderable widgets
        this.listWidgets.forEach((child) ->
        {
            if (child instanceof AbstractWidget widget)
                this.addRenderableWidget(widget);
        });
    }

    /**
     * Cache an item stack that will be used to highlight in the saved entries container next render cycle.
     * @param itemStack The item stack to reference when highlighting a saved entry row.
     */
    public void highlightItem(ItemStack itemStack)
    {
        this.refreshSearchResults();
        ContainerButton.expand(ContainerId.SAVED_ITEMS);
        ContainerButton.expand(ContainerId.ALL_ITEMS);

        this.highlightItem = itemStack;
    }

    /**
     * Adds found items from the query in the search box to the given items list.
     * @param allItems A non-null list of item stacks.
     */
    protected void addSearchedItems(NonNullList<ItemStack> allItems)
    {
        allItems.clear();

        String query = this.getSearchBox().getValue();

        if (query.isEmpty())
        {
            for (Item item : Registry.ITEM)
                item.fillItemCategory(CreativeModeTab.TAB_SEARCH, allItems);
        }
        else
        {
            SearchTree<ItemStack> searchTree;

            if (query.startsWith("#"))
            {
                query = query.substring(1);
                searchTree = this.minecraft.getSearchTree(SearchRegistry.CREATIVE_TAGS);
            }
            else
                searchTree = this.minecraft.getSearchTree(SearchRegistry.CREATIVE_NAMES);

            allItems.addAll(searchTree.search(query.toLowerCase(Locale.ROOT)));
        }

        this.filterItem(allItems);
    }

    /**
     * Refreshes the items that are found from updating the search box.
     * Vanilla item searching mechanics are used here.
     */
    public void refreshSearchResults()
    {
        this.resetRowList();
        this.addSearchedItems(this.items);
        this.generateContainers();
    }

    /**
     * Manage a filter entry for this list screen.
     * @param filter A new list filter enumeration value.
     */
    public void manageFilter(ListFilter filter, boolean state)
    {
        this.filters.remove(ListFilter.ALL);

        if (state)
            this.filters.add(filter);
        else
            this.filters.remove(filter);

        if (this.filters.size() == 0)
            this.filters.add(ListFilter.ALL);
    }

    /**
     * Filter out item stacks out of the given items list for this screen. The current list filter will be used.
     * @param allItems The list of items to filter through.
     */
    private void filterItem(NonNullList<ItemStack> allItems)
    {
        ArrayList<ItemStack> filteredItems = new ArrayList<>();

        for (ItemStack itemStack : allItems)
        {
            ItemStack copyStack = ItemClientUtil.getItemStack(ItemClientUtil.getResourceKey(itemStack.getItem()));

            if (itemStack.getTag() != null && !itemStack.getTag().equals(copyStack.getTag()))
            {
                filteredItems.add(itemStack);
                continue;
            }

            if (this.filters.contains(ListFilter.ALL))
                continue;

            Item item = itemStack.getItem();

            boolean isEdible = item.isEdible();
            boolean isTool = item instanceof DiggerItem || item instanceof SwordItem;
            boolean isBlock = item instanceof BlockItem;
            boolean isItem = !isTool && !isBlock;

            boolean isItemsFiltered = isItem && this.filters.contains(ListFilter.ITEMS);
            boolean isToolsFiltered = isTool && this.filters.contains(ListFilter.TOOLS);
            boolean isBlocksFiltered = isBlock && this.filters.contains(ListFilter.BLOCKS);
            boolean isEdibleFiltered = isEdible && this.filters.contains(ListFilter.EDIBLE);

            if (isItemsFiltered || isToolsFiltered || isBlocksFiltered || isEdibleFiltered)
                filteredItems.add(itemStack);
        }

        for (ItemStack itemStack : filteredItems)
            allItems.remove(itemStack);
    }

    /**
     * Generate the saved/all items containers and the rows that go within them.
     * Saved item rows are first, then all items rows are second.
     */
    private void generateContainers()
    {
        ConfigRowGroup.ContainerRow tutorial = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.LIST_HELP_TITLE),
            this::getTutorial,
            ContainerId.LIST_HELP
        );

        ConfigRowGroup.ContainerRow savedItems = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.LIST_SAVED_ITEMS),
            this::getSavedItems,
            ContainerId.SAVED_ITEMS
        );

        ConfigRowGroup.ContainerRow allItems = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.LIST_ALL_ITEMS),
            this::getAllItems,
            ContainerId.ALL_ITEMS
        );

        this.getConfigRowList().addRow(tutorial.generate());
        this.getConfigRowList().addRow(savedItems.generate());
        this.getConfigRowList().addRow(allItems.generate());
    }

    /**
     * Jump to a saved entry row within the row list based on the given item stack.
     * @param itemStack The item stack that is used to find the saved entry row.
     */
    public void jumpToEntry(ItemStack itemStack)
    {
        if (itemStack == null)
            return;

        for (ConfigRowList.Row row : this.getConfigRowList().children())
        {
            if (row.getResourceKey().equals(ItemClientUtil.getResourceKey(itemStack.getItem())))
            {
                this.getConfigRowList().setScrollOn(row);
                break;
            }
        }

        if (this.highlightItem == itemStack)
            this.highlightItem = null;
    }

    /**
     * @return Generates the tutorial paragraph for a container.
     */
    private ArrayList<ConfigRowList.Row> getTutorial()
    {
        return new TextGroup(Component.translatable(LangUtil.Gui.LIST_TUTORIAL)).generate();
    }

    /**
     * Get rows that contain all items within the game. The buttons within these rows are clickable and will bring
     * up a new overlay window with options for the user.
     *
     * @return A list of config row list rows.
     */
    private ArrayList<ConfigRowList.Row> getAllItems() { return new ItemGroup(this).generate(); }

    /**
     * Handler method for tick events.
     * This method ticks widgets and updates checkbox states.
     */
    @Override
    public void tick() { this.getSearchBox().tick(); }

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
        String query = this.getSearchBox().getValue();

        this.init(minecraft, width, height);
        this.getSearchBox().setValue(query);

        if (!query.isEmpty())
        {
            this.refreshSearchResults();
            ContainerButton.collapse(ContainerId.LIST_HELP);
        }

        if (Overlay.isOpened())
            this.getSearchBox().setFocus(false);

        Overlay.resize();
    }

    /**
     * Handler method for when a character is typed.
     * @param codePoint The character code.
     * @param modifiers Modifiers.
     * @return Whether the character that was typed was handled by this method.
     */
    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        String query = this.getSearchBox().getValue();

        if (this.getSearchBox().charTyped(codePoint, modifiers))
        {
            if (!this.getSearchBox().getValue().equals(query))
            {
                this.refreshSearchResults();
                ContainerButton.collapse(ContainerId.LIST_HELP);
            }

            return true;
        }

        return false;
    }

    /**
     * Handler method for when a key is pressed.
     * @param keyCode A key code.
     * @param scanCode A key scancode.
     * @param modifiers Any held key modifiers.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        String query = this.getSearchBox().getValue();
        boolean isEsc = keyCode == GLFW.GLFW_KEY_ESCAPE;

        if (Overlay.getVisible() instanceof LostPermissionOverlay)
            return false;

        if (this.getSearchBox().keyPressed(keyCode, scanCode, modifiers))
        {
            if (!this.getSearchBox().getValue().equals(query))
                this.refreshSearchResults();

            return true;
        }

        if (this.getSearchBox().isFocused() && this.getSearchBox().isVisible())
        {
            if (isEsc)
                this.getSearchBox().setFocus(false);

            return true;
        }

        if (isEsc && this.getConfigRowList().unsetFocus())
            return true;

        if (isEsc && this.shouldCloseOnEsc())
        {
            this.exitList();
            return true;
        }

        return false;
    }

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
        if (widget instanceof AbstractWidget button)
        {
            // An active overlay window requires that every widget on the active screen be inactive
            if (Overlay.isOpened())
                button.active = false;
            else
            {
                // Every widget should be active unless otherwise noted below
                button.active = true;

                // Widget auto-add button check
                if (this.minecraft.level == null)
                    this.widgetProvider.autoButton.active = false;

                // Filter list button check
                if (this.originalFilter == ListFilter.EDIBLE)
                    this.widgetProvider.filterButton.active = false;
            }
        }

        // Save button is only active when something can be saved
        Button save = this.widgetProvider.saveButton;

        if (widget instanceof Button button && button.x == save.x && button.y == save.y)
            button.active = !Overlay.isOpened() && this.isListSavable();

        // Render the widget to the screen
        widget.render(poseStack, mouseX, mouseY, partialTick);
    }

    /**
     * Override for rendering the list screen.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        // Background rendering
        if (this.minecraft.level != null)
            this.fillGradient(poseStack, 0, 0, this.width, this.height, 839913488, 16777216);
        else
            this.renderDirtBackground(0);

        this.fillGradient(poseStack, 0, 0, this.width, this.height, -1072689136, -804253680);
        this.fillGradient(poseStack, 0, 0, this.width, this.height, 1744830464, 1744830464);

        // Row list rendering
        this.getConfigRowList().render(poseStack, mouseX, mouseY, partialTick);
        this.jumpToEntry(this.highlightItem);

        // Widget rendering
        for (Widget widget : this.listWidgets)
            this.renderWidget(widget, poseStack, mouseX, mouseY, partialTick);

        // Screen title rendering
        drawCenteredString(poseStack, this.font, this.title.getString(), this.width / 2, 8, 0xFFFFFF);

        // Permission monitor
        Overlay overlay = Overlay.getVisible();

        if (this.isPermissionWatched() && !NetUtil.isPlayerOp())
        {
            if (ClassUtil.isNotInstanceOf(overlay, LostPermissionOverlay.class))
                new LostPermissionOverlay();
        }
        else if (this.isPermissionWatched() && NetUtil.isPlayerOp() && overlay instanceof LostPermissionOverlay)
            Overlay.close();

        // Overlay rendering
        Overlay.render(poseStack, mouseX, mouseY, partialTick);

        // Render last runners
        if (!Overlay.isOpened())
            this.renderLast.forEach(Runnable::run);
        else
        {
            // Ensure the search box is not focused
            this.widgetProvider.searchBox.setFocus(false);

            // Translate on the z-axis for tooltips
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.0D, 400.0D);

            this.renderOverlayTooltips.forEach(tooltip -> tooltip.accept(poseStack, mouseX, mouseY));

            poseStack.popPose();
        }

        this.renderLast.clear();
        this.renderOverlayTooltips.clear();
    }

    /*
       Cancelling Changes

       The following methods and classes are used to cancel changes made in a list screen.
       The user can cancel the changes and go back to the config screen, or go back to the list screen.
     */

    /**
     * Instructions to run when the changes made are to be cancelled.
     */
    private void exitList()
    {
        if (!this.isListSavable())
        {
            this.closeList(true);
            return;
        }

        this.minecraft.setScreen
        (
            new ConfirmScreen
            (
                new CancelConsumer(),
                Component.translatable(LangUtil.Gui.CONFIRM_QUIT_TITLE),
                Component.translatable(LangUtil.Gui.CONFIRM_QUIT_BODY),
                Component.translatable(LangUtil.Gui.CONFIRM_QUIT_DISCARD),
                Component.translatable(LangUtil.Gui.CONFIRM_QUIT_CANCEL)
            )
        );
    }

    /**
     * Exit the current list screen without saving any changes.
     */
    public void closeWithoutSaving() { this.closeList(true); }

    /**
     * This class is responsible for instructing the game which screen to go to.
     * This depends on the response set by the user.
     */

    private class CancelConsumer implements BooleanConsumer
    {
        @Override
        public void accept(boolean understood)
        {
            if (understood)
            {
                AbstractListScreen.this.closeList(true);
                AbstractListScreen.this.minecraft.setScreen(AbstractListScreen.this.parentScreen);
            }
            else
                AbstractListScreen.this.minecraft.setScreen(AbstractListScreen.this);
        }
    }

    /**
     * This class is responsible for creating widgets for the screen.
     * Widget constants are kept in this class.
     */

    private class WidgetProvider
    {
        /* Widget Constants */

        public static final int SEARCH_TOP_Y = 25;
        public static final int SEARCH_BOX_W = 226;
        public static final int SEARCH_BOX_H = 18;

        /* Widget Helpers */

        private int getSmallWidth() { return Math.min(200, (AbstractListScreen.this.width - 50 - 12) / 3); }

        /* List Widgets */

        public final EditBox searchBox;
        public final Button cancelButton;
        public final Button saveButton;
        public final StateButton nukeButton;
        public final StateButton autoButton;
        public final StateButton filterButton;
        public final StateButton clearButton;
        public final StateButton swingButton;

        /* Constructor */

        /**
         * Create a new widget provider instance. This constructor will define all widgets created by this provider.
         * All widgets are added to the {@link AbstractListScreen#listWidgets} list. Any widget setup or handling must be
         * done elsewhere.
         */
        public WidgetProvider()
        {
            this.searchBox = this.createSearchBox();
            this.cancelButton = this.createCancelButton();
            this.saveButton = this.createSaveButton();
            this.nukeButton = this.createNukeButton();
            this.autoButton = this.createAutoButton();
            this.filterButton = this.createFilterButton();
            this.clearButton = this.createClearButton();
            this.swingButton = this.createSwingButton();

            Set<Widget> children = new HashSet<>
            (
                Set.of
                (
                    searchBox,
                    cancelButton,
                    saveButton,
                    nukeButton,
                    autoButton,
                    filterButton,
                    clearButton
                )
            );

            if (AbstractListScreen.this.getListId() == ListId.CUSTOM_SWING)
                children.add(this.swingButton);

            AbstractListScreen.this.listWidgets.addAll(children);
        }

        /* Widget Creators */

        /**
         * The search box is used to search for items within the item row list.
         * @return A new edit box widget instance.
         */
        private EditBox createSearchBox()
        {
            return new EditBox
            (
                AbstractListScreen.this.font,
                AbstractListScreen.this.width / 2 - 112,
                SEARCH_TOP_Y,
                SEARCH_BOX_W,
                SEARCH_BOX_H,
                Component.empty()
            );
        }

        /**
         * The save button is active when changes have been made. When pressed, all changes are saved to disk.
         * @return A new button instance.
         */
        private Button createSaveButton()
        {
            return new Button
            (
                AbstractListScreen.this.width / 2 + 3,
                AbstractListScreen.this.height - SettingsScreen.DONE_BUTTON_TOP_OFFSET,
                this.getSmallWidth(),
                SettingsScreen.BUTTON_HEIGHT,
                Component.translatable(LangUtil.Gui.BUTTON_SAVE_AND_DONE),
                (button) ->
                {
                    AbstractListScreen.this.closeList(false);
                    AutoConfig.getConfigHolder(ClientConfig.class).save();
                }
            );
        }

        /**
         * The cancel button is always active, and when pressed, cancels any changes made to the list.
         * A confirmation window is presented before leaving the list screen.
         *
         * @return A new button instance.
         */
        private Button createCancelButton()
        {
            return new Button
            (
                AbstractListScreen.this.width / 2 - this.getSmallWidth() - 3,
                AbstractListScreen.this.height - SettingsScreen.DONE_BUTTON_TOP_OFFSET,
                this.getSmallWidth(),
                SettingsScreen.BUTTON_HEIGHT,
                Component.translatable(LangUtil.Vanilla.GUI_CANCEL),
                (button) -> AbstractListScreen.this.exitList()
            );
        }

        /**
         * The nuke button is always active, and when pressed, opens a confirmation pop-up overlay window asking the
         * user if the entire saved entries list should be cleared.
         *
         * @return A new state button instance.
         */
        private StateButton createNukeButton()
        {
            return new StateButton
            (
                StateWidget.NUKE,
                this.searchBox.x - 61,
                this.searchBox.y - 1,
                (button) -> new NukeListOverlay()
            );
        }

        /**
         * This button opens an overlay with list filtering options.
         * @return A state button widget.
         */
        private StateButton createFilterButton()
        {
            return new StateButton
            (
                StateWidget.FILTER,
                this.searchBox.x - 42,
                this.searchBox.y - 1,
                (button) -> new FilterListOverlay()
            );
        }

        /**
         * This button quickly adds the item in the player's hand.
         * @return A state button widget.
         */
        private StateButton createAutoButton()
        {
            return new StateButton(StateWidget.LIGHTNING, this.searchBox.x - 23, this.searchBox.y - 1, (button) ->
            {
                if (AbstractListScreen.this.minecraft.player != null)
                {
                    this.searchBox.setValue("");
                    this.searchBox.setFocus(false);

                    ItemStack itemStack = AbstractListScreen.this.minecraft.player.getMainHandItem();

                    AbstractListScreen.this.addItem(itemStack);
                    AbstractListScreen.this.refreshSearchResults();
                    AbstractListScreen.this.highlightItem(itemStack);
                }
            });
        }

        /**
         * This button clears any input from the search box.
         * @return A state button widget.
         */
        private StateButton createClearButton()
        {
            EditBox search = this.searchBox;

            return new StateButton(StateWidget.CLEAR, search.x + search.getWidth() + 3, search.y - 1, (button) ->
            {
                search.setValue("");
                search.setFocus(true);
                AbstractListScreen.this.refreshSearchResults();
            });
        }

        /**
         * This button will open an informative swing speed pop-up overlay.
         * @return A state button widget.
         */
        private StateButton createSwingButton()
        {
            EditBox search = this.searchBox;

            return new StateButton(StateWidget.SWING, search.x + search.getWidth() + 22, search.y - 1, (button) ->
            {
                if (AbstractListScreen.this.getListId() == ListId.CUSTOM_SWING)
                    new SpeedOverlay();
            });
        }
    }
}
