package mod.adrenix.nostalgic.client.config.gui.screen.list;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.gui.overlay.*;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.toast.ToastNotification;
import mod.adrenix.nostalgic.client.config.gui.widget.button.*;
import mod.adrenix.nostalgic.client.config.gui.widget.group.ItemGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.group.TextGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.list.row.ConfigRowBuild;
import mod.adrenix.nostalgic.client.config.gui.widget.list.row.ConfigRowGroup;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextAlign;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.auto.AutoConfig;
import mod.adrenix.nostalgic.common.config.list.AbstractList;
import mod.adrenix.nostalgic.common.config.list.ListFilter;
import mod.adrenix.nostalgic.common.config.list.ListId;
import mod.adrenix.nostalgic.common.config.list.ListInclude;
import mod.adrenix.nostalgic.mixin.duck.MaxSizeChanger;
import mod.adrenix.nostalgic.network.packet.PacketC2SChangeTweak;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.client.KeyUtil;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.ItemCommonUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.PacketUtil;
import mod.adrenix.nostalgic.util.common.function.TriConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A list screen contains a list of every item in the game, and those items can be assigned data by special instances of
 * the screen. Some examples include the custom swing speed list, the old item holding blacklist, food health points,
 * and other tweaks utilize this screen.
 */

public abstract class ListScreen extends ConfigScreen
{
    /* Fields */

    public final ArrayList<TriConsumer<GuiGraphics, Integer, Integer>> renderOverlayTooltips = new ArrayList<>();

    protected final Set<ListFilter> filters = new HashSet<>();
    protected final ListInclude onlyInclude;
    protected final ListId listId;
    protected final AbstractList list;
    protected final Screen parentScreen;
    protected final Set<String> disabledDefaults;
    protected final Set<String> undoDisabledDefaults;

    private final Minecraft minecraft;
    private final NonNullList<ItemStack> allItems;
    private final NonNullList<ItemStack> selectableItems;
    private final Set<Renderable> listWidgets;

    private WidgetProvider widgetProvider;
    private ItemStack highlightItem;
    private double scrollAmountCache;

    /* Flags */

    private boolean permissionWatch = false;

    /* Constructor */

    /**
     * Constructor for an abstract list screen.
     * @param title The list screen title.
     * @param list An abstract list instance.
     */
    public ListScreen(Component title, AbstractList list)
    {
        super(Minecraft.getInstance().screen, title);

        this.list = list;
        this.listId = list.getId();
        this.onlyInclude = list.getInclude();
        this.disabledDefaults = list.getDisabledDefaults();
        this.undoDisabledDefaults = Sets.newHashSet(list.getDisabledDefaults());
        this.parentScreen = Minecraft.getInstance().screen;
        this.minecraft = Minecraft.getInstance();
        this.allItems = NonNullList.create();
        this.selectableItems = NonNullList.create();
        this.listWidgets = new HashSet<>();
        this.highlightItem = null;
        this.scrollAmountCache = 0.0D;

        this.updateItemTags();

        if (TweakClientCache.get(list.getTweak()).isServer())
            this.watchPermissions();
    }

    /**
     * Instructs the list screen that this list is used by the server and should therefore monitor the player's current
     * permissions. If the user loses permissions while changing this list, a pop-up window will inform the user of what
     * happened and will provide two options for managing changed list data.
     */
    public void watchPermissions() { this.permissionWatch = true; }

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
        for (Item item : BuiltInRegistries.ITEM)
            this.allItems.add(item.getDefaultInstance());

        if (this.minecraft.level == null)
        {
            this.minecraft.populateSearchTree(SearchRegistry.CREATIVE_NAMES, this.allItems);
            this.minecraft.populateSearchTree(SearchRegistry.CREATIVE_TAGS, this.allItems);
        }

        switch (this.onlyInclude)
        {
            case ALL -> this.filters.add(ListFilter.NONE);
            case NO_TOOLS -> this.filters.add(ListFilter.TOOLS);
            case NO_ITEMS -> this.filters.add(ListFilter.ITEMS);
            case NO_BLOCKS -> this.filters.add(ListFilter.BLOCKS);
            case ONLY_TOOLS -> this.filters.addAll(Set.of(ListFilter.BLOCKS, ListFilter.ITEMS));
            case ONLY_ITEMS -> this.filters.addAll(Set.of(ListFilter.BLOCKS, ListFilter.TOOLS));
            case ONLY_BLOCKS -> this.filters.addAll(Set.of(ListFilter.ITEMS, ListFilter.TOOLS));
            case ONLY_EDIBLE -> this.filters.addAll(Set.of(ListFilter.ITEMS, ListFilter.TOOLS, ListFilter.BLOCKS));
        }

