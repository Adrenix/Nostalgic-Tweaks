package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.input.GenericInput;
import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRow;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.tweak.listing.Listing;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.client.search.GenericDatabase;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.Rectangle;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListingWidgets<V, L extends Listing<V, L>>
{
    /* Fields */

    private final Overlay overlay;
    public final RowList rowList;
    public final ButtonWidget add;
    public final ButtonWidget undo;
    public final GenericInput search;
    public final ButtonWidget finish;
    public final SeparatorWidget separator;
    public final IntegerHolder tabOrder;
    public final LinkedHashSet<ButtonWidget> shrinkable;
    final ListingOverlay<V, L> listing;
    final LinkedHashSet<AbstractRow<?, ?>> sorted;
    final GenericDatabase<AbstractRow<?, ?>> database;
    @Nullable AbstractRow<?, ?> highlighted;

    /* Constructor */

    public ListingWidgets(ListingOverlay<V, L> listing)
    {
        this.listing = listing;
        this.overlay = listing.getOverlay();
        this.sorted = new LinkedHashSet<>();
        this.shrinkable = new LinkedHashSet<>();
        this.database = new GenericDatabase<>();
        this.tabOrder = IntegerHolder.create(0);

        this.add = ButtonWidget.create(Lang.Button.ADD)
            .posX(1)
            .fromScreenEndY(1)
            .skipFocusOnClick()
            .useTextWidth()
            .padding(5)
            .icon(Icons.ADD)
            .tooltip(Lang.Button.ADD, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.ADD, 35)
            .tabOrderGroup(this.tabOrder.getAndIncrement())
            .onPress(listing::onAdd)
            .build(List.of(this.overlay::addWidget, this.shrinkable::add));

        this.separator = SeparatorWidget.create(Color.SILVER_CHALICE)
            .above(this.add, 1)
            .height(1)
            .extendWidthToScreenEnd(0)
            .build(this.overlay::addWidget);

        this.rowList = RowList.create()
            .highlight(0.15D, Animation.linear(150L, TimeUnit.MILLISECONDS))
            .emptyMessage(this::getEmptyMessage)
            .useScissorRectangle(this::getListScissoringBounds)
            .extendHeightTo(this.separator, 0)
            .extendWidthToScreenEnd(0)
            .heightOverflowMargin(1)
            .showSelectionBorder()
            .horizontalMargin(2)
            .verticalMargin(2)
            .useSeparators()
            .build(this.overlay::addWidget);

        this.undo = ButtonWidget.create(Lang.Button.UNDO)
            .onPress(listing::onUndo)
            .enableIf(listing.getTweak()::isCacheUndoable)
            .rightOf(this.add, 1)
            .skipFocusOnClick()
            .useTextWidth()
            .padding(5)
            .icon(Icons.UNDO)
            .hoverIcon(Icons.UNDO_HOVER)
            .tooltip(Lang.Button.UNDO, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.UNDO, 35)
            .build();

        this.finish = ButtonWidget.create(Lang.Vanilla.GUI_DONE)
            .icon(Icons.GREEN_CHECK)
            .onPress(this.overlay::close)
            .fromScreenEndX(1)
            .fromScreenEndY(1)
            .useTextWidth()
            .padding(5)
            .build();

        this.search = GenericInput.create()
            .onInput(this::find)
            .icon(Icons.SEARCH)
            .whenEmpty(Lang.Input.SEARCH)
            .border(Color.BLACK, Color.WHITE)
            .background(Color.OLIVE_BLACK, Color.OLIVE_BLACK)
            .maxLength(100)
            .searchShortcut()
            .extendWidthTo(this.finish, 1)
            .rightOf(this.undo, 1)
            .afterSync(this::resizeSearch)
            .build();

        listing.createExtraWidgets(this);
        listing.setTabOrder(this);

        this.overlay.addWidgets(this.undo, this.finish, this.search);
        this.shrinkable.add(this.undo);
    }

    /* Methods */

    /**
     * Check if buttons need resized so that the search widget is not too small.
     */
    private void resizeSearch(GenericInput search)
    {
        if (search.getWidth() >= 120)
            return;

        this.shrinkable.forEach(ButtonWidget::shrink);

        search.getBuilder().sync();
    }

    /**
     * Find and highlight a row from the given string.
     *
     * @param string A string to find within a map's keys or a set's elements.
     */
    public void findAndHighlight(String string)
    {
        AbstractRow<?, ?> row = this.database.getFromDatabase(string);

        if (row == null)
            return;
        else
            this.highlighted = row;

        this.rowList.setFocused(row);
    }

    /**
     * If the row has a focused widget, or is highlighted, then a color will be returned. Otherwise, the transparent
     * color is returned.
     *
     * @param row A {@link AbstractRow} instance.
     * @return A {@link Color} instance.
     */
    public Color getColor(AbstractRow<?, ?> row, String key, BooleanSupplier isDeleted)
    {
        return new Color(() -> {
            if (isDeleted.getAsBoolean())
                return Color.fromFormatting(ChatFormatting.RED);

            if (row.isWidgetFocused())
                return Color.FRENCH_SKY_BLUE;

            if (row == this.highlighted)
                return Color.GREEN_APPLE;

            boolean isAdded = switch (this.listing.getTweak().getCacheMode())
            {
                case LOCAL -> !this.listing.getTweak().fromDisk().containsKey(key);
                case NETWORK -> !this.listing.getTweak().fromServer().containsKey(key);
            };

            return isAdded ? Color.MUGHAL_GREEN : Color.TRANSPARENT;
        });
    }

    /**
     * Filter out rows that are not related to the given search query.
     *
     * @param query A string that filters out unrelated rows and highlights highly relatable ones.
     */
    private void find(String query)
    {
        if (query.isEmpty())
        {
            this.rowList.clear();
            this.rowList.addAll(this.sorted);

            return;
        }

        this.rowList.clear();
        this.rowList.addAll(this.database.findValues(query));
    }

    /**
     * @return An empty message that depends on whether nothing is found or nothing is saved.
     */
    private Component getEmptyMessage()
    {
        if (this.search == null)
            return Component.empty();

        if (this.search.getInput().isEmpty() && this.rowList.getVisibleRows().isEmpty())
            return Lang.Listing.NOTHING_SAVED.get();
        else
            return Lang.Listing.NOTHING_FOUND.get();
    }

    /**
     * @return The scissoring rectangle for the row list.
     */
    private Rectangle getListScissoringBounds()
    {
        int startX = this.overlay.getInsideX();
        int startY = this.overlay.getInsideY();
        int endX = this.overlay.getInsideEndX();
        int endY = this.separator.getY();

        return new Rectangle(startX, startY, endX, endY);
    }

    /**
     * Render a highlight outline around a row based on current list context.
     *
     * @param row         The {@link AbstractRow} instance.
     * @param graphics    The {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    @PublicAPI
    public void renderOutline(AbstractRow<?, ?> row, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        Color color = row.getHighlightColor();

        if (color.isEmpty())
            return;

        RenderUtil.outline(graphics, row.getX(), row.getY(), row.getWidth(), row.getHeight(), color);
    }
}
