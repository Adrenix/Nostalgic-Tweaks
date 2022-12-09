package mod.adrenix.nostalgic.client.config.gui.overlay;

import mod.adrenix.nostalgic.client.config.gui.overlay.template.AbstractWidgetProvider;
import mod.adrenix.nostalgic.client.config.gui.overlay.template.ListScreenOverlay;
import mod.adrenix.nostalgic.client.config.gui.screen.list.ListFilter;
import mod.adrenix.nostalgic.client.config.gui.widget.ToggleCheckbox;
import mod.adrenix.nostalgic.util.common.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.Set;

/**
 * This class provides checkbox options for further filtering an abstract list screen. Some filtering may already be
 * predefined by an abstract list.
 */

public class FilterListOverlay extends ListScreenOverlay<FilterListOverlay.WidgetProvider>
{
    /* Static Fields */

    public static final int OVERLAY_WIDTH = 176;
    public static final int OVERLAY_HEIGHT = 98;

    /* Overlay Fields */

    private boolean toolFiltered;
    private boolean itemFiltered;
    private boolean blockFiltered;

    /* Constructor & Initialize */

    /**
     * Start a new filter list overlay window instance.
     */
    public FilterListOverlay()
    {
        super(Component.translatable(LangUtil.Gui.OVERLAY_FILTER), OVERLAY_WIDTH, OVERLAY_HEIGHT);

        this.setHeightPadding(38);
        this.setBackground(0xFF303030);
        this.init();
    }

    /**
     * Sets up overlay fields based on current game window properties.
     */
    @Override
    public void init()
    {
        int textWidth = Minecraft.getInstance().font.width(Component.translatable(LangUtil.Gui.OVERLAY_FILTER));
        this.width = Math.max(135, textWidth + 44);

        super.init();
    }

    /* List Overlay Overrides */

    @Override
    protected void createWidgetProvider() { this.widgetProvider = new WidgetProvider(); }

    /* Getters */

    /**
     * @return Whether tool items should be filtered in the current abstract list screen.
     */
    public boolean isToolFiltered() { return this.toolFiltered; }

    /**
     * @return Whether regular items should be filtered in the current abstract list screen.
     */
    public boolean isItemFiltered() { return this.itemFiltered; }

    /**
     * @return Whether block items should be filtered in the current abstract list screen.
     */
    public boolean isBlockFiltered() { return this.blockFiltered; }

    /* Setters */

    /**
     * Change the tool filter flag.
     * @param state The new flag state.
     */
    public void setToolFiltered(boolean state)
    {
        this.toolFiltered = state;

        this.listScreen.manageFilter(ListFilter.TOOLS, state);
        this.listScreen.refreshSearchResults();
    }

    /**
     * Change the item filter tag.
     * @param state The new flag state.
     */
    public void setItemFiltered(boolean state)
    {
        this.itemFiltered = state;

        this.listScreen.manageFilter(ListFilter.ITEMS, state);
        this.listScreen.refreshSearchResults();
    }

    /**
     * Change the block filter tag.
     * @param state The new block state.
     */
    public void setBlockFiltered(boolean state)
    {
        this.blockFiltered = state;

        this.listScreen.manageFilter(ListFilter.BLOCKS, state);
        this.listScreen.refreshSearchResults();
    }

    /* Widget Provider */

    /**
     * This class is responsible for creating and adding widgets to the overlay.
     * There will be three checkbox options that can further refine an abstract list screen's category filtering.
     */
    protected class WidgetProvider extends AbstractWidgetProvider
    {
        /* Widgets */

        public ToggleCheckbox toolFilter;
        public ToggleCheckbox itemFilter;
        public ToggleCheckbox blockFilter;

        /* Methods */

        /**
         * Create and add widgets to the item manager overlay window.
         */
        public void generate()
        {
            this.toolFilter = this.createToolFilter();
            this.itemFilter = this.createItemFilter();
            this.blockFilter = this.createBlockFilter();

            FilterListOverlay.this.widgets.add(this.toolFilter);
            FilterListOverlay.this.widgets.add(this.itemFilter);
            FilterListOverlay.this.widgets.add(this.blockFilter);

            this.children = Set.of(toolFilter, itemFilter, blockFilter);
        }

        /**
         * This checkbox changes whether tool items should be filtered out of both categories.
         * @return A toggle checkbox instance.
         */
        public ToggleCheckbox createToolFilter()
        {
            return new ToggleCheckbox
            (
                FilterListOverlay.this.getOverlayStartX(),
                FilterListOverlay.this.getOverlayStartY() + 2,
                Component.translatable(LangUtil.Gui.OVERLAY_FILTER_TOOL),
                Component.translatable(LangUtil.Gui.OVERLAY_FILTER_TOOL_TOOLTIP),
                FilterListOverlay.this::isToolFiltered,
                FilterListOverlay.this::setToolFiltered
            );
        }

        /**
         * This checkbox changes whether regular items should be filtered out of both categories.
         * @return A toggle checkbox instance.
         */
        public ToggleCheckbox createItemFilter()
        {
            return new ToggleCheckbox
            (
                FilterListOverlay.this.getOverlayStartX(),
                FilterListOverlay.this.getOverlayStartY() + 24,
                Component.translatable(LangUtil.Gui.OVERLAY_FILTER_ITEM),
                Component.translatable(LangUtil.Gui.OVERLAY_FILTER_ITEM_TOOLTIP),
                FilterListOverlay.this::isItemFiltered,
                FilterListOverlay.this::setItemFiltered
            );
        }

        /**
         * This checkbox changes whether block items should be filtered out of both categories.
         * @return A toggle checkbox instance.
         */
        public ToggleCheckbox createBlockFilter()
        {
            return new ToggleCheckbox
            (
                FilterListOverlay.this.getOverlayStartX(),
                FilterListOverlay.this.getOverlayStartY() + 46,
                Component.translatable(LangUtil.Gui.OVERLAY_FILTER_BLOCK),
                Component.translatable(LangUtil.Gui.OVERLAY_FILTER_BLOCK_TOOLTIP),
                FilterListOverlay.this::isBlockFiltered,
                FilterListOverlay.this::setBlockFiltered
            );
        }
    }
}
