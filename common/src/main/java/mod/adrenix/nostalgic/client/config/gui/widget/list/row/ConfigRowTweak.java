package mod.adrenix.nostalgic.client.config.gui.widget.list.row;

import mod.adrenix.nostalgic.client.config.annotation.TweakGui;
import mod.adrenix.nostalgic.client.config.gui.widget.TweakTag;
import mod.adrenix.nostalgic.client.config.gui.widget.button.*;
import mod.adrenix.nostalgic.client.config.gui.widget.input.ColorInput;
import mod.adrenix.nostalgic.client.config.gui.widget.input.StringInput;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.gui.widget.slider.ConfigSlider;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * Tweak Row Templates
 *
 * The following classes are used to generate configuration rows based on a value associated with a tweak. Any unhandled
 * types will be defaulted to an invalid row instance.
 */

public abstract class ConfigRowTweak
{
    /**
     * Template class for value-specific rows.
     * @param <T> The type of the controlled value that will be handled.
     */
    private abstract static class AbstractRow<T>
    {
        /* Fields */

        protected final TweakClientCache<T> tweak;
        protected final TweakGroup group;
        protected final String key;
        protected final T value;

        /* Constructor */

        /**
         * Generate a new abstract row.
         * @param group The group type associated with this row.
         * @param key The tweak cache key associated with this row.
         * @param value The tweak value associated with this row.
         */
        protected AbstractRow(TweakGroup group, String key, T value)
        {
            this.tweak = TweakClientCache.get(group, key);
            this.group = group;
            this.key = key;
            this.value = value;
        }

        /* Methods */

        /**
         * Create a new config row list row instance.
         * @param controller The main widget controller for this row.
         * @return A new config row list row instance.
         */
        protected ConfigRowList.Row create(AbstractWidget controller)
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            TweakGui.NoTooltip noTooltip = this.tweak.getMetadata(TweakGui.NoTooltip.class);

            widgets.add(controller);
            widgets.add(new ResetButton(this.tweak, controller));
            widgets.add(new StatusButton(this.tweak, controller));
            widgets.add(new TweakTag(this.tweak, controller, noTooltip == null));

            if (noTooltip == null)
                widgets.add(new TooltipButton(this.tweak, controller));

            if (controller instanceof ColorInput color)
                widgets.add(color.getWidget());

            return new ConfigRowList.Row(widgets, controller, this.tweak);
        }

        /**
         * Create a new config row list row instance based on the provided array list of widgets and the row's tweak
         * cache.
         *
         * @return A new config row list row instance.
         */
        public ConfigRowList.Row generate() { return new ConfigRowList.Row(new ArrayList<>(), this.tweak); }
    }

    /**
     * This row is used when the provided value does not have a specific handler.
     */
    public static class InvalidRow extends AbstractRow<Object>
    {
        /**
         * Creates an empty row within a row list.
         * This is used by automatic generation and has no manual use cases.
         *
         * @param group The group type associated with a tweak.
         * @param key A tweak client cache map identifier.
         * @param value The value associated with a tweak.
         */
        public InvalidRow(TweakGroup group, String key, Object value) { super(group, key, value); }

        @Override
        public ConfigRowList.Row generate() { return new ConfigRowList.Row(new ArrayList<>(), this.tweak); }
    }

    /**
     * This row type is used when the provided value is a boolean type.
     */
    public static class BooleanRow extends AbstractRow<Boolean>
    {
        /**
         * Create a row within a row list that has a boolean widget controller.
         * This is used by automatic generation and has manual use cases.
         *
         * @param group The group type associated with a tweak.
         * @param key A tweak client cache map identifier.
         * @param value The value associated with a tweak.
         */
        public BooleanRow(TweakGroup group, String key, boolean value) { super(group, key, value); }

        @Override
        public ConfigRowList.Row generate()
        {
            return this.create(new BooleanButton(this.tweak, (button) -> this.tweak.setValue(!this.tweak.getValue())));
        }
    }

    /**
     * This row type is used when the provided value is an integer type.
     */
    public static class IntSliderRow extends AbstractRow<Integer>
    {
        /**
         * Create a row within a row list that has an integer slider widget controller.
         * This is used by automatic generation and has manual use cases.
         *
         * @param group The group type associated with a tweak.
         * @param key A tweak client cache map identifier.
         * @param value The value associated with a tweak.
         */
        public IntSliderRow(TweakGroup group, String key, int value) { super(group, key, value); }

        @Override
        public ConfigRowList.Row generate() { return this.create(new ConfigSlider(this.tweak)); }
    }

    /**
     * This row type is used when the provided value is a generic string type.
     */
    public static class StringRow extends AbstractRow<String>
    {
        /**
         * Create a row within a row list that has a text input box widget controller.
         * This is used by automatic generation and has manual use cases.
         *
         * @param group The group type associated with a tweak.
         * @param key A tweak client cache map identifier.
         * @param value The value associated with a tweak.
         */
        public StringRow(TweakGroup group, String key, String value) { super(group, key, value); }

        @Override
        public ConfigRowList.Row generate() { return this.create(new StringInput(this.tweak).getWidget()); }
    }

    /**
     * This row type is used when the provided string value has hex color metadata.
     */
    public static class ColorRow extends AbstractRow<String>
    {
        /**
         * Create a row within a row list that has a text input box with color specific input logic as a widget
         * controller. This is used by automatic generation and has manual use cases.
         *
         * @param group The group type associated with a tweak.
         * @param key A tweak client cache map identifier.
         * @param value The value associated with a tweak.
         */
        public ColorRow(TweakGroup group, String key, String value) { super(group, key, value); }

        @Override
        public ConfigRowList.Row generate() { return this.create(new ColorInput(this.tweak)); }
    }

    /**
     * This row type is used when the provided value can be various values within an enumeration.
     * @param <E> The value type of the enumeration.
     */
    public static class EnumRow<E extends Enum<E>> extends AbstractRow<E>
    {
        /**
         * Create a row within a row list that has an enumeration cycle button as a widget controller.
         * This is used by automatic generation and has manual use cases.
         *
         * @param group The group type associated with a tweak.
         * @param key A tweak client cache map identifier.
         * @param value The value associated with a tweak.
         */
        @SuppressWarnings("unchecked")
        public EnumRow(TweakGroup group, String key, Object value) { super(group, key, (E) value); }

        @Override
        public ConfigRowList.Row generate()
        {
            return this.create
            (
                new CycleButton<>
                (
                    this.tweak,
                    this.tweak.getValue().getDeclaringClass(),
                    (button) -> ((CycleButton<?>) button).toggle()
                )
            );
        }
    }
}
