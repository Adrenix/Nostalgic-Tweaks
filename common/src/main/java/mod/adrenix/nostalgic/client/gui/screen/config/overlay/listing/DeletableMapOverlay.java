package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing;

import mod.adrenix.nostalgic.client.gui.screen.config.widget.SliderTweak;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.ActiveBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.factory.TweakMap;
import mod.adrenix.nostalgic.tweak.listing.DeletableMap;
import mod.adrenix.nostalgic.util.client.world.ItemClientUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.Holder;
import mod.adrenix.nostalgic.util.common.data.Pair;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.world.ItemCommonUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface DeletableMapOverlay<V, L extends DeletableMap<V, L>> extends ListingOverlay<V, L>
{
    /* Methods */

    /**
     * @return The map being used by this overlay.
     */
    L getMap();

    @Override
    default Collection<Pair<String, V>> getEntries()
    {
        return this.getMap().entrySet().stream().map(Pair::entry).collect(Collectors.toSet());
    }

    @Override
    default void onRowAdd(Object object)
    {
        this.getMap().putIfAbsent(object.toString());
    }

    /**
     * Gets the reset value for a map entry. If a default list map has the given key, then the value associated with
     * that key in the default map will be used as the reset value. Otherwise, the map's default value will be used.
     *
     * @param key A listing map key.
     * @return A reset value for the key.
     */
    private V getResetValue(String key)
    {
        V resetValue = this.getMap().getDefaultValue();

        if (this.getTweak().getDefault().getMap().containsKey(key))
            return this.getTweak().getDefault().getMap().get(key);

        return resetValue;
    }

    @Override
    default Row getRow(String key, V value)
    {
        Item item = ItemCommonUtil.getOptionalItem(key).orElse(Items.BARRIER);
        Row row = Row.create(this.getWidgets().rowList).build();
        TextureIcon texture = this.getRowIcon(key);
        Holder<V> cached = Holder.create(this.getMap().getOrDeleted(key));
        V resetValue = this.getResetValue(key);
        L map = this.getMap();

        row.getBuilder().highlightColor(this.getWidgets().getColor(row, key, () -> map.isDeleted(key)));
        row.getBuilder().postRenderer(this.getWidgets()::renderOutline);

        IconWidget icon = IconWidget.create(texture)
            .pos(1, ItemClientUtil.isModelFlat(item) ? 0 : 2)
            .disableIf(() -> map.isDeleted(key))
            .darkenOnDisable(0.8F)
            .build(row::addWidget);

        TextWidget.create(() -> this.getRowTitle(key))
            .posY(() -> icon.getY() + 4)
            .color(() -> map.isDeleted(key) ? Color.fromFormatting(ChatFormatting.RED).get() : Color.WHITE.get())
            .italicsWhen(() -> this.isUndoable(key, cached))
            .rightOf(icon, 4)
            .centerAligned()
            .useTextWidth()
            .build(row::addWidget);

        ButtonWidget delete = ButtonWidget.create(Lang.Button.DELETE)
            .below(icon, 4)
            .posX(1)
            .icon(Icons.TRASH_CAN)
            .onPress(() -> map.delete(key, map.getOrDeleted(key)))
            .disableIf(() -> map.isDeleted(key))
            .useTextWidth()
            .build(row::addWidget);

        ButtonWidget undo = ButtonWidget.create(Lang.Button.UNDO)
            .rightOf(delete, 1)
            .icon(Icons.UNDO)
            .hoverIcon(Icons.UNDO_HOVER)
            .fromWidgetEndX(row, 1)
            .onPress(() -> this.undo(key, cached))
            .enableIf(() -> this.isUndoable(key, cached))
            .useTextWidth()
            .build(row::addWidget);

        ButtonWidget reset = ButtonWidget.create(Lang.Button.RESET)
            .rightOf(undo, 1)
            .icon(Icons.REDO)
            .hoverIcon(Icons.REDO_HOVER)
            .fromWidgetEndX(row, 1)
            .onPress(() -> map.put(key, resetValue))
            .disableIf(() -> this.isResettable(key, resetValue))
            .useTextWidth()
            .build(row::addWidget);

        DynamicWidget<?, ?> controller = this.getController(key, row, reset);

        if (controller.getBuilder() instanceof ActiveBuilder<?, ?> builder)
            builder.disableIf(() -> map.isDeleted(key));

        return row;
    }

    /**
     * Check whether a map listing key is resettable.
     *
     * @param key        A map listing key.
     * @param resetValue A reset value.
     * @return Whether changes in the listing map key can be reset.
     */
    private boolean isResettable(String key, V resetValue)
    {
        V value = this.getMap().getOrDeleted(key);

        if (value != null)
            return value.equals(resetValue);

        return false;
    }

    /**
     * Check whether a map listing key is undoable.
     *
     * @param key    A map listing key.
     * @param cached A cached value holder from the map listing key.
     * @return Whether changes in a map listing key can be undone.
     */
    private boolean isUndoable(String key, Holder<V> cached)
    {
        if (this.getMap().isDeleted(key))
            return true;

        V fromCache = this.getTweak().fromCache().getOrDeleted(key);
        V fromSaved = this.getTweak().fromDisk().getOrDeleted(key);

        boolean isCacheDiff = fromCache != null && !fromCache.equals(cached.get());
        boolean isSaveDiff = fromSaved != null && !fromSaved.equals(cached.get());

        return isCacheDiff || isSaveDiff;
    }

    /**
     * Perform an undo action on a map listing key.
     *
     * @param key    A map listing key.
     * @param cached A cached value holder from the map listing key.
     */
    private void undo(String key, Holder<V> cached)
    {
        if (this.getMap().isDeleted(key))
            this.getMap().undo(key, this.getMap().getOrDeleted(key));
        else
        {
            V fromCache = this.getTweak().fromCache().getOrDeleted(key);
            V fromSaved = this.getTweak().fromDisk().getOrDeleted(key);

            if (fromCache != null && !fromCache.equals(cached.get()))
                this.getMap().put(key, cached.get());
            else if (fromSaved != null && !fromSaved.equals(cached.get()))
            {
                V saved = this.getTweak().fromDisk().getMap().get(key);

                cached.set(saved);
                this.getMap().put(key, saved);
            }
        }
    }

    /**
     * Create a row controller based on the given map listing key.
     *
     * @param key     A map listing key to set/get data from.
     * @param row     A {@link Row} to get context data from.
     * @param rightOf The {@link DynamicWidget} to keep this controller to the right of.
     * @return A new {@link DynamicWidget} instance.
     */
    private DynamicWidget<?, ?> getController(String key, Row row, DynamicWidget<?, ?> rightOf)
    {
        // Number
        if (Number.class.isAssignableFrom(this.getMap().genericType()))
        {
            TweakMap<?, ?> tweak = this.getTweak().cast(TweakMap.class).orElseThrow();

            Supplier<Number> getValue = () -> {
                Number number = (Number) this.getMap().getOrDeleted(key);

                if (number == null)
                    return 0;

                return number;
            };

            Consumer<Number> setValue = (number) -> {
                if (this.getMap().isDeleted(key))
                    return;

                this.getMap().applySafely(key, number, this.getMap()::put);
            };

            return SliderTweak.create(tweak.getSlider().orElseThrow(), setValue, getValue)
                .rightOf(rightOf, 1)
                .extendWidthToEnd(row, 1)
                .build(row::addWidget);
        }

        // Unknown
        return ButtonWidget.create(Lang.literal("NO-IMPL"))
            .rightOf(rightOf, 1)
            .extendWidthToEnd(row, 1)
            .disabledTooltip(Lang.TweakRow.NO_IMPL, 45)
            .disableIf(BooleanSupplier.ALWAYS)
            .icon(Icons.NO_ENTRY)
            .padding(4)
            .build();
    }
}
