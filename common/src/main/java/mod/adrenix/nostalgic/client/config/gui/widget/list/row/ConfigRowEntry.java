package mod.adrenix.nostalgic.client.config.gui.widget.list.row;

import mod.adrenix.nostalgic.client.config.gui.screen.list.ListMapScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.button.DeleteButton;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ResetButton;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.slider.GenericSlider;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextTitle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entry Row Templates
 *
 * The following classes are used to generate configuration rows based on a specific value associated with a list config
 * entry. Any unknown types will be defaulted to an invalid row instance.
 */

public abstract class ConfigRowEntry
{
    /**
     * Template class for value-specific rows.
     * @param <T> The type of the controlled value that will be handled.
     */
    private abstract static class AbstractEntryRow<T>
    {
        /* Fields */

        protected final ListMapScreen<T> screen;
        protected final Map.Entry<String, T> entry;
        protected final T reset;
        protected final T undo;

        /* Constructor */

        /**
         * Generate a new abstract row.
         * @param entry The list entry associated with this row.
         */
        @SuppressWarnings("unchecked") // The map screen uses the same type as its map entry values
        protected AbstractEntryRow(Map.Entry<String, T> entry, T reset)
        {
            this.entry = entry;
            this.reset = reset;
            this.screen = (ListMapScreen<T>) Minecraft.getInstance().screen;

            if (this.screen == null)
                throw new RuntimeException("Could not create an abstract entry row since 'screen' is 'null'");

            this.undo = this.screen.getCopiedValue(this.entry);
        }

        /* Methods */

        /**
         * Performs reset instructions when the reset button is pressed.
         * @param controller The abstract widget controller for this row.
         */
        private void onReset(AbstractWidget controller)
        {
            this.screen.copy(this.entry);
            this.entry.setValue(this.isChanged() ? this.undo : this.reset);

            if (!this.isChanged())
                this.screen.copy(this.entry);

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
        public InvalidEntryRow(Map.Entry<String, Object> entry, Object reset) { super(entry, reset); }

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
        public IntegerEntryRow(Map.Entry<String, Integer> entry, int reset) { super(entry, reset); }

        @Override
        public ConfigRowList.Row generate()
        {
            GenericSlider slider = new GenericSlider
            (
                this.entry::setValue,
                this.entry::getValue,
                null,
                ConfigRowList.getInstance().getRowWidth() - 206,
                ConfigRowList.BUTTON_START_Y,
                ConfigRowList.BUTTON_WIDTH,
                ConfigRowList.BUTTON_HEIGHT
            );

            return this.create(slider);
        }
    }
}
