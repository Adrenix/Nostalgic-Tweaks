package mod.adrenix.nostalgic.client.gui.screen.config.widget.list;

import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRowMaker;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import org.jetbrains.annotations.Nullable;

public class TweakRowMaker extends AbstractRowMaker<TweakRowMaker, TweakRow>
{
    /* Fields */

    @Nullable GroupRow parent;
    final Tweak<?> tweak;
    boolean indent = false;

    /* Constructors */

    protected TweakRowMaker(Tweak<?> tweak, RowList rowList)
    {
        super(rowList);

        this.tweak = tweak;
    }

    protected TweakRowMaker(Tweak<?> tweak, GroupRow parent)
    {
        this(tweak, parent.getRowList());

        this.parent = parent;
    }

    /* Methods */

    @Override
    public TweakRowMaker self()
    {
        return this;
    }

    @Override
    public TweakRowMaker indent(int indent)
    {
        this.indent = true;

        return super.indent(indent);
    }

    @Override
    protected TweakRow construct()
    {
        return new TweakRow(this);
    }
}
