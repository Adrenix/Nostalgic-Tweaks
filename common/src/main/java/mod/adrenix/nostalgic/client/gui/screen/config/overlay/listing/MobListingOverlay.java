package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageOverlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageType;
import mod.adrenix.nostalgic.client.gui.overlay.types.state.SwitchGroup;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.add.StringOverlay;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.listing.ListingSuggestion;
import mod.adrenix.nostalgic.tweak.listing.MobListing;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.data.Pair;
import mod.adrenix.nostalgic.util.common.function.BooleanConsumer;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public abstract class MobListingOverlay<V, L extends MobListing<V, L>> implements ListingOverlay<V, L>
{
    /* Fields */

    private final ListingWidgets<V, L> widgets;
    private final TweakListing<V, L> tweak;
    private final Overlay overlay;
    private final L mobListing;
    protected final Overlay wildcardHelp;

    /* Constructor */

    public MobListingOverlay(TweakListing<V, L> tweak)
    {
        this.tweak = tweak;
        this.mobListing = tweak.fromCache();
        this.overlay = this.getDefaultOverlay().build();
        this.widgets = new ListingWidgets<>(this);

        this.wildcardHelp = MessageOverlay.create(MessageType.HELP, Lang.Button.HELP, Lang.Listing.WILDCARD_MOB)
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
    public MobListing<V, L> getListing()
    {
        return this.mobListing;
    }

    @Override
    public Overlay getOverlay()
    {
        return this.overlay;
    }

    @Override
    public void onAdd()
    {
        new StringOverlay(() -> ListingSuggestion.MOB, ((L) this.tweak.fromCache())::containsKey, this::createListRows, this::addMob).open();
    }

    /**
     * Adds the given entity type resource key to the listing and updates the row list.
     *
     * @param listKey The mob entity type resource key.
     */
    private void addMob(String listKey)
    {
        this.onRowAdd(listKey);

        this.createListRows();
        this.widgets.findAndHighlight(this.getLocalizedKey(listKey));
    }

    @Override
    public String getLocalizedKey(String listKey)
    {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.tryParse(listKey))
            .map(EntityType::getDescription)
            .map(Component::getString)
            .orElse(listKey);
    }

    @Override
    public HashMap<Pair<String, V>, String> getLocalizedEntries(Collection<Pair<String, V>> collection)
    {
        HashMap<Pair<String, V>, String> localized = new HashMap<>();

        for (Pair<String, V> pair : this.getEntries())
            localized.put(pair, this.getLocalizedKey(pair.left()));

        return localized;
    }

    @Override
    public TextureIcon getRowIcon(String listKey)
    {
        Optional<EntityType<?>> maybeType = BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.tryParse(listKey));

        if (maybeType.isEmpty())
            return TextureIcon.fromItem(Items.BARRIER);

        SpawnEggItem item = SpawnEggItem.byId(maybeType.get());

        if (item == null)
            return TextureIcon.fromItem(Items.BARRIER);

        return TextureIcon.fromItem(item);
    }

    @Override
    public Component appendToRowTitle(final String listKey, MutableComponent rowTitle)
    {
        if (BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.tryParse(listKey)).isEmpty())
        {
            return rowTitle.append(Component.literal(" ["))
                .append(Lang.Listing.INVALID_MOB.withStyle(ChatFormatting.RED))
                .append(Component.literal("]").withStyle(ChatFormatting.RESET));
        }

        return rowTitle;
    }

    /**
     * Get the widgets needed to manage a row's wildcard option.
     *
     * @param row The {@link Row} instance adding these widgets.
     * @param key The mob resource key to check if it is a wildcard.
     * @return A new {@link SwitchGroup.Widgets} instance.
     */
    protected SwitchGroup.Widgets getWildcardWidgets(Row row, String key)
    {
        Component wildcardHead = Lang.Listing.WILDCARD_TITLE.get();
        Component wildcardInfo = Lang.Listing.WILDCARD_MESSAGE.get(key);

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
     * @param key The mob resource key to check.
     * @return Whether the wildcard state changed for the given mob resource key.
     */
    protected boolean isWildcardChanged(String key)
    {
        L mode = this.tweak.fromMode();
        L cache = this.tweak.fromCache();

        boolean isRemoved = mode.containsWildcard(key) && !cache.containsWildcard(key);
        boolean isAdded = cache.containsWildcard(key) && !mode.containsWildcard(key);

        return isAdded || isRemoved;
    }
}
