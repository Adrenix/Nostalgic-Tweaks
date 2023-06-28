package mod.adrenix.nostalgic.client.config.gui.widget.list.row;

import mod.adrenix.nostalgic.client.config.gui.screen.list.ListScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.list.ListSetScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.button.RemoveButton;
import mod.adrenix.nostalgic.client.config.gui.widget.button.RemoveType;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextTitle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;

import java.util.ArrayList;
import java.util.List;

/**
 * Key Row Templates
 *
 * The following classes are used to generate configuration rows based on generic item resource key lists. Since these
 * lists contain only keys, only one type of row will be needed for these types of lists.
 */

public abstract class ConfigRowKey
{
    /**
     * This class is the only row type available for saved entries associated with a list set screen.
     * The structure of this class is to mirror that of other config row list row generator classes.
     */
    public static class SavedRow
    {
        /* Fields */

        private final String resourceKey;
        private final ListSetScreen screen;

        /* Constructor */

        /**
         * Generate a new saved row.
         * @param resourceKey The item resource key associated with this row.
         */
        public SavedRow(String resourceKey)
        {
            this.resourceKey = resourceKey;
            this.screen = (ListSetScreen) Minecraft.getInstance().screen;
        }

        /* Methods */

        /**
         * Determines if the current resource key has been removed.
         * @return Whether the current resource key is within the screen's deleted keys array list.
         */
        private boolean isRemoved() { return this.screen.isKeyDeleted(this.resourceKey); }

        /**
         * Adds this row's item resource key to the screen's deleted resource keys array list.
         */
        private void onRemove() { this.screen.delete(this.resourceKey); }

        /**
         * Undoes the removal of an item resource key.
         */
        private void onUndo() { this.screen.undo(this.resourceKey); }

        /**
         * Create a new config row list row instance for an item resource key contained within a list set screen.
         * @return A new config row list row instance.
         */
        public ConfigRowList.Row generate()
        {
            List<Renderable> widgets = new ArrayList<>();

            RemoveButton remove = new RemoveButton(RemoveType.SAVED, this.resourceKey, this::isRemoved, this::onRemove, this::onUndo);
            TextTitle<?> title = new TextTitle<>(RemoveType.SAVED, this.resourceKey, this::isRemoved);

            widgets.add(title);
            widgets.add(remove);

            ConfigRowList.Row row = new ConfigRowList.Row(widgets, null, null);

            row.setHighlight(true);
            row.setResourceKey(this.resourceKey);

            return row;
        }
    }

    /**
     * This class is the only row type available for default entries associated with a list set screen.
     * The structure of this class is to mirror that of other config row list row generator classes.
     */
    public static class DefaultRow
    {
        /* Fields */

        private final String resourceKey;
        private final ListScreen screen;

        /* Constructor */

        public DefaultRow(String resourceKey)
        {
            this.resourceKey = resourceKey;
            this.screen = (ListScreen) Minecraft.getInstance().screen;
        }

        /* Methods */

        /**
         * Determines if the current item resource key is contained within the disabled default item set.
         * @return Whether the current item resource key is disabled.
         */
        private boolean isRemoved() { return this.screen.isDefaultItemDisabled(this.resourceKey); }

        /**
         * Removes the item resource key from the screen's disabled default items set.
         */
        private void onEnable() { this.screen.enableDefaultItem(this.resourceKey); }

        /**
         * Adds the item resource key to the screen's disabled default items set.
         */
        private void onDisable() { this.screen.disableDefaultItem(this.resourceKey); }

        /**
         * Create a new config row list row instance for an item resource key contained within a default entries
         * container.
         *
         * @return A new config row list instance.
         */
        public ConfigRowList.Row generate()
        {
            List<Renderable> widgets = new ArrayList<>();

            RemoveButton remove = new RemoveButton(RemoveType.DEFAULT, this.resourceKey, this::isRemoved, this::onDisable, this::onEnable);
            TextTitle<?> title = new TextTitle<>(RemoveType.DEFAULT, this.resourceKey, this::isRemoved);

            widgets.add(title);
            widgets.add(remove);

            ConfigRowList.Row row = new ConfigRowList.Row(widgets, null, null);

            row.setHighlight(true);
            row.setResourceKey(this.resourceKey);

            return row;
        }
    }
}
