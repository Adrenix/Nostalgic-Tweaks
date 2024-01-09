package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageOverlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageType;
import mod.adrenix.nostalgic.client.gui.overlay.types.state.SwitchGroup;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.add.ItemOverlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.listing.ItemListing;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.data.NullableAction;
import mod.adrenix.nostalgic.util.common.data.Pair;
import mod.adrenix.nostalgic.util.common.function.BooleanConsumer;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.world.ItemCommonUtil;
import mod.adrenix.nostalgic.util.common.world.ItemFilter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public abstract class ItemListingOverlay<V, L extends ItemListing<V, L>> implements ListingOverlay<V, L>
{
    /* Fields */

    @Nullable private FilterOverlay filter = null;
    @Nullable private ButtonWidget abacus = null;
    @Nullable private ButtonWidget quick = null;
    private final ListingWidgets<V, L> widgets;
    private final TweakListing<V, L> tweak;
    private final Overlay overlay;
    private final L itemListing;
    protected final Overlay wildcardHelp;

    /* Constructor */

    public ItemListingOverlay(TweakListing<V, L> tweak)
    {
        this.tweak = tweak;
        this.itemListing = tweak.fromCache();
        this.overlay = this.getDefaultOverlay().build();
        this.widgets = new ListingWidgets<>(this);

        this.wildcardHelp = MessageOverlay.create(MessageType.HELP, Lang.Button.HELP, Lang.Listing.WILDCARD_HELP)
            .setResizePercentage(0.5D)
            .alignLeft()
            .build();
    }

    /* Methods */

    @Override
    public TweakListing<V, L> getTweak()
    {
        return this.tweak;
    }

    @Override
    public ListingWidgets<V, L> getWidgets()
    {
        return this.widgets;
    }

    @Override
    public ItemListing<V, L> getListing()
    {
        return this.itemListing;
    }

    @Override
    public Overlay getOverlay()
    {
        return this.overlay;
    }

    @Override
    public void onAdd()
    {
        new ItemOverlay<>(this.tweak.fromCache(), this::createListRows, this::addItem).open();
    }

    /**
     * Adds the given item stack to the listing and updates the row list.
     *
     * @param itemStack An {@link ItemStack} instance.
     */
    private void addItem(ItemStack itemStack)
    {
        this.onRowAdd(ItemCommonUtil.getResourceKey(itemStack));

        this.createListRows();
        this.widgets.findAndHighlight(ItemCommonUtil.getLocalizedItem(itemStack));
    }

    /**
     * Quickly add the item from the player's hand, if possible.
     */
    private void onQuickAdd()
    {
        if (Minecraft.getInstance().player != null)
            this.addItem(Minecraft.getInstance().player.getMainHandItem());
    }

    /**
     * Get the widgets needed to manage a row's wildcard option.
     *
     * @param row The {@link Row} instance adding these widgets.
     * @param key The item resource key to check if it is a wildcard.
     * @return A new {@link SwitchGroup.Widgets} instance.
     */
    protected SwitchGroup.Widgets getWildcardWidgets(Row row, String key)
    {
        Component wildcardHead = Lang.Listing.WILDCARD_TITLE.get();
        Component wildcardInfo = Lang.Listing.WILDCARD_MESSAGE.get(ItemCommonUtil.getLocalizedItem(key));

        BooleanSupplier isWildcard = () -> this.getListing().containsWildcard(key);
        BooleanConsumer setWildcard = (state) -> {
            if (state)
                this.getListing().addWildcard(key);
            else
                this.getListing().removeWildcard(key);
        };

        return SwitchGroup.create(row, wildcardHead, wildcardInfo, isWildcard, setWildcard).getWidgets();
    }

    /**
     * Check if a wildcard entry has changed.
     *
     * @param key The item resource key to check.
     * @return Whether the wildcard state changed for the given item resource key.
     */
    protected boolean isWildcardChanged(String key)
    {
        L disk = this.tweak.fromDisk();
        L cache = this.tweak.fromCache();

        boolean isRemoved = disk.containsWildcard(key) && !cache.containsWildcard(key);
        boolean isAdded = cache.containsWildcard(key) && !disk.containsWildcard(key);

        return isAdded || isRemoved;
    }

    @Override
    public String getLocalizedKey(String listKey)
    {
        if (ItemCommonUtil.getOptionalItem(listKey).isPresent())
            return ItemCommonUtil.getLocalizedItem(listKey);

        return listKey;
    }

    @Override
    public HashMap<Pair<String, V>, String> getLocalizedEntries(Collection<Pair<String, V>> collection)
    {
        HashMap<Pair<String, V>, String> localized = new HashMap<>();

        for (Pair<String, V> pair : this.getEntries())
        {
            Optional<Item> item = ItemCommonUtil.getOptionalItem(pair.left());

            if (item.isPresent() && this.filter != null && this.filter.isFiltered(item.get()))
                continue;

            localized.put(pair, this.getLocalizedKey(pair.left()));
        }

        return localized;
    }

    @Override
    public TextureIcon getRowIcon(String listKey)
    {
        return TextureIcon.fromItem(ItemCommonUtil.getOptionalItem(listKey).orElse(Items.BARRIER));
    }

    @Override
    public Component appendToRowTitle(final String listKey, MutableComponent rowTitle)
    {
        if (ItemCommonUtil.getOptionalItem(listKey).isEmpty())
        {
            return rowTitle.append(Component.literal(" ["))
                .append(Lang.Listing.INVALID_ITEM.withStyle(ChatFormatting.RED))
                .append(Component.literal("]").withStyle(ChatFormatting.RESET));
        }

        return rowTitle;
    }

    @Override
    public void createExtraWidgets(ListingWidgets<V, L> widgets)
    {
        if (Minecraft.getInstance().player != null)
        {
            BooleanSupplier isMainHandItemInvalid = () -> {
                ItemStack item = Minecraft.getInstance().player.getMainHandItem();

                return ItemFilter.isFiltered(item, this.itemListing);
            };

            this.quick = ButtonWidget.create(Lang.Button.QUICK)
                .disableIf(CollectionUtil.areAnyTrue(isMainHandItemInvalid, this::isLocked))
                .onPress(this::onQuickAdd)
                .rightOf(widgets.add, 1)
                .icon(Icons.LIGHTNING)
                .tooltip(Lang.Button.QUICK, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.QUICK, 35)
                .iconTextPadding(4)
                .padding(5)
                .useTextWidth()
                .skipFocusOnClick()
                .build(this.overlay::addWidget);

            widgets.undo.getBuilder().rightOf(this.quick, 1);
            widgets.shrinkable.add(this.quick);
        }

        if (this.itemListing.areRulesEmpty())
        {
            this.abacus = ButtonWidget.create(Lang.Button.FILTER)
                .onPress(() -> NullableAction.attempt(this.filter, FilterOverlay::open))
                .skipFocusOnClick()
                .useTextWidth()
                .padding(5)
                .icon(Icons.FILTER)
                .tooltip(Lang.Button.FILTER, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.FILTER, 35)
                .build(this.overlay::addWidget);

            if (this.quick == null)
                this.abacus.getBuilder().rightOf(widgets.add, 1);
            else
                this.abacus.getBuilder().rightOf(this.quick, 1);

            this.filter = new FilterOverlay(this.abacus, this::createListRows);

            widgets.undo.getBuilder().rightOf(this.abacus, 1);
            widgets.shrinkable.add(this.abacus);
        }
    }

    @Override
    public void setTabOrder(ListingWidgets<V, L> widgets)
    {
        widgets.add.setTabOrderGroup(widgets.tabOrder.getAndIncrement());

        if (this.quick != null)
            this.quick.setTabOrderGroup(widgets.tabOrder.getAndIncrement());

        if (this.abacus != null)
            this.abacus.setTabOrderGroup(widgets.tabOrder.getAndIncrement());

        widgets.undo.setTabOrderGroup(widgets.tabOrder.getAndIncrement());
        widgets.search.setTabOrderGroup(widgets.tabOrder.getAndIncrement());
        widgets.manage.setTabOrderGroup(widgets.tabOrder.getAndIncrement());
        widgets.finish.setTabOrderGroup(widgets.tabOrder.getAndIncrement());
        widgets.rowList.setTabOrderGroup(widgets.tabOrder.getAndIncrement());
    }
}