        this.filterItems(this.allItems);
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
    protected abstract ArrayList<ConfigRowList.Row> getSavedRows();

    /**
     * Get the rows associated with a list's default entries. If no default entries are available, then no "Default
     * Entries" container should be generated.
     *
     * @return A list of config row list rows.
     */
    protected abstract ArrayList<ConfigRowList.Row> getDefaultRows();

    /**
     * @return The number of default entries associated with a list.
     */
    protected abstract int getDefaultCount();

    /**
     * Disables all default entries for a list.
     */
    public abstract void disableAllDefaults();

    /**
     * Enables all default entries for a list.
     */
    public abstract void enableAllDefaults();

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
    public NonNullList<ItemStack> getSelectableItems() { return this.selectableItems; }

    /**
     * A getter method for getting the widgets associated with this list screen.
     * @return A set of widgets.
     */
    public Set<Renderable> getListWidgets() { return this.listWidgets; }

    /**
     * A getter method for getting the list filters currently applied to this list screen.
     * @return A set of list filter enumeration values.
     */
    public Set<ListFilter> getFilters() { return this.filters; }

    /**
     * A getter method for getting the list identifier associated with this list screen.
     * @return A list id enumeration value.
     */
    public ListId getListId() { return this.listId; }

    /* Common Utility */

    /**
     * Disables a default entry by adding it to the disabled default entries set.
     * @param resourceKey The item resource key to add to the disabled default entries set.
     */
    public void disableDefaultItem(String resourceKey) { this.disabledDefaults.add(resourceKey); }

    /**
     * Enables a default entry by removing it from the deleted default entries set.
     * @param resourceKey The item resource key to remove from the disabled default entries set.
     */
    public void enableDefaultItem(String resourceKey) { this.disabledDefaults.remove(resourceKey); }

    /**
     * Checks if the given item resource key is contained within the deleted default entries set.
     * @param resourceKey The item resource key to check.
     * @return Whether the given resource key is a disabled default entry.
     */
    public boolean isDefaultItemDisabled(String resourceKey) { return this.disabledDefaults.contains(resourceKey); }

    /**
     * Checks if the given item is eligible to be added to a list.
     * @param item The item to check to see if it can be added.
     * @return Whether the item can be added to a list.
     */
    public boolean isItemEligible(Item item)
    {
        String resourceKey = ItemCommonUtil.getResourceKey(item);

        for (ItemStack compareItem : this.allItems)
        {
            if (ItemCommonUtil.getResourceKey(compareItem.getItem()).equals(resourceKey))
                return true;
        }

        return false;
    }

    /**
     * Shortcut for checking if an item stack is eligible to be added to a list.
     * @param itemStack The item stack to check to see if it can be added.
     * @return Whether the item stack can be added to a list.
     */
    public boolean isItemEligible(ItemStack itemStack) { return this.isItemEligible(itemStack.getItem()); }

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

        // Caches any important information from the parent config screen
        if (this.parentScreen instanceof ConfigScreen configScreen)
            configScreen.setupCache();

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
        ContainerButton.expand(ContainerId.DEFAULT_ITEMS);
        ContainerButton.expand(ContainerId.SAVED_ITEMS);
        ContainerButton.expand(ContainerId.SELECTABLE_ITEMS);

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
        ContainerButton.expand(ContainerId.SELECTABLE_ITEMS);

