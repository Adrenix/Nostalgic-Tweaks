package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing;

import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.listing.DeletableSet;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
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

    @Override
    default Row getRow(String key, V value)
    {
        TextureIcon texture = this.getRowIcon(key);
        Row row = Row.create(this.getWidgets().rowList).build();
        L set = this.getSet();

        row.getBuilder().highlightColor(this.getWidgets().getColor(row, key, () -> set.isDeleted(value)));
        row.getBuilder().postRenderer(this.getWidgets()::renderOutline);

        IconWidget icon = IconWidget.create(texture)
            .pos(1, 3)
            .disableIf(() -> set.isDeleted(value))
            .darkenOnDisable(0.8F)
            .build(row::addWidget);

        TextWidget.create(() -> this.getRowTitle(key))
            .posY(() -> icon.getY() + 4)
            .color(() -> set.isDeleted(value) ? Color.fromFormatting(ChatFormatting.RED).get() : Color.WHITE.get())
            .italicsWhen(() -> set.isDeleted(value))
            .rightOf(icon, 4)
            .useTextWidth()
            .centerAligned()
            .build(row::addWidget);

        ButtonWidget undo = ButtonWidget.create(Lang.Button.UNDO)
            .posY(1)
            .icon(Icons.UNDO)
            .hoverIcon(Icons.UNDO_HOVER)
            .fromWidgetEndX(row, 1)
            .onPress(() -> set.add(value))
            .disableIf(() -> set.contains(value))
            .useTextWidth()
            .build(row::addWidget);

        ButtonWidget.create(Lang.Button.DELETE)
            .pos(1, 1)
            .leftOf(undo, 1)
            .icon(Icons.TRASH_CAN)
            .onPress(() -> set.delete(value))
            .disableIf(() -> set.isDeleted(value))
            .useTextWidth()
            .build(row::addWidget);

        return row;
    }
}
