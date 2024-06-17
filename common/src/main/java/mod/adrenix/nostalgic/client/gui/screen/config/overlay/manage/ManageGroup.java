package mod.adrenix.nostalgic.client.gui.screen.config.overlay.manage;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;

public abstract class ManageGroup
{
    /* Abstraction */

    /**
     * This method defines the widgets that are present within this group. Use the widget register method
     * (package-private) to register a widget to this group.
     *
     * @param manager A {@link ManageOverlay} instance to collect overlay information.
     */
    abstract void define(ManageOverlay manager);

    /* Fields */

    private final UniqueArrayList<DynamicWidget<?, ?>> widgets = new UniqueArrayList<>();

    /* Methods */

    /**
     * Register a widget to this group.
     *
     * @param widget A {@link DynamicWidget} instance.
     */
    void register(DynamicWidget<?, ?> widget)
    {
        this.widgets.add(widget);
    }

    /**
     * @return Whether this group is currently visible to the management overlay.
     */
    public boolean isGroupInvisible()
    {
        return !ManageSection.getActive().getManager().equals(this);
    }

    /**
     * Set all widgets in this group manager to invisible.
     */
    public void setInvisible()
    {
        this.widgets.forEach(DynamicWidget::setInvisible);
    }

    /**
     * Set all widgets in this group manager to visible.
     */
    public void setVisible()
    {
        this.widgets.forEach(DynamicWidget::setVisible);
    }

    /**
     * Subscribes this group to the given manager.
     *
     * @param manager A {@link ManageOverlay} instance.
     */
    public void subscribe(ManageOverlay manager)
    {
        this.widgets.clear();

        this.define(manager);

        manager.overlay.addWidgets(this.widgets);
    }
}
