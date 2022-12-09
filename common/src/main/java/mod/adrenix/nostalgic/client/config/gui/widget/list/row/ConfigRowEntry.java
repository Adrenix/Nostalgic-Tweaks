package mod.adrenix.nostalgic.client.config.gui.widget.list.row;

import mod.adrenix.nostalgic.client.config.gui.screen.list.ListMapScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.button.DeleteButton;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ResetButton;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.slider.GenericSlider;
import mod.adrenix.nostalgic.client.config.gui.widget.text.TextTitle;
import mod.adrenix.nostalgic.util.common.ClassUtil;
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

        protected final Map.Entry<String, T> entry;
        protected final T reset;

        /* Constructor */

        /**
         * Generate a new abstract row.
         * @param entry The list entry associated with this row.
         */
        protected AbstractEntryRow(Map.Entry<String, T> entry, T reset)
        {
            this.entry = entry;
            this.reset = reset;
        }

        /* Methods */

        /**
         * Create a new config row list row instance.
         * @param controller The main widget controller for this row.
         * @return A new config row list row instance.
         */
        @SuppressWarnings("unchecked") // The map screen uses the same type as its map entry values.
        protected ConfigRowList.Row create(AbstractWidget controller)
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            ListMapScreen<T> screen = (ListMapScreen<T>) Minecraft.getInstance().screen;

            if (ClassUtil.isNotInstanceOf(screen, ListMapScreen.class))
                return new ConfigRowList.Row(widgets, controller, null);

            TextTitle<T> textTitle = new TextTitle<>(screen, this.entry, this.entry.getKey());
            T undoValue = screen.getCopiedValue(this.entry);

            ResetButton resetButton = new ResetButton
            (
                screen,
                controller,
                this.entry,
                this.reset,
                undoValue,
                () ->
                {
                    if (this.entry.getValue() instanceof Integer && undoValue instanceof Integer)
                        return ((Integer) this.entry.getValue()).compareTo((Integer) undoValue) != 0;

                    return !this.entry.equals(undoValue);
                }
            );

            DeleteButton deleteButton = new DeleteButton
            (
                this.entry,
                resetButton,
                () -> screen.isDeleted(this.entry),
                (entry) -> screen.delete((Map.Entry<String, T>) entry),
                (entry) -> screen.undo((Map.Entry<String, T>) entry)
            );

            widgets.add(textTitle);
            widgets.add(controller);
            widgets.add(resetButton);
            widgets.add(deleteButton);

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
