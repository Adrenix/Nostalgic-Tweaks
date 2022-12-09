package mod.adrenix.nostalgic.client.config.gui.widget.list.row;

import mod.adrenix.nostalgic.client.config.gui.widget.button.KeyBindButton;
import mod.adrenix.nostalgic.client.config.gui.widget.button.ResetButton;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;

/**
 * Manual Row Builders
 *
 * The following classes and methods are used to manually construct rows within the config row list. This is useful when
 * the page has a predefined layout.
 */

public abstract class ConfigRowBuild
{
    /**
     * Generate a row that handles a key mapping.
     * @param mapping The key mapping this row will handle.
     */
    public record BindingRow(KeyMapping mapping)
    {
        /**
         * Create a new config row list row instance that has a key binding button as a widget controller.
         * @return A config row list row instance.
         */
        public ConfigRowList.Row generate()
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            KeyBindButton controller = new KeyBindButton(this.mapping);

            widgets.add(controller);
            widgets.add(new ResetButton(null, controller));

            return new ConfigRowList.Row(widgets, controller, null);
        }
    }

    /**
     * Generate a row that will have a single centered widget controller.
     * @param controller The widget controller.
     */
    public record SingleCenteredRow(AbstractWidget controller)
    {
        /**
         * Create a new config row list row instance with only the provided widget as the controller.
         * @return A config row list instance with only the provided widget centered.
         */
        public ConfigRowList.Row generate()
        {
            Screen screen = Minecraft.getInstance().screen;
            assert screen != null;

            List<AbstractWidget> widgets = new ArrayList<>();
            this.controller.x = (screen.width / 2) - (this.controller.getWidth() / 2);
            this.controller.y = 0;

            widgets.add(this.controller);

            return new ConfigRowList.Row(widgets, null);
        }
    }

    /**
     * Generate a row that will be aligned to the given indent value.
     * @param controller The widget controller.
     * @param indent The indent value that will be aligned from the left.
     */
    public record SingleLeftRow(AbstractWidget controller, int indent)
    {
        /**
         * Create a new config row list row instance with only the provided widget as the controller.
         * @return A config row list instance with only the provided widget aligned to the left.
         */
        public ConfigRowList.Row generate()
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            this.controller.x = indent;
            this.controller.y = 0;

            widgets.add(this.controller);

            return new ConfigRowList.Row(widgets, null);
        }
    }

    /**
     * Manually create a configuration row with a predefined list of widgets.
     * @param widgets The widgets that belong to this row.
     */
    public record ManualRow(List<AbstractWidget> widgets)
    {
        /**
         * Create a new config row list row instance with the widgets provided.
         * @return A config row list instance with widgets that are in order as provided by the list.
         */
        public ConfigRowList.Row generate() { return new ConfigRowList.Row(widgets, null); }
    }

    /**
     * Manually create an empty row within the configuration row list.
     */
    public record BlankRow()
    {
        /**
         * Create a new blank row. No widgets will be in the row.
         * The purpose of having a blank row is to provide spacing in a crowded area of the menu.
         *
         * @return A config row list instance with no widgets.
         */
        public ConfigRowList.Row generate() { return new ManualRow(new ArrayList<>()).generate(); }
    }
}
