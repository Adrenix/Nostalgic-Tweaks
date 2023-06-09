package mod.adrenix.nostalgic.client.config.gui.widget.list.row;

import mod.adrenix.nostalgic.client.config.gui.screen.list.ListMapScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.button.DeleteButton;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ResetButton;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.slider.GenericSlider;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextTitle;
import mod.adrenix.nostalgic.common.config.list.ListMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Map Entry Row Templates
 *
 * The following classes are used to generate configuration rows based on a specific value associated with a list config
 * map entry. Any unknown types will be defaulted to an invalid row instance.
 */

public abstract class ConfigRowEntry
{
    /**
     * Template class for value-specific rows.
     * @param <V> The type of the controlled value that will be handled.
     */
    private abstract static class AbstractEntryRow<V>
    {
        /* Fields */

        protected final ListMap<V> listMap;
        protected final ListMapScreen<V> screen;
        protected final Map.Entry<String, V> entry;
        protected final V reset;
        protected final V undo;

        /* Constructor */

        /**
         * Generate a new abstract row.
         * @param entry The list entry associated with this row.
         */
        @SuppressWarnings("unchecked") // The map screen uses the same type as its map entry values
        protected AbstractEntryRow(ListMap<V> listMap, Map.Entry<String, V> entry, V reset)
        {
            this.listMap = listMap;
            this.entry = entry;
            this.reset = reset;
            this.screen = (ListMapScreen<V>) Minecraft.getInstance().screen;

            if (this.screen == null)
                throw new RuntimeException("Could not create an abstract entry row since 'screen' is 'null'");

            this.undo = this.screen.getCachedValue(this.entry);
        }

        /* Methods */

        /**
         * Performs reset instructions when the reset button is pressed.
         * @param controller The abstract widget controller for this row.
         */
        private void onReset(AbstractWidget controller)
        {
            this.entry.setValue(this.isChanged() ? this.undo : this.reset);

            if (controller instanceof GenericSlider slider)
                slider.update();
        }

        /**
         * Determines of the current entry value has changed from its undo value.
         * @return Whether the current entry has changed.
         */
        private boolean isChanged()
        {
            if (this.entry.getValue() instanceof Integer && this.undo instanceof Integer)
                return ((Integer) this.entry.getValue()).compareTo((Integer) this.undo) != 0;

            return !this.entry.equals(this.undo);
        }

        /**
         * Determines if this entry is contained within this screen's deleted entries map.
         * @return Whether this entry should be considered as deleted.
         */
        private boolean isDeleted() { return this.screen.isDeleted(this.entry); }

        /**
         * Deletes this entry by adding it to the screen's deleted entries map.
         */
        private void onDelete() { this.screen.delete(this.entry); }

        /**
         * Undoes the deletion of an entry by removing it from the screen's deleted entries map.
         */
        private void onUndo() { this.screen.undo(this.entry); }

        /**
         * Create a new config row list row instance for a map entry contained within a list map screen.
         * @param controller The main widget controller for this row.
         * @return A new config row list row instance.
         */
        protected ConfigRowList.Row create(AbstractWidget controller)
        {
            List<AbstractWidget> widgets = new ArrayList<>();

            ResetButton reset = new ResetButton(controller, this::isChanged, this::onReset);
            DeleteButton delete = new DeleteButton(reset, this::isDeleted, this::onDelete, this::onUndo);

            widgets.add(new TextTitle<>(this.screen, this.entry, this.entry.getKey()));
            widgets.add(controller);
            widgets.add(reset);
            widgets.add(delete);

            ConfigRowList.Row row = new ConfigRowList.Row(widgets, controller, null);

            row.setHighlight(true);
            row.setResourceKey(this.entry.getKey());

            return row;
        }

        /**
         * Create a new config row list row instance based on the provided array list of widgets and the row's entry
         * value.
         *
         * @return A new config row list row instance.
         */
        public ConfigRowList.Row generate() { return new ConfigRowList.Row(new ArrayList<>(), null); }
    }

    /**
     * This row is used when the provided value does not have a specific handler.
     */
    public static class InvalidEntryRow extends AbstractEntryRow<Object>
    {
        /**
         * Creates an empty row within a row list.
         * This is used by automatic generation and has no manual use cases.
         *
         * @param entry The entry associated with this row.
         */
        public InvalidEntryRow(ListMap<Object> listMap, Map.Entry<String, Object> entry, Object reset)
        {
            super(listMap, entry, reset);
        }

        @Override
        public ConfigRowList.Row generate() { return super.generate(); }
    }

    /**
     * This row type is used when the provided value is an integer type.
     */
    public static class IntegerEntryRow extends AbstractEntryRow<Integer>
    {
        /**
         * Create a row within a row list that has an integer slider widget controller.
         * This is used by automatic generation and has manual use cases.
         *
         * @param entry The integer valued entry associated with this row.
         */
        public IntegerEntryRow(ListMap<Integer> listMap, Map.Entry<String, Integer> entry, int reset)
        {
            super(listMap, entry, reset);
        }

        /**
         * Create a row with widgets associated with a list map that has integer values.
         * @return A config row list row instance.
         */
        @Override
        public ConfigRowList.Row generate()
        {
            GenericSlider slider = new GenericSlider
            (
                this.entry::setValue,
                this.entry::getValue,
                this.listMap.getTweak(),
                ConfigRowList.getInstance().getRowWidth() - 206,
                ConfigRowList.BUTTON_START_Y,
                ConfigRowList.BUTTON_WIDTH,
                ConfigRowList.BUTTON_HEIGHT
            );

            return this.create(slider);
        }
    }
}