        this.highlightItem = itemStack;
    }

    /**
     * Sorts translated item names alphabetically.
     * @param firstKey The first resource key.
     * @param secondKey The second resource key.
     * @return A negative integer, zero, or a positive integer ignoring case considerations.
     */
    private int getLocalizedItem(String firstKey, String secondKey)
    {
        String firstItem = ItemCommonUtil.getLocalizedItem(firstKey);
        String secondItem = ItemCommonUtil.getLocalizedItem(secondKey);

        return firstItem.compareToIgnoreCase(secondItem);
    }

    /**
     * Sorts the given entry set alphabetically by translated item names.
     * @param entrySet A set of entries to sort.
     * @param resourceKey A function that accepts the type value for the set and returns an item resource key.
     * @param sortedConsumer A consumer that accepts a set of sorted values.
     * @param listClear A runnable that clears a set or map. This happens before sorting consumers accept sorted sets.
     * @param <T> The type value of the set.
     */
    protected <T> void sortEntries
    (
        Set<T> entrySet,
        Function<T, String> resourceKey,
        Consumer<Set<T>> sortedConsumer,
        Runnable listClear
    )
    {
        List<T> list = new ArrayList<>(entrySet);
        Set<T> unknowns = new LinkedHashSet<>();

        for (T entry : list)
        {
            if (!ItemCommonUtil.isValidKey(resourceKey.apply(entry)))
                unknowns.add(entry);
        }

        for (T entry : unknowns)
            list.remove(entry);

        list.sort((firstKey, secondKey) -> this.getLocalizedItem(resourceKey.apply(firstKey), resourceKey.apply(secondKey)));

        listClear.run();
        sortedConsumer.accept(unknowns);
        sortedConsumer.accept(new LinkedHashSet<>(list));
    }

    /**
     * Gets an arrow list of config row list rows based on the current search query.
     * @param entrySet A set of entries to loop through.
     * @param resourceKey A function that accepts the type value for the set and returns an item resource key.
     * @param addRow A bi-consumer that accepts an array list of config row list rows and a type value for the set.
     * @param <T> The type value of the set.
     * @return An array list of config row list rows that are relevant to the current search query and the given set.
     */
    protected <T> ArrayList<ConfigRowList.Row> getSearchedItems
    (
        String langKey,
        Set<T> entrySet,
        Function<T, String> resourceKey,
        BiConsumer<ArrayList<ConfigRowList.Row>, T> addRow
    )
    {
        ArrayList<ConfigRowList.Row> rows = new ArrayList<>();
        String query = this.getSearchBox().getValue();

        if (query.isEmpty())
        {
            for (T entry : entrySet)
                addRow.accept(rows, entry);
        }
        else
        {
            NonNullList<ItemStack> allItems = NonNullList.create();

            for (T entry : entrySet)
            {
                if (ItemCommonUtil.isValidKey(resourceKey.apply(entry)))
                    allItems.add(ItemCommonUtil.getItemStack(resourceKey.apply(entry)));
            }

            this.addSearchedItems(allItems);

            for (T entry : entrySet)
            {
                for (ItemStack itemStack : allItems)
                {
                    if (ItemCommonUtil.getResourceKey(itemStack.getItem()).equals(resourceKey.apply(entry)))
                    {
                        addRow.accept(rows, entry);
                        break;
                    }
                }
            }
        }

        if (rows.size() == 0)
        {
            Component translate = Component.translatable(langKey);
            Component text = Component.literal(ChatFormatting.RED + translate.getString());

            rows.addAll(new TextGroup(text, TextAlign.CENTER).generate());
        }

        return rows;
    }

    /**
     * Adds found items from the query in the search box to the given items list.
     * @param itemList A non-null list of item stacks.
     */
    protected void addSearchedItems(NonNullList<ItemStack> itemList)
    {
        itemList.clear();

        String query = this.getSearchBox().getValue();
        boolean isFiltered = this.listId != ListId.LEFT_CLICK_SPEEDS && this.listId != ListId.RIGHT_CLICK_SPEEDS;

        if (query.isEmpty())
        {
            for (Item item : BuiltInRegistries.ITEM)
            {
                if (isFiltered && item == Items.AIR)
                    continue;

                itemList.add(item.getDefaultInstance());
            }
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

            itemList.addAll(searchTree.search(query.toLowerCase(Locale.ROOT)));
        }

        this.filterItems(itemList);
    }

    /**
     * Refreshes the items that are found from updating the search box.
     * Vanilla item searching mechanics are used here.
     */
    public void refreshSearchResults()
    {
        this.resetRowList();
        this.addSearchedItems(this.selectableItems);
        this.generateContainers();
    }

    /**
     * Manage a filter entry for this list screen.
     * @param filter A new list filter enumeration value.
     */
    public void manageFilter(ListFilter filter, boolean state)
    {
        this.filters.remove(ListFilter.NONE);

        if (state)
            this.filters.add(filter);
        else
            this.filters.remove(filter);

        if (this.filters.size() == 0)
            this.filters.add(ListFilter.NONE);
    }

    /**
     * Filter out item stacks out of the given items list for this screen. The current list filter will be used.
     * @param allItems The list of items to filter through.
     */
    private void filterItems(NonNullList<ItemStack> allItems)
    {
        ArrayList<ItemStack> filteredItems = new ArrayList<>();

        for (ItemStack itemStack : allItems)
        {
            MaxSizeChanger injector = (MaxSizeChanger) itemStack.getItem();
            ItemStack copyStack = ItemCommonUtil.getItemStack(ItemCommonUtil.getResourceKey(itemStack.getItem()));

            boolean isUniqueItem = itemStack.getTag() != null && !itemStack.getTag().equals(copyStack.getTag());
            boolean isSizeExceeded = injector.NT$getOriginalSize() == 1 && this.listId == ListId.CUSTOM_ITEM_STACKING;

            if (isUniqueItem || isSizeExceeded)
            {
                filteredItems.add(itemStack);
                continue;
            }

            if (this.filters.contains(ListFilter.NONE))
                continue;

            Item item = itemStack.getItem();

            boolean isEdible = item.isEdible();
            boolean isTool = item instanceof DiggerItem || item instanceof SwordItem;
            boolean isBlock = item instanceof BlockItem;
            boolean isItem = !isTool && !isBlock;

            boolean isItemsFiltered = isItem && this.filters.contains(ListFilter.ITEMS);
            boolean isToolsFiltered = isTool && this.filters.contains(ListFilter.TOOLS);
            boolean isBlocksFiltered = isBlock && this.filters.contains(ListFilter.BLOCKS);

            if (this.onlyInclude == ListInclude.ONLY_EDIBLE)
            {
                if (!isEdible)
                    filteredItems.add(itemStack);
            }
            else if (isItemsFiltered || isToolsFiltered || isBlocksFiltered)
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
            this::getTutorialRows,
            ContainerId.LIST_HELP
        );

        ConfigRowGroup.ContainerRow savedItems = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.LIST_SAVED_ITEMS),
            this::getSavedRows,
            ContainerId.SAVED_ITEMS
        );

        ConfigRowGroup.ContainerRow defaultItems = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.LIST_DEFAULT_ITEMS),
            this::getRowsForDefaultItems,
            ContainerId.DEFAULT_ITEMS
        );

        ConfigRowGroup.ContainerRow selectableItems = new ConfigRowGroup.ContainerRow
        (
            Component.translatable(LangUtil.Gui.LIST_SELECTABLE_ITEMS),
            this::getRowsFromSelectableItems,
            ContainerId.SELECTABLE_ITEMS
        );

        this.getConfigRowList().addRow(tutorial.generate());
        this.getConfigRowList().addRow(savedItems.generate());

        if (this.getDefaultCount() > 0)
            this.getConfigRowList().addRow(defaultItems.generate());

        this.getConfigRowList().addRow(selectableItems.generate());
    }

    /**
     * @return Generates the tutorial paragraph for a container.
     */
    private ArrayList<ConfigRowList.Row> getTutorialRows()
    {
        return new TextGroup(Component.translatable(LangUtil.Gui.LIST_TUTORIAL)).generate();
    }

    /**
     * @return Generates the rows for a default entries container.
     */
    private ArrayList<ConfigRowList.Row> getRowsForDefaultItems()
    {
        Component title = Component.translatable(LangUtil.Gui.BUTTON_MANAGE_DEFAULTS);
        ControlButton button = new ControlButton(title, this::manageDefaults);
        ConfigRowList.Row manager = new ConfigRowBuild.SingleCenteredRow(button).generate();
        ArrayList<ConfigRowList.Row> rows = new ArrayList<>();

        if (this.getSearchBox().getValue().isEmpty())
            rows.add(manager);

        rows.addAll(this.getDefaultRows());

        return rows;
    }

    /**
     * Get rows that contain items from within the game. The buttons within these rows are clickable and will bring
     * up a new overlay window with options for the user. The selectable buttons will be filtered based on the current
     * list filters and search query.
     *
     * @return A list of config row list rows.
     */
    private ArrayList<ConfigRowList.Row> getRowsFromSelectableItems() { return new ItemGroup(this).generate(); }

    /**
     * Functional consumer that accepts an ignored button and opens a new default entries overlay window.
     * @param button A button widget.
     */
    private void manageDefaults(Button button) { new DefaultEntriesOverlay(); }

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
            if (row.getResourceKey().equals(ItemCommonUtil.getResourceKey(itemStack.getItem())))
            {
                this.getConfigRowList().setScrollOn(row);
                break;
            }
        }

        if (this.highlightItem == itemStack)
            this.highlightItem = null;
    }

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
        this.scrollAmountCache = this.getConfigRowList().getScrollAmount();

        this.init(minecraft, width, height);
        this.getSearchBox().setValue(query);

        if (!query.isEmpty())
        {
            this.refreshSearchResults();
            ContainerButton.collapse(ContainerId.LIST_HELP);
        }

        if (Overlay.isOpened())
            this.getSearchBox().setFocused(false);

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

        if (Overlay.getVisible() instanceof PermissionLostOverlay)
            return false;

        if (Overlay.getVisible() != null)
        {
            if (isEsc)
                Overlay.close();

            return true;
        }

        if (KeyUtil.isSearching(keyCode))
        {
            if (this.getSearchBox().isFocused())
            {
                this.getSearchBox().setValue("");
                this.refreshSearchResults();
            }

            this.getSearchBox().setFocused(true);

            return true;
        }

        if (this.getSearchBox().keyPressed(keyCode, scanCode, modifiers))
        {
            if (!this.getSearchBox().getValue().equals(query))
                this.refreshSearchResults();

            return true;
        }

        if (this.getSearchBox().isFocused() && this.getSearchBox().isVisible())
        {
            if (isEsc)
                this.getSearchBox().setFocused(false);

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
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    private void renderWidget(Renderable widget, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
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
                boolean isLevelAbsent = this.minecraft.level == null;
                boolean isItemIneligible = false;
                boolean isItemListed = false;

                if (this.minecraft.player != null)
                {
                    ItemStack itemStack = this.minecraft.player.getMainHandItem();
                    isItemIneligible = !this.isItemEligible(itemStack);
                    isItemListed = this.isItemSaved(itemStack);
                }

                if (isLevelAbsent || isItemIneligible || isItemListed)
                    this.widgetProvider.autoButton.active = false;

                // List inclusion check
                if (this.onlyInclude != ListInclude.ALL)
                    this.widgetProvider.filterButton.active = false;
            }
        }

        // Save button is only active when something can be saved
        Button save = this.widgetProvider.saveButton;

        if (widget instanceof Button button && button.getX() == save.getX() && button.getY() == save.getY())
            button.active = !Overlay.isOpened() && this.isListSavable();

        // Render the widget to the screen
        widget.render(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Override for rendering the list screen.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        // Background rendering
        if (this.minecraft.level != null)
            graphics.fillGradient(0, 0, this.width, this.height, 839913488, 16777216);
        else
            this.renderDirtBackground(graphics);

        graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        graphics.fillGradient(0, 0, this.width, this.height, 1744830464, 1744830464);

        // Reset scrollbar position without flashing
        if (this.scrollAmountCache > 0.0D)
        {
            this.getConfigRowList().render(graphics, mouseX, mouseY, partialTick);
            this.getConfigRowList().setScrollAmount(this.scrollAmountCache);
            this.scrollAmountCache = 0.0D;
        }

        // Row list rendering
        this.getConfigRowList().render(graphics, mouseX, mouseY, partialTick);
        this.jumpToEntry(this.highlightItem);

        // Widget rendering
        for (Renderable widget : this.listWidgets)
            this.renderWidget(widget, graphics, mouseX, mouseY, partialTick);

        // Mouse overlap highlight
        for (Renderable widget : this.listWidgets)
        {
            if (widget instanceof OverlapButton button && button.isMouseOver(mouseX, mouseY))
                widget.render(graphics, mouseX, mouseY, partialTick);
        }

        // Screen title rendering
        graphics.drawCenteredString(this.font, this.title.getString(), this.width / 2, 8, 0xFFFFFF);

        // Permission monitor
        Overlay overlay = Overlay.getVisible();

        if (this.isPermissionWatched() && !NetUtil.isPlayerOp())
        {
            if (ClassUtil.isNotInstanceOf(overlay, PermissionLostOverlay.class))
                new PermissionLostOverlay();
        }
        else if (this.isPermissionWatched() && NetUtil.isPlayerOp() && overlay instanceof PermissionLostOverlay)
            Overlay.close();

        // Overlay rendering
        Overlay.render(graphics, mouseX, mouseY, partialTick);

        // Render last runners
        if (!Overlay.isOpened())
            this.renderLast.forEach(Runnable::run);
        else
        {
            // Ensure the search box is not focused
            this.widgetProvider.searchBox.setFocused(false);

            // Translate on the z-axis for tooltips
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.0D, 400.0D);

            this.renderOverlayTooltips.forEach(tooltip -> tooltip.accept(graphics, mouseX, mouseY));

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
                ListScreen.this.closeList(true);
            else
                ListScreen.this.minecraft.setScreen(ListScreen.this);
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

        private int getSmallWidth() { return Math.min(200, (ListScreen.this.width - 50 - 12) / 3); }

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
         * All widgets are added to the {@link ListScreen#listWidgets} list. Any widget setup or handling must be
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

            Set<Renderable> children = new HashSet<>
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

            ListId listId = ListScreen.this.getListId();

            if (listId == ListId.LEFT_CLICK_SPEEDS || listId == ListId.RIGHT_CLICK_SPEEDS)
                children.add(this.swingButton);

            ListScreen.this.listWidgets.addAll(children);
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
                ListScreen.this.font,
                ListScreen.this.width / 2 - 112,
                SEARCH_TOP_Y,
                SEARCH_BOX_W,
                SEARCH_BOX_H,
                Component.empty()
            );
        }

        /**
         * Functional shortcut for when the save button is clicked.
         * @param button A button instance.
         */
        private void onSave(Button button)
        {
            ListScreen.this.closeList(false);

            TweakServerCache<?> serverTweak = ListScreen.this.list.getTweak().getServerCache();
            boolean isServerTweak = serverTweak != null;
            boolean isMultiplayer = NostalgicTweaks.isNetworkVerified() && NetUtil.isMultiplayer();

            if (isServerTweak && isMultiplayer)
            {
                PacketUtil.sendToServer(new PacketC2SChangeTweak(serverTweak));
                ToastNotification.sentChanges();
            }
            else
                AutoConfig.getConfigHolder(ClientConfig.class).save();
        }

        /**
         * The save button is active when changes have been made. When pressed, all changes are saved to disk.
         * @return A new button instance.
         */
        private Button createSaveButton()
        {
            return Button.builder(Component.translatable(LangUtil.Gui.BUTTON_SAVE_AND_DONE), this::onSave)
                .pos(ListScreen.this.width / 2 + 3, ListScreen.this.height - SettingsScreen.DONE_BUTTON_TOP_OFFSET)
                .size(this.getSmallWidth(), SettingsScreen.BUTTON_HEIGHT)
                .build()
            ;
        }

        /**
         * The cancel button is always active, and when pressed, cancels any changes made to the list.
         * A confirmation window is presented before leaving the list screen.
         *
         * @return A new button instance.
         */
        private Button createCancelButton()
        {
            return Button.builder(Component.translatable(LangUtil.Vanilla.GUI_CANCEL), (button) -> ListScreen.this.exitList())
                .pos(ListScreen.this.width / 2 - this.getSmallWidth() - 3, ListScreen.this.height - SettingsScreen.DONE_BUTTON_TOP_OFFSET)
                .size(this.getSmallWidth(), SettingsScreen.BUTTON_HEIGHT)
                .build()
            ;
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
                this.searchBox.getX() - 61,
                this.searchBox.getY() - 1,
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
                this.searchBox.getX() - 42,
                this.searchBox.getY() - 1,
                (button) -> new FilterListOverlay()
            );
        }

        /**
         * This button quickly adds the item in the player's hand.
         * @return A state button widget.
         */
        private StateButton createAutoButton()
        {
            return new StateButton(StateWidget.LIGHTNING, this.searchBox.getX() - 23, this.searchBox.getY() - 1, (button) ->
            {
                if (ListScreen.this.minecraft.player != null)
                {
                    this.searchBox.setValue("");
                    this.searchBox.setFocused(false);

                    ItemStack itemStack = ListScreen.this.minecraft.player.getMainHandItem();

                    ListScreen.this.addItem(itemStack);
                    ListScreen.this.refreshSearchResults();
                    ListScreen.this.highlightItem(itemStack);
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

            return new StateButton(StateWidget.CLEAR, search.getX() + search.getWidth() + 3, search.getY() - 1, (button) ->
            {
                search.setValue("");
                search.setFocused(true);
                ListScreen.this.refreshSearchResults();
            });
        }

        /**
         * This button will open an informative swing speed pop-up overlay.
         * @return A state button widget.
         */
        private StateButton createSwingButton()
        {
            EditBox search = this.searchBox;

            return new StateButton(StateWidget.SWING, search.getX() + search.getWidth() + 22, search.getY() - 1, (button) ->
            {
                ListId listId = ListScreen.this.getListId();

                if (listId == ListId.LEFT_CLICK_SPEEDS || listId == ListId.RIGHT_CLICK_SPEEDS)
                    new SpeedOverlay();
            });
        }
    }
}
