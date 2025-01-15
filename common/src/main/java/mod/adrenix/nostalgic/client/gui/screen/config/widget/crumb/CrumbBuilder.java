package mod.adrenix.nostalgic.client.gui.screen.config.widget.crumb;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.LayoutBuilder;
import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRow;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import org.jetbrains.annotations.Nullable;

public class CrumbBuilder extends DynamicBuilder<CrumbBuilder, CrumbWidget>
    implements LayoutBuilder<CrumbBuilder, CrumbWidget>
{
    /* Fields */

    final Tweak<?> tweak;
    @Nullable AbstractRow<?, ?> row;

    /* Constructor */

    CrumbBuilder(Tweak<?> tweak)
    {
        this.tweak = tweak;
        this.canFocus = BooleanSupplier.NEVER;
    }

    /* Methods */

    @Override
    public CrumbBuilder self()
    {
        return this;
    }

    /**
     * Set the {@link AbstractRow} associated with this crumb widget. If defined, this crumb widget will expand its
     * height so that crumbs can move to the next line if a crumb's text exceeds a row's maximum ending x-position. If
     * not defined, the crumb widget will expand its height when a crumb's text exceeds the maximum game window width.
     *
     * @param row An {@link AbstractRow} instance.
     */
    @PublicAPI
    public CrumbBuilder syncWithRow(AbstractRow<?, ?> row)
    {
        this.row = row;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CrumbWidget construct()
    {
        return new CrumbWidget(this);
    }
}
