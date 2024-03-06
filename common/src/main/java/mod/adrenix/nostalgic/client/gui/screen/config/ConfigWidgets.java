package mod.adrenix.nostalgic.client.gui.screen.config;

import mod.adrenix.nostalgic.client.gui.screen.WidgetManager;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage.ManageOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.DescriptionRow;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.GroupRow;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.RowProvider;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.TweakRow;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.tab.TabButton;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.input.GenericInput;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import mod.adrenix.nostalgic.util.client.search.SearchTag;
import mod.adrenix.nostalgic.util.client.search.TweakDatabase;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.function.ForEachWithPrevious;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConfigWidgets implements WidgetManager
{
    /* Constants */

    public static final int TAB_CONTROLLER_SIZE = 14;
    public static final int BOTTOM_OFFSET = 21;
    public static final int LIST_START_Y = 21;

    /* Fields */

    private String lastQuery;
    private final ConfigScreen configScreen;
    private final LinkedHashSet<TabButton> tabs = new LinkedHashSet<>();
    private final HashMap<Tweak<?>, CompletableFuture<TweakRow>> searchMap = new HashMap<>();

    /* Constructor */

    ConfigWidgets(ConfigScreen configScreen)
    {
        RowProvider.DEFAULT.use();

        this.configScreen = configScreen;
        this.lastQuery = "";
    }

    /* Widgets */

    private ButtonWidget manage;
    private ButtonWidget finish;
    private ButtonWidget save;
    private ButtonWidget all;
    private ButtonWidget favorite;
    private ButtonWidget tabLeft;
    private ButtonWidget tabRight;
    private RowList rowList;
    private GenericInput search;
    private SeparatorWidget topSeparator;
    private SeparatorWidget bottomSeparator;

    /* Getters */

    @PublicAPI
    public GenericInput getSearch()
    {
        return this.search;
    }

    @PublicAPI
    public RowList getRowList()
    {
        return this.rowList;
    }

    @PublicAPI
    public ButtonWidget getAll()
    {
        return this.all;
    }

    @PublicAPI
    public LinkedHashSet<TabButton> getTabs()
    {
        return this.tabs;
    }

    @PublicAPI
    public ButtonWidget getTabLeft()
    {
        return this.tabLeft;
    }

    @PublicAPI
    public ButtonWidget getTabRight()
    {
        return this.tabRight;
    }

    @PublicAPI
    public ButtonWidget getFavorite()
    {
        return this.favorite;
    }

    @PublicAPI
    public SeparatorWidget getTopSeparator()
    {
        return this.topSeparator;
    }

    @PublicAPI
    public SeparatorWidget getBottomSeparator()
    {
        return this.bottomSeparator;
    }

    /* Methods */

    @Override
    public void init()
    {
        RowProvider.DEFAULT.setPredicate(Tweak::isNotIgnored);

        this.rowList = this.createRowList();
        this.save = this.createSaveButton();
        this.tabLeft = this.createTabLeft();
        this.tabRight = this.createTabRight();
        this.manage = this.createManageButton();
        this.favorite = this.createFavoriteButton();
        this.all = this.createAllButton();
        this.finish = this.createFinishButton();
        this.search = this.createSearch();
        this.topSeparator = this.createTopSeparator();
        this.bottomSeparator = this.createBottomSeparator();

        Container.CATEGORIES.forEach(this::createTabFromCategory);

        this.createSearchMap();
        this.checkTabs();
        this.centerTabs();
        this.populateRowList();

        this.tabLeft.setTabOrderGroup(0);
        this.tabRight.setTabOrderGroup(0);
        this.tabs.forEach(tab -> tab.setTabOrderGroup(1));
        this.rowList.setTabOrderGroup(2);
        this.save.setTabOrderGroup(3);
        this.manage.setTabOrderGroup(4);
        this.favorite.setTabOrderGroup(5);
        this.all.setTabOrderGroup(6);
        this.search.setTabOrderGroup(7);
        this.finish.setTabOrderGroup(8);
    }

    /**
     * Manually set the search query value.
     *
     * @param query A string value.
     */
    public void setQuery(String query)
    {
        this.search.setInput(query);
        this.updateSearchResults();
    }

    /**
     * @return The current string value set in the input box.
     */
    public String getQuery()
    {
        return this.search.getInput();
    }

    /**
     * Creates {@link CompletableFuture} instances for tweak rows that are used in search results. Rebuilding tweak rows
     * for every new search query is an expensive operation. Caching pre-built tweak rows that generate with each widget
     * manager instance greatly speeds up performance.
     */
    private void createSearchMap()
    {
        TweakPool.stream()
            .forEach(tweak -> this.searchMap.put(tweak, CompletableFuture.supplyAsync(() -> TweakRow.create(tweak, this.rowList)
                .indent(0)
                .build(), Util.backgroundExecutor())));
    }

    /**
     * Get a search result tweak row linked to the provided tweak. This should be used over creating a new tweak row
     * instance since this method uses a pre-built cache map of completable futures.
     *
     * @param tweak A {@link Tweak} instance.
     * @return A {@link TweakRow} instance.
     */
    private TweakRow getSearchRow(Tweak<?> tweak)
    {
        return this.searchMap.get(tweak).join();
    }

    /**
     * Add a tab button.
     *
     * @param tab The tab button to subscribe.
     */
    private void addTab(TabButton tab)
    {
        this.configScreen.addWidget(tab);
        this.tabs.add(tab);
    }

    /**
     * Populate the row list based on the current row provider.
     */
    public void populateFromProvider()
    {
        for (RowProvider provider : RowProvider.values())
        {
            if (provider.isProviding())
            {
                switch (provider)
                {
                    case ALL -> this.populateFromAll();
                    case FAVORITE -> this.populateFromFavorite();
                    case DEFAULT -> this.populateRowList();
                }

                return;
            }
        }
    }

    /**
     * Add rows to the row list based on the current selected category.
     */
    public void populateRowList()
    {
        if (!this.lastQuery.isEmpty())
        {
            RowProvider.DEFAULT.useAndThen(this::updateSearchResults);
            return;
        }

        RowProvider.DEFAULT.useAndThen(this.rowList::clear);

        Container.CATEGORIES.stream()
            .filter(this.configScreen.getCategory()::equals)
            .findFirst()
            .ifPresent(this::populateFromCategory);
    }

    /**
     * Creates group rows and tweak rows associated with what is saved in the user's favorite list.
     */
    private void populateFromFavorite()
    {
        if (this.lastQuery.isEmpty())
            this.populateFromPredicate(RowProvider.FAVORITE.useAndGetPredicate());
        else
            RowProvider.FAVORITE.useAndThen(this::updateSearchResults);
    }

    /**
     * Create tweak rows and group rows for every tweak and group within the mod.
     */
    private void populateFromAll()
    {
        if (this.lastQuery.isEmpty())
            this.populateFromPredicate(RowProvider.ALL.useAndGetPredicate());
        else
            RowProvider.ALL.useAndThen(this::updateSearchResults);
    }

    /**
     * Creates group rows and tweak rows associated with the given category.
     *
     * @param category A tweak category instance.
     */
    private void populateFromCategory(Container category)
    {
        if (category.getDescription().isPresent())
            this.rowList.addBottomRow(DescriptionRow.create(category, this.rowList).build());

        category.getTweaks()
            .stream()
            .filter(Tweak::isTop)
            .forEach(tweak -> this.rowList.addBottomRow(TweakRow.create(tweak, this.rowList).build()));

        Container.GROUPS.stream()
            .filter(group -> group.getParent().stream().anyMatch(parent -> parent.equals(category)))
            .forEachOrdered(group -> this.rowList.addBottomRow(GroupRow.create(group, this.rowList).build()));

        CollectionUtil.filterOut(category.getTweaks(), Tweak::isTop)
            .forEach(tweak -> this.rowList.addBottomRow(TweakRow.create(tweak, this.rowList).build()));
    }

    /**
     * Repopulates the row list based on the given tweak predicate.
     *
     * @param predicate A {@link Predicate} that accepts a {@link Tweak}.
     */
    private void populateFromPredicate(Predicate<Tweak<?>> predicate)
    {
        this.rowList.clear();

        Container.CATEGORIES.forEach(category -> category.getTweaks()
            .stream()
            .filter(predicate)
            .forEach(tweak -> this.rowList.addBottomRow(TweakRow.create(tweak, this.rowList).build())));

        ArrayList<Container> matchedGroups = new ArrayList<>();
        ArrayList<Container> matchedFromCategory = new ArrayList<>();

        Container.GROUPS.stream()
            .filter(Container::isGroup)
            .filter(group -> group.getTweaks().stream().anyMatch(predicate))
            .forEachOrdered(matchedGroups::add);

        matchedGroups.forEach(group -> group.getGroupSetFromCategory()
            .stream()
            .filter(Container::isGroup)
            .findFirst()
            .ifPresent(matchedFromCategory::add));

        matchedFromCategory.stream()
            .distinct()
            .forEach(container -> this.rowList.addBottomRow(GroupRow.create(container, this.rowList).build()));
    }

    /**
     * Create tweak rows from the current search input. If group rows or tweak rows are not appearing properly within
     * the row list, then this method is most likely to blame due to a logical error.
     */
    private void populateFromSearch(String query)
    {
        RowProvider.DEFAULT.setPredicate(Tweak::isNotIgnored);

        if (this.search == null)
            return;

        if (query.isEmpty())
        {
            if (ConfigScreen.SCREEN_CACHE.isPushed())
                ConfigScreen.SCREEN_CACHE.pop(this.configScreen);
            else
                this.populateFromProvider();

            this.lastQuery = query;

            return;
        }

        if (query.equals(this.lastQuery))
            return;
        else
            this.lastQuery = query;

        RowProvider.DEFAULT.setPredicate(tweak -> {
            if (tweak.isIgnored())
                return false;

            return tweak.getCategory().equals(this.configScreen.getCategory());
        });

        this.rowList.clear();

        if (SearchTag.isInvalid(query))
            return;

        this.findAndPopulateList(query);

        if (this.rowList.getVisibleRows().isEmpty() && !RowProvider.ALL.isProviding() && query.length() > 1)
            RowProvider.ALL.useAndThen(() -> this.findAndPopulateList(query));
    }

    /**
     * Add rows to the row list based on the results found from the given query.
     */
    private void findAndPopulateList(String query)
    {
        for (Tweak<?> tweak : TweakDatabase.getInstance().findValues(query, 0.08D))
        {
            if (RowProvider.get().test(tweak))
                this.rowList.addBottomRow(this.getSearchRow(tweak));
        }
    }

    /**
     * Alternative method to {@link #populateFromSearch(String)}. This will forcibly update the search results and
     * bypass query caching.
     */
    private void updateSearchResults()
    {
        if (this.lastQuery.isEmpty() || this.lastQuery.equals(this.getQuery()))
            return;

        this.lastQuery = "";
        this.populateFromSearch(this.getQuery());
    }

    /**
     * Create a new tab based on the position data from the last created tab.
     *
     * @param rightOf  The previously created tab button.
     * @param category The category container this tab button is associated with.
     */
    private void createTabRightOf(DynamicWidget<?, ?> rightOf, Container category)
    {
        this.addTab(TabButton.create(this.configScreen, category).pos(rightOf.getEndX() + 1, 1).build());
    }

    /**
     * Create a new tab button with the given category container.
     *
     * @param category A tweak category container instance.
     */
    private void createTabFromCategory(Container category)
    {
        Consumer<TabButton> createTabFromPrev = (prevTab) -> this.createTabRightOf(prevTab, category);
        Runnable createFirstTab = () -> this.createTabRightOf(this.tabRight, category);

        CollectionUtil.last(this.tabs).ifPresentOrElse(createTabFromPrev, createFirstTab);
    }

    /**
     * Check if any tabs are overflowing off the screen. If any are, then their {@link TabButton#setHiddenRight()} flag
     * is activated.
     */
    private void checkTabs()
    {
        this.tabs.stream().filter(TabButton::isOverflow).forEach(TabButton::setHiddenRight);
    }

    /**
     * Set the next tabs x-position based on the previous tab's x-position.
     *
     * @param prev The previous tab button to get an ending x-position from.
     * @param next The next tab button to set a new starting x-position.
     */
    private void setTabPositionFromPrev(TabButton prev, TabButton next)
    {
        next.setX(prev.getEndX() + 1);
    }

    /**
     * Realign tabs by aligning the first visible tab after the right tab controller button. The previous tab afterward
     * will align every other visible tab.
     */
    private void realignTabs()
    {
        this.tabs.stream().filter(TabButton::isVisible).findFirst().ifPresent(TabButton::setAtStartPosition);

        ForEachWithPrevious.create(this.tabs.stream().filter(TabButton::isVisible))
            .forEach(this::setTabPositionFromPrev)
            .run();

        this.checkTabs();
    }

    /**
     * If none of the tabs are overflowing off the screen, then the tab group is centered. Otherwise, any overflowing
     * tabs will prevent this method from running.
     */
    private void centerTabs()
    {
        if (this.isTabOverflow())
            return;

        int size = this.tabs.stream().mapToInt(TabButton::getWidth).sum() + this.tabs.size() - 1;
        int maxSize = this.configScreen.width;

        this.tabs.stream().findFirst().ifPresent(tab -> tab.setX(Math.round(MathUtil.center(size, maxSize))));

        ForEachWithPrevious.create(this.tabs).forEach(this::setTabPositionFromPrev).run();
    }

    /**
     * Select the first tab in the tab cache.
     */
    private void selectFirstTab()
    {
        this.tabs.stream().findFirst().ifPresent(TabButton::select);

        while (!this.isLeftSatisfied())
            this.moveTabsRight();
    }

    /**
     * Select the last tab in the tab cache.
     */
    private void selectLastTab()
    {
        CollectionUtil.last(this.tabs.stream()).ifPresent(TabButton::select);

        while (!this.isRightSatisfied())
            this.moveTabsLeft();
    }

    /**
     * Move to and select the next tab to the right of the currently selected tab.
     */
    public void selectTabRight()
    {
        ForEachWithPrevious.create(this.tabs)
            .returnNextWhenPrev(TabButton::isSelected)
            .run()
            .ifPresentOrElse(TabButton::select, this::selectFirstTab);

        if (this.tabs.stream().anyMatch(TabButton::isRealignNeeded))
            this.moveTabsLeft();
    }

    /**
     * Move to and select the next tab to the left of the currently selected tab.
     */
    public void selectTabLeft()
    {
        ForEachWithPrevious.create(this.tabs)
            .returnPrevWhenNext(TabButton::isSelected)
            .run()
            .ifPresentOrElse(TabButton::select, this::selectLastTab);

        if (this.tabs.stream().anyMatch(TabButton::isRealignNeeded))
            this.moveTabsRight();
    }

    /**
     * @return Whether there are tabs overflowing the game window.
     */
    private boolean isTabOverflow()
    {
        return this.tabs.stream().anyMatch(TabButton::isOverflow) || this.tabs.stream()
            .anyMatch(TabButton::isHiddenLeft);
    }

    /**
     * @return Whether there are any tabs overflowing to the left.
     */
    private boolean isLeftSatisfied()
    {
        return CollectionUtil.first(this.tabs).filter(TabButton::isHiddenLeft).isEmpty();
    }

    /**
     * @return Whether there are any tabs overflowing to the right.
     */
    private boolean isRightSatisfied()
    {
        return CollectionUtil.last(this.tabs).filter(TabButton::isHiddenRight).isEmpty();
    }

    /**
     * Move tabs right to the left. When the right [->] button is clicked, tabs should move to the left.
     */
    public void moveTabsLeft()
    {
        if (this.isRightSatisfied())
            return;

        this.tabs.stream().filter(TabButton::isVisible).findFirst().ifPresent(TabButton::setHiddenLeft);
        this.tabs.stream().filter(TabButton::isHiddenRight).findFirst().ifPresent(TabButton::setVisibleRight);

        this.realignTabs();
    }

    /**
     * Move tabs left to the right. When the left [<-] button is clicked, tabs should move to the right.
     */
    public void moveTabsRight()
    {
        if (this.isLeftSatisfied())
            return;

        CollectionUtil.last(this.tabs.stream().filter(TabButton::isHiddenLeft)).ifPresent(TabButton::setVisibleLeft);
        CollectionUtil.last(this.tabs.stream().filter(TabButton::isVisible)).ifPresent(TabButton::setHiddenRight);

        this.realignTabs();
    }

    /**
     * The tab left button will move the tab categories to the left.
     *
     * @return A {@link ButtonWidget} instance.
     */
    private ButtonWidget createTabLeft()
    {
        return ButtonWidget.create(Component.literal("←"))
            .pos(1, 6)
            .size(TAB_CONTROLLER_SIZE)
            .renderer(TabButton::arrowRenderer)
            .onPress(this::moveTabsRight)
            .visibleIf(this::isTabOverflow)
            .disableIf(this::isLeftSatisfied)
            .build(this.configScreen::addWidget);
    }

    /**
     * The tab right button will move the tab categories to the right.
     *
     * @return A {@link ButtonWidget} instance.
     */
    private ButtonWidget createTabRight()
    {
        return ButtonWidget.create(Component.literal("→"))
            .rightOf(this.tabLeft, 1)
            .size(TAB_CONTROLLER_SIZE)
            .renderer(TabButton::arrowRenderer)
            .onPress(this::moveTabsLeft)
            .visibleIf(this::isTabOverflow)
            .disableIf(this::isRightSatisfied)
            .build(this.configScreen::addWidget);
    }

    /**
     * @return The {@link Component} for an empty row list.
     */
    private Component getEmptyMessage()
    {
        Component message;

        if (RowProvider.FAVORITE.isProviding())
            message = Lang.Listing.EMPTY_FAVORITES.get();
        else
            message = Lang.Listing.NOTHING_FOUND.get();

        if (this.search != null)
        {
            if (this.search.isFocused())
                message = Lang.Listing.NOTHING_FOUND.get();

            if (SearchTag.isInvalid(this.search.getInput()))
                message = Lang.Input.INVALID_TAG.get(this.search.getInput());
        }

        return message;
    }

    /**
     * @return Create a new row list instance.
     */
    private RowList createRowList()
    {
        return RowList.create()
            .pos(0, LIST_START_Y)
            .useHighlightsWhen(ModTweak.DISPLAY_ROW_HIGHLIGHT::fromCache)
            .backgroundOpacity(ModTweak.MENU_BACKGROUND_OPACITY::fromCache)
            .overrideHighlights(ModTweak.OVERRIDE_ROW_HIGHLIGHT::fromCache, ModTweak.ROW_HIGHLIGHT_OPACITY::fromCache)
            .extendHeightToScreenEnd(BOTTOM_OFFSET + 2)
            .extendWidthToScreenEnd(0)
            .renderTopAndBottomDirt()
            .emptyMessage(this::getEmptyMessage)
            .build(this.configScreen::addWidget);
    }

    /**
     * @return The icon for the finish button.
     */
    private TextureIcon getFinishIcon()
    {
        return this.configScreen.isSavable() ? Icons.RED_X : Icons.GREEN_CHECK;
    }

    /**
     * @return The title for the finish button.
     */
    private Component getFinishTitle()
    {
        return this.configScreen.isSavable() ? Lang.Vanilla.GUI_CANCEL.get() : Lang.Vanilla.GUI_DONE.get();
    }

    /**
     * @return The finish button.
     */
    private ButtonWidget createFinishButton()
    {
        return ButtonWidget.create()
            .posY(this.configScreen.height - BOTTOM_OFFSET)
            .onPress(this.configScreen::onClose)
            .icon(this::getFinishIcon)
            .title(this::getFinishTitle)
            .tooltip(this::getFinishTitle, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.FINISH, 45)
            .fromScreenEndX(1)
            .skipFocusOnClick()
            .useTextWidth()
            .padding(5)
            .build(this.configScreen::addWidget);
    }

    /**
     * The save button will exit the config screen while also saving any changes.
     *
     * @return A save button.
     */
    private ButtonWidget createSaveButton()
    {
        return ButtonWidget.create(Lang.Button.SAVE)
            .pos(1, this.configScreen.height - BOTTOM_OFFSET)
            .icon(Icons.SAVE_FLOPPY)
            .tooltip(Lang.Button.SAVE, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.SAVE, 45)
            .skipFocusOnClick()
            .useTextWidth()
            .padding(5)
            .onPress(this.configScreen::saveConfig)
            .enableIf(this.configScreen::isSavable)
            .build(this.configScreen::addWidget);
    }

    /**
     * The manage button will pop up a new overlay screen with config management options.
     *
     * @return A config management button.
     */
    private ButtonWidget createManageButton()
    {
        return ButtonWidget.create(Lang.Button.MANAGE)
            .onPress(() -> new ManageOverlay().open())
            .tooltip(Lang.Button.MANAGE, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.MANAGE, 45)
            .icon(Icons.MECHANICAL_TOOLS)
            .rightOf(this.save, 1)
            .skipFocusOnClick()
            .useTextWidth()
            .padding(5)
            .build(this.configScreen::addWidget);
    }

    /**
     * The favorite button will display any favorite tweaks.
     *
     * @return A button wrapper instance.
     */
    private ButtonWidget createFavoriteButton()
    {
        return ButtonWidget.create(Lang.Button.FAVORITE)
            .skipFocusOnClick()
            .useTextWidth()
            .padding(5)
            .icon(Icons.STAR_ON)
            .tooltip(Lang.Button.FAVORITE, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(RowProvider.FAVORITE::getInfoTooltip, 45)
            .disableIf(RowProvider.FAVORITE::isProviding)
            .onPress(this::populateFromFavorite)
            .rightOf(this.manage, 1)
            .build(this.configScreen::addWidget);
    }

    /**
     * The view all button will display all tweaks within the mod.
     *
     * @return A button wrapper instance.
     */
    private ButtonWidget createAllButton()
    {
        return ButtonWidget.create(Lang.Button.SEE_ALL)
            .padding(5)
            .useTextWidth()
            .skipFocusOnClick()
            .icon(Icons.BOOK_OPEN)
            .tooltip(Lang.Button.SEE_ALL, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(RowProvider.ALL::getInfoTooltip, 45)
            .disableIf(RowProvider.ALL::isProviding)
            .onPress(this::populateFromAll)
            .rightOf(this.favorite, 1)
            .build(this.configScreen::addWidget);
    }

    /**
     * Check if buttons need resized so that the search widget is not too small.
     */
    private void resizeSearch(GenericInput search)
    {
        if (search.getWidth() > 180)
            return;

        this.favorite.shrink();
        this.all.shrink();

        search.getBuilder().sync();
    }

    /**
     * The config search bar will filter config results using the options set by the user.
     *
     * @return An input widget instance.
     */
    private GenericInput createSearch()
    {
        return GenericInput.create()
            .icon(Icons.SEARCH)
            .whenEmpty(Lang.Input.SEARCH)
            .background(Color.OLIVE_BLACK, Color.OLIVE_BLACK)
            .border(Color.BLACK, Color.WHITE)
            .maxLength(100)
            .searchShortcut()
            .rightOf(this.all, 1)
            .extendWidthTo(this.finish, 1)
            .afterSync(this::resizeSearch)
            .whenFocused(this::updateSearchResults)
            .onInput(this::populateFromSearch)
            .build(this.configScreen::addWidget);
    }

    /**
     * The separator bar that splits the top tabs and the row list. This will only be visible of no tab widgets are
     * selected.
     *
     * @return A separator widget instance.
     */
    private SeparatorWidget createTopSeparator()
    {
        return SeparatorWidget.create(Color.SILVER_CHALICE)
            .visibleIf(() -> this.tabs.stream().noneMatch(TabButton::isSelected))
            .extendWidthToScreenEnd(0)
            .height(1)
            .below(this.tabLeft, 0)
            .build(this.configScreen::addWidget);
    }

    /**
     * The separator bar that splits the manager buttons and the row list.
     *
     * @return A separator widget instance.
     */
    private SeparatorWidget createBottomSeparator()
    {
        return SeparatorWidget.create(Color.SILVER_CHALICE)
            .extendWidthToScreenEnd(0)
            .height(1)
            .above(this.save, 1)
            .build(this.configScreen::addWidget);
    }
}
