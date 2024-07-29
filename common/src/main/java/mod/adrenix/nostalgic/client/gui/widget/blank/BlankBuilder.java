package mod.adrenix.nostalgic.client.gui.widget.blank;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.LayoutBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.TooltipBuilder;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;

public class BlankBuilder extends DynamicBuilder<BlankBuilder, BlankWidget>
    implements LayoutBuilder<BlankBuilder, BlankWidget>, TooltipBuilder<BlankBuilder, BlankWidget>
{
    /* Fields */

    BlankRenderer renderer = BlankRenderer.EMPTY;

    /* Constructor */

    protected BlankBuilder()
    {
        this.defaultWidth = 1;
        this.defaultHeight = 1;
        this.canFocus = BooleanSupplier.NEVER;
    }

    /* Methods */

    @Override
    public BlankBuilder self()
    {
        return this;
    }

    /**
     * Set custom rendering instructions for this {@link BlankWidget}.
     *
     * @param renderer A {@link BlankRenderer} instance.
     */
    @PublicAPI
    public BlankBuilder renderer(BlankRenderer renderer)
    {
        this.renderer = renderer;

        return this.self();
    }

    @Override
    protected BlankWidget construct()
    {
        return new BlankWidget(this);
    }
}
