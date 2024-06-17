package mod.adrenix.nostalgic.client.gui.screen.config.widget.list;

import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRowMaker;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.tweak.container.Container;
import org.jetbrains.annotations.Nullable;

public class GroupRowMaker extends AbstractRowMaker<GroupRowMaker, GroupRow>
{
    /* Fields */

    @Nullable final GroupRow parent;
    final Container container;

    /* Constructor */

    GroupRowMaker(Container container, RowList rowList)
    {
        super(rowList);

        this.container = container;
        this.parent = null;
    }

    GroupRowMaker(Container container, GroupRow parent)
    {
        super(parent.getRowList());

        this.container = container;
        this.parent = parent;
    }

    /* Methods */

    @Override
    public GroupRowMaker self()
    {
        return this;
    }

    @Override
    protected GroupRow construct()
    {
        return new GroupRow(this);
    }
}
