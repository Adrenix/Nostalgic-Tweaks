package mod.adrenix.nostalgic.client.gui.screen.config.widget.tag;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.LayoutBuilder;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;

public class TagBuilder extends DynamicBuilder<TagBuilder, TagWidget> implements LayoutBuilder<TagBuilder, TagWidget>
{
    /* Fields */

    final Tweak<?> tweak;

    /* Constructor */

    TagBuilder(Tweak<?> tweak)
    {
        this.tweak = tweak;
        this.canFocus = BooleanSupplier.NEVER;
    }

    /* Methods */

    @Override
    public TagBuilder self()
    {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TagWidget construct()
    {
        return new TagWidget(this);
    }
}
