package mod.adrenix.nostalgic.common.config.v2.gui;

import java.util.function.Function;

public class TweakSlider
{
    /* Fields */

    private SliderType type;
    private String langKey;
    private String suffix;
    private Number value;
    private Number min;
    private Number max;
    private Number interval;
    private Function<Number, String> formatter;

    /* Constructor */

    private TweakSlider() { }

    /* Methods */

    /**
     * Default number string formatting for printing out the current slider value.
     * @param input Current number.
     * @return A string using the number's default toString value.
     */
    private static String defaultFormatting(Number input)
    {
        return input.toString();
    }

    /**
     * Each slider type will change how a slider renders.
     * @return The current slider type.
     */
    public SliderType getType()
    {
        return this.type;
    }

    /**
     * Each slider can have a custom title if a lang key is defined.
     * @return The current lang file key for this slider.
     */
    public String getLangKey()
    {
        return this.langKey;
    }

    /**
     * Get the current slider suffix.
     * @return A slider suffix that is added to the end of a slider's printed out value.
     */
    public String getSuffix()
    {
        return this.suffix;
    }

    /**
     * @return Get the current value of this slider.
     */
    public Number getValue()
    {
        return this.value;
    }

    /**
     * @return Get the minimum value allowed for this slider.
     */
    public Number getMin()
    {
        return this.min;
    }

    /**
     * @return Get the maximum value allowed for this slider.
     */
    public Number getMax()
    {
        return this.max;
    }

    /**
     * @return Get the interval value delta for this slider.
     */
    public Number getInterval()
    {
        return this.interval;
    }

    /**
     * @return Get the value output formatter function for this slider.
     */
    public Function<Number, String> getFormatter()
    {
        return this.formatter;
    }

    /* Builder */

    /**
     * Begin the process of building a new tweak slider that will be used in the configuration menu.
     * @param value The value this slider starts at.
     * @param min The minimum value allowed for this slider.
     * @param max The maximum value allowed for this slider.
     * @param interval The interval change used when the slider is moved by the mouse.
     * @return A new tweak slider factory builder.
     */
    public static Builder builder(Number value, Number min, Number max, Number interval)
    {
        return new Builder(value, min, max, interval);
    }

    public static class Builder
    {
        private final Number value;
        private final Number min;
        private final Number max;
        private final Number interval;

        private Function<Number, String> formatter = TweakSlider::defaultFormatting;
        private SliderType type = SliderType.GENERIC;

        private String langKey = "";
        private String suffix = "";

        private Builder(Number value, Number min, Number max, Number interval)
        {
            this.value = value;
            this.min = min;
            this.max = max;
            this.interval = interval;
        }

        /**
         * Define a custom string value for this slider.
         * @param formatter A function that takes in the current value and outputs a formatted string to show on the slider.
         * @see TweakSlider#formatter
         */
        @SuppressWarnings("unused")
        public Builder formatter(Function<Number, String> formatter)
        {
            this.formatter = formatter;
            return this;
        }

        /**
         * If you want to prefix the value with a custom message define a lang file key for this slider.
         * @param langKey A lang file key.
         * @see TweakSlider#langKey
         */
        public Builder langKey(String langKey)
        {
            this.langKey = langKey;
            return this;
        }

        /**
         * Define a suffix to add to the end of a slider value, such as a % sign.
         * @param suffix A slider value suffix.
         * @see TweakSlider#suffix
         */
        public Builder suffix(String suffix)
        {
            this.suffix = suffix;
            return this;
        }

        /**
         * Define a custom slider type which changes the visual style of the slider.
         * @param type A slider type.
         * @see SliderType
         * @see TweakSlider#type
         */
        public Builder type(SliderType type)
        {
            this.type = type;
            return this;
        }

        /**
         * Finish the building process of this slider.
         * @return A new tweak slider instance.
         */
        public TweakSlider build()
        {
            TweakSlider slider = new TweakSlider();

            slider.value = this.value;
            slider.min = this.min;
            slider.max = this.max;
            slider.interval = this.interval;
            slider.formatter = this.formatter;
            slider.type = this.type;
            slider.langKey = this.langKey;
            slider.suffix = this.suffix;

            return slider;
        }
    }
}
