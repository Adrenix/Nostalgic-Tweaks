package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing;

import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.listing.DeletableSet;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.Pair;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.ChatFormatting;

import java.util.Collection;
import java.util.stream.Collectors;

public interface DeletableSetOverlay<V, L extends DeletableSet<V, L>> extends ListingOverlay<V, L>
{
    /**
     * @return The set being used by this overlay.
     */
    L getSet();

    @Override
    default Collection<Pair<String, V>> getEntries()
    {
        return this.getSet().stream().map(Pair::keyValue).collect(Collectors.toSet());
    }

    @Override
    default void onRowAdd(Object object)
    {
        this.getSet().applySafely(object, this.getSet()::add);
    }

    /**
     * Create a new row using the given arguments.
     *
     * @param key   A resource key.
     * @param value The same resource key that represents the value of the listing.
     * @return A new {@link Row} instance.
     */
    default Row createRow(String key, V value)
    {
        Row row = Row.create(this.getWidgets().rowList).heightOverflowMargin(2).build();

        row.getBuilder().highlightColor(this.getWidgets().getColor(row, key, () -> this.getSet().isDeleted(value)));
        row.getBuilder().postRenderer(this.getWidgets()::renderOutline);

        return row;
    }

    /**
     * @return The {@link IconWidget} that represents the row.
     */
    default IconWidget createIcon(Row row, String key, V value)
    {
        return IconWidget.create(this.getRowIcon(key))
            .pos(1, 4)
            .disableIf(() -> this.getSet().isDeleted(value))
            .darkenOnDisable(0.8F)
            .build(row::addWidget);
    }

    /**
     * @return The {@link TextWidget} that represents the row's title.
     */
    default TextWidget createTitle(Row row, String key, V value, IconWidget icon, ButtonWidget delete)
    {
        final int red = Color.fromFormatting(ChatFormatting.RED).get();
        final int white = Color.WHITE.get();

        return TextWidget.create(() -> this.getRowTitle(key))
            .posY(() -> icon.getY() + 4)
            .color(() -> this.getSet().isDeleted(value) ? red : white)
            .italicsWhen(() -> this.getSet().isDeleted(value))
            .rightOf(icon, 4)
            .extendWidthTo(delete, 2)
            .build(row::addWidget);
    }

    /**
     * @return The {@link ButtonWidget} that undoes a deletion action.
     */
    default ButtonWidget createUndo(Row row, V value)
    {
        return ButtonWidget.create(Lang.Button.UNDO)
            .posY(2)
            .icon(Icons.UNDO)
            .hoverIcon(Icons.UNDO_HOVER)
            .fromWidgetEndX(row, 2)
            .onPress(() -> this.getSet().add(value))
            .disableIf(() -> this.getSet().contains(value))
            .useTextWidth()
            .build(row::addWidget);
    }

    /**
     * @return The {@link ButtonWidget} that deletes the row entry.
     */
    default ButtonWidget createDelete(Row row, V value, ButtonWidget leftOf)
    {
        return ButtonWidget.create(Lang.Button.DELETE)
            .leftOf(leftOf, 1)
            .icon(Icons.TRASH_CAN)
            .onPress(() -> this.getSet().delete(value))
            .disableIf(() -> this.getSet().isDeleted(value))
            .useTextWidth()
            .build(row::addWidget);
    }
}
