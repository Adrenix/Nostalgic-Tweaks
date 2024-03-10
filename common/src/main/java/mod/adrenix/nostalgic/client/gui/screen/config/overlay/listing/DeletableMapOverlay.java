package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing;

import mod.adrenix.nostalgic.client.gui.screen.config.widget.SliderTweak;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonBuilder;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconFactory;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.text.TextBuilder;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.factory.TweakMap;
import mod.adrenix.nostalgic.tweak.listing.DeletableMap;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.Holder;
import mod.adrenix.nostalgic.util.common.data.Pair;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.ChatFormatting;
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
     * Create a new row using the given arguments.
     *
     * @param key A map key.
     * @return A new {@link Row} instance.
     */
    default Row createRow(String key)
    {
        Row row = Row.create(this.getWidgets().rowList).heightOverflowMargin(2).build();

        row.getBuilder().highlightColor(this.getWidgets().getColor(row, key, () -> this.getMap().isDeleted(key)));
        row.getBuilder().postRenderer(this.getWidgets()::renderOutline);

        return row;
    }

    /**
     * @return The {@link IconFactory} that represents the row.
     */
    default IconFactory getIconFactory(String key, TextureIcon icon)
    {
        return IconWidget.create(icon)
            .pos(1, GameUtil.isModelFlat(icon.getItem().orElse(Items.BARRIER)) ? 1 : 2)
            .disableIf(() -> this.getMap().isDeleted(key))
            .darkenOnDisable(0.8F);
    }

    /**
     * @return The {@link TextBuilder} that represents the row's title.
     */
    default TextBuilder getTitleBuilder(String key, IconWidget icon, Holder<V> cache)
    {
        final int red = Color.fromFormatting(ChatFormatting.RED).get();
        final int white = Color.WHITE.get();

        return TextWidget.create(() -> this.getRowTitle(key))
            .posY(() -> icon.getY() + 4)
            .color(() -> this.getMap().isDeleted(key) ? red : white)
            .italicsWhen(() -> this.isUndoable(key, cache))
            .rightOf(icon, 4)
            .centerAligned()
            .useTextWidth();
    }

    /**
     * @return The {@link TextBuilder} that represents the row's title with additional italics instructions.
     */
    default TextBuilder getTitleBuilder(String key, IconWidget icon, Holder<V> cache, BooleanSupplier italicsWhen)
    {
        return this.getTitleBuilder(key, icon, cache)
            .italicsWhen(() -> italicsWhen.getAsBoolean() || this.isUndoable(key, cache));
    }

    /**
     * @return The {@link ButtonBuilder} that deletes the row entry.
     */
    default ButtonBuilder getDeleteBuilder(String key)
    {
        return ButtonWidget.create(Lang.Button.DELETE)
            .icon(Icons.TRASH_CAN)
            .onPress(() -> this.getMap().delete(key, this.getMap().getOrDeleted(key)))
            .disableIf(() -> this.getMap().isDeleted(key) || this.isLocked())
            .useTextWidth();
    }

    /**
     * @return The {@link ButtonBuilder} that undoes changes made to the row entry.
     */
    default ButtonBuilder getUndoBuilder(String key, Holder<V> cache)
    {
        return ButtonWidget.create(Lang.Button.UNDO)
            .icon(Icons.UNDO)
            .hoverIcon(Icons.UNDO_HOVER)
            .onPress(() -> this.undo(key, cache))
            .enableIf(() -> this.isUndoable(key, cache) && this.isUnlocked())
            .useTextWidth();
    }

    /**
     * @return The {@link ButtonBuilder} that resets the row's entry value.
     */
    default ButtonBuilder getResetBuilder(String key, V resetValue)
    {
        return ButtonWidget.create(Lang.Button.RESET)
            .icon(Icons.REDO)
            .hoverIcon(Icons.REDO_HOVER)
            .onPress(() -> this.getMap().put(key, resetValue))
            .disableIf(() -> this.isResettable(key, resetValue) || this.isLocked())
            .useTextWidth();
    }

    /**
     * Gets the reset value for a map entry. If a default list map has the given key, then the value associated with
     * that key in the default map will be used as the reset value. Otherwise, the map's default value will be used.
     *
     * @param key A listing map key.
     * @return A reset value for the key.
     */
    default V getResetValue(String key)
    {
        V resetValue = this.getMap().getDefaultValue();

        if (this.getTweak().getDefault().getMap().containsKey(key))
            return this.getTweak().getDefault().getMap().get(key);

        return resetValue;
    }

    /**
     * Check whether a map listing key is resettable.
     *
     * @param key        A map listing key.
     * @param resetValue A reset value.
     * @return Whether changes in the listing map key can be reset.
     */
    default boolean isResettable(String key, V resetValue)
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
    default boolean isUndoable(String key, Holder<V> cached)
    {
        if (this.getMap().isDeleted(key))
            return true;

        V fromCache = this.getTweak().fromCache().getOrDeleted(key);
        V fromSaved = this.getTweak().fromMode().getOrDeleted(key);

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
    default void undo(String key, Holder<V> cached)
    {
        if (this.getMap().isDeleted(key))
            this.getMap().undo(key, this.getMap().getOrDeleted(key));
        else
        {
            V fromCache = this.getTweak().fromCache().getOrDeleted(key);
            V fromSaved = this.getTweak().fromMode().getOrDeleted(key);

            if (fromCache != null && !fromCache.equals(cached.get()))
                this.getMap().put(key, cached.get());
            else if (fromSaved != null && !fromSaved.equals(cached.get()))
            {
                V saved = this.getTweak().fromMode().getMap().get(key);

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
    default DynamicWidget<?, ?> getController(String key, Row row, DynamicWidget<?, ?> rightOf)
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
                .extendWidthToEnd(row, 2)
                .build(row::addWidget);
        }

        // Unknown
        return ButtonWidget.create(Lang.literal("NO-IMPL"))
            .rightOf(rightOf, 1)
            .extendWidthToEnd(row, 2)
            .disabledTooltip(Lang.TweakRow.NO_IMPL, 45)
            .disableIf(BooleanSupplier.ALWAYS)
            .icon(Icons.NO_ENTRY)
            .padding(4)
            .build();
    }
}
