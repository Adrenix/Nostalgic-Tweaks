package mod.adrenix.nostalgic.client.gui.screen.config.widget.list;

import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRowMaker;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.tweak.container.Container;

public class DescriptionRowMaker extends AbstractRowMaker<DescriptionRowMaker, DescriptionRow>
{
    /* Fields */

    final Container container;

    /* Constructor */

    DescriptionRowMaker(Container container, RowList rowList)
    {
        super(rowList);

        this.container = container;
    }

    /* Methods */

    @Override
    public DescriptionRowMaker self()
    {
        return this;
    }

    @Override
    protected DescriptionRow construct()
    {
        return new DescriptionRow(this);
    }
}
