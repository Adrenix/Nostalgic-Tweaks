package mod.adrenix.nostalgic.client.config.gui.widget.list.row;

import mod.adrenix.nostalgic.client.config.annotation.container.TweakEmbed;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ContainerButton;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Group Container Rows
 *
 * The following classes and methods are used to handle categories, subcategories, and embedded subcategories.
 * These configuration rows appear as arrows that will expand or collapse subscribed configuration rows.
 */

public abstract class ConfigRowGroup
{
    /**
     * An enumeration that defines three categorization types.
     */
    public enum ContainerType
    {
        CATEGORY,    // The main parent (subscribes to a main group like Eye Candy)
        SUBCATEGORY, // A child of the parent (subscribes to a category like Combat Gameplay)
        EMBEDDED     // A subcategory of a subcategory (like 'Buttons' to 'Title Screen Candy')
    }

    /**
     * A container row is a type of row that holds subscribed rows. Subscriptions are determined based on metadata
     * defined in the client config.
     *
     * These rows can also be embedded within other container rows. This is done by specific a container type.
     * Containers are expanded in the following order: Category > Subcategory > Embedded
     */
    public static class ContainerRow
    {
        /* Fields */

        private ArrayList<ConfigRowList.Row> cache;
        private ContainerButton controller;

        private final Enum<?> id;
        private final Component title;
        private final Supplier<ArrayList<ConfigRowList.Row>> childrenSupply;
        private final ContainerType containerType;
        private final ConfigRowList list;

        private boolean expanded = false;

        /* Constructors */

        /**
         * Create a new container row with the given container type.
         * @param title A translatable component for the row's display title.
         * @param childrenSupply A supplier that provides an array list of config row list row instances.
         * @param id An enumeration identifier for this container row instance.
         * @param containerType The type of container this row instance is associated with.
         */
        public ContainerRow
        (
            Component title,
            Supplier<ArrayList<ConfigRowList.Row>> childrenSupply,
            Enum<?> id,
            ContainerType containerType
        )
        {
            this.id = id;
            this.title = title;
            this.childrenSupply = childrenSupply;
            this.containerType = containerType;
            this.list = ConfigRowList.getInstance();
        }

        /**
         * Create a new container row instance with a category container type.
         * @param title A translatable component for the row's display title.
         * @param childrenSupply A supplier that provides an array list of config row list row instances.
         * @param id An enumeration identifier for this container row instance.
         */
        public ContainerRow(Component title, Supplier<ArrayList<ConfigRowList.Row>> childrenSupply, Enum<?> id)
        {
            this(title, childrenSupply, id, ContainerType.CATEGORY);
        }

        /* Methods */

        /**
         * Get an indentation value based on the provided container type.
         * @param containerType The container to get an indent value from.
         * @return An integer that defines where text should start from the left side.
         */
        public static int getIndent(ContainerType containerType)
        {
            return switch (containerType)
            {
                case CATEGORY -> ConfigRowList.TEXT_START;
                case SUBCATEGORY -> ConfigRowList.CAT_TEXT_START;
                case EMBEDDED -> ConfigRowList.SUB_TEXT_START;
            };
        }

        /**
         * @return Check if the current row is expanded.
         */
        public boolean isExpanded() { return this.expanded; }

        /**
         * Create the metadata for this category row.
         * This data defines flags needed for proper tree rendering.
         */
        private void setGroupMetadata()
        {
            // If the parent group contains only subcategories then we don't want pipe bars in the last subcategory
            for (ConfigRowList.Row categories : this.list.children())
            {
                for (Renderable renderable : categories.children)
                {
                    // Check if parent group
                    if (renderable instanceof ContainerButton group && this.controller.equals(group))
                    {
                        // Ensure children only consist of subcategories
                        ContainerButton subcategory = null;
                        boolean isSubOnly = true;

                        for (ConfigRowList.Row subcategories : this.cache)
                        {
                            for (Renderable subRenderable : subcategories.children)
                            {
                                if (subRenderable instanceof ContainerButton subGroup)
                                    subcategory = subGroup;
                                else
                                {
                                    isSubOnly = false;
                                    break;
                                }
                            }

                            if (!isSubOnly)
                                break;
                        }

                        // If parent group only has subcategories, then tell the last subgroup to not display pipe bars
                        if (isSubOnly && subcategory != null)
                            subcategory.setLastSubcategory(true);

                        // If category group has tweaks at the end, a category bar is needed for embedded tree rendering
                        if ((!isSubOnly || group.isParentTreeNeeded()) && subcategory != null)
                            subcategory.setParentTreeNeeded(true);

                        // If category group has additional children then show grandparent pipe bars for embedded rows
                        if (group.getId() instanceof TweakEmbed)
                        {
                            if (categories.getGroup() != null && !categories.getGroup().isLastSubcategory())
                                group.setGrandparentTreeNeeded(true);
                        }
                    }
                }
            }
        }

        /**
         * @return Determine the index of where this category row resides within the children array of the main config
         * row list.
         */
        private int getHeaderIndex()
        {
            int header = -1;
            for (int i = 0; i < this.list.children().size(); i++)
            {
                if (header != -1)
                    break;

                for (Renderable renderable : this.list.children().get(i).children)
                {
                    if (renderable instanceof ContainerButton && renderable.equals(this.controller))
                    {
                        header = i;
                        break;
                    }
                }
            }

            return header == -1 ? 0 : ++header;
        }

        /**
         * Expand this category row so that its subscribed rows become visible.
         */
        public void expand()
        {
            int indent = getIndent(this.containerType) + 20;
            ConfigRowList.currentIndent = indent;

            this.expanded = true;
            this.cache = this.childrenSupply.get();

            this.setGroupMetadata();

            int header = this.getHeaderIndex();

            for (ConfigRowList.Row row : this.cache)
            {
                this.list.children().add(header, row);

                row.setIndent(indent);
                row.setGroup(this.controller);

                header++;
            }

            if (this.cache.size() > 0)
            {
                this.cache.get(0).setFirst(true);
                this.cache.get(this.cache.size() - 1).setLast(true);
            }

            ConfigRowList.currentIndent = ConfigRowList.TEXT_START;
        }

        /**
         * Collapse this category row so that its subscribed rows become invisible.
         */
        public void collapse()
        {
            if (this.cache == null)
                return;

            this.expanded = false;

            for (ConfigRowList.Row cache : this.cache)
            {
                for (ConfigRowList.Row child : this.list.children())
                {
                    if (child.equals(cache))
                    {
                        // Collapse any subcategories within the category
                        for (Renderable renderable : child.children)
                        {
                            if (renderable instanceof ContainerButton group)
                                group.collapse();
                        }

                        this.list.removeRow(child);
                        break;
                    }
                }
            }

            if (this.list.getScrollAmount() > 0.0D)
                this.list.setScrollAmount(this.list.getScrollAmount());
        }

        /**
         * @return Creates a new row instance so that it can be added to the config row list.
         */
        public ConfigRowList.Row generate()
        {
            List<Renderable> widgets = new ArrayList<>();
            this.controller = new ContainerButton(this, this.id, this.title, this.containerType);

            widgets.add(this.controller);

            return new ConfigRowList.Row(widgets, this.controller, null);
        }
    }
}
