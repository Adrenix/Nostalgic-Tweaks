package mod.adrenix.nostalgic.tweak.gui;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Function;

/**
 * This is a server-safe class used by tweak builders. The client user interface will pull from this class when
 * rendering the sliders in the client's menu.
 */
public class TweakSlider
{
    /* Fields */

    private final TweakSlider.Builder builder;

    /* Constructor */

    private TweakSlider(TweakSlider.Builder builder)
    {
        this.builder = builder;
    }

    /* Methods */

    /**
     * Default number string formatting for printing out the current slider value.
     *
     * @param input Current number.
     * @return A string using the number's default toString value.
     */
    private static String defaultFormatting(Number input)
    {
        return input.toString();
    }

    /**
     * Each slider type will change how a slider renders.
     *
     * @return The current slider type.
     */
    public SliderType getType()
    {
        return this.builder.type;
    }

    /**
     * Each slider can have a custom title if a lang key is defined.
     *
     * @return The current lang file key for this slider.
     */
    public MutableComponent getTranslation()
    {
        return this.builder.langKey.get();
    }

    /**
     * Get the current slider suffix.
     *
     * @return A slider suffix that is added to the end of a slider's printed out value.
     */
    public String getSuffix()
    {
        return this.builder.suffix;
    }

    /**
     * @return Get the minimum value allowed for this slider.
     */
    public Number getMin()
    {
        return this.builder.min;
    }

    /**
     * @return Get the maximum value allowed for this slider.
     */
    public Number getMax()
    {
        return this.builder.max;
    }

    /**
     * @return Get the interval value delta for this slider.
     */
    public Number getInterval()
    {
        return this.builder.interval;
    }

    /**
     * @return Get the round to place value for this slider.
     */
    public int getRoundTo()
    {
        return this.builder.roundTo;
    }

    /**
     * @return Get the value output formatter function for this slider.
     */
    public Function<Number, String> getFormatter()
    {
        return this.builder.formatter;
    }

    /* Builder */

    /**
     * Begin the process of building a new tweak slider that will be used in the configuration menu.
     *
     * @param min      The minimum value allowed for this slider.
     * @param max      The maximum value allowed for this slider.
     * @param interval The interval change used when the slider is scrolled or changed by key input.
     * @return A new tweak slider factory builder.
     */
    public static Builder create(Number min, Number max, Number interval)
    {
        return new Builder(min, max, interval);
    }

    /**
     * Begin the process of building a new {@code (int)} tweak slider that will be used in the configuration menu. This
     * is an overload method that sets the slider skip interval to {@code (int) 1}. The interval change is used when the
     * slider is scrolled or changed by key input.
     *
     * @param min The minimum value allowed for this slider.
     * @param max The maximum value allowed for this slider.
     * @return A new tweak slider factory builder.
     */
    @PublicAPI
    public static Builder create(int min, int max)
    {
        return create(min, max, 1);
    }

    /**
     * Begin the process of building a new {@code (long)} tweak slider that will be used in the configuration menu. This
     * is an overload method that sets the slider interval to {@code (long) 1L}. The interval change is used when the
     * slider is scrolled or changed by key input.
     *
     * @param min The minimum value allowed for this slider.
     * @param max The maximum value allowed for this slider.
     * @return A new tweak slider factory builder.
     */
    @PublicAPI
    public static Builder create(long min, long max)
    {
        return create(min, max, 1L);
    }

    /**
     * Begin the process of building a new tweak slider. The {@code defaultNumber} will be set as the {@code min} and
     * {@code max}, additionally the {@code interval} will be set to {@code 1L}. The min/max range and the interval
     * number can be changed later in the building process using {@link Builder#range(Number, Number)} and
     * {@link Builder#interval(Number)} respectively.
     *
     * @param defaultNumber The default starting number.
     * @return A new tweak slider factory builder.
     */
    @PublicAPI
    public static Builder create(Number defaultNumber)
    {
        return create(defaultNumber, defaultNumber, 1L);
    }

    /**
     * Contract for a builder whose purpose is to build a tweak slider.
     *
     * @param <Builder> The builder implementing this interface.
     */
    public interface Factory<Builder>
    {
        /**
         * Change the min/max range of the slider.
         *
         * @param min The minimum number.
         * @param max The maximum number.
         */
        Builder range(Number min, Number max);

        /**
         * Change the interval of the slider.
         *
         * @param interval The interval number.
         */
        Builder interval(Number interval);

        /**
         * Set a round to place value for this slider. The default is {@code 2}, which is the hundredth place.
         *
         * @param place The place to round to. For example, {@code 1} represents the tenths' place.
         */
        Builder roundTo(int place);

        /**
         * Define a custom string value for this slider.
         *
         * @param formatter A function that takes in the current value and outputs a formatted string to show on the
         *                  slider.
         */
        Builder formatter(Function<Number, String> formatter);

        /**
         * If a prefix is need in front of the value, then provide a lang key.
         *
         * @param langKey A lang key.
         */
        Builder langKey(Translation langKey);

        /**
         * Define a suffix to add to the end of a slider value, such as a % sign.
         *
         * @param suffix A slider value suffix.
         */
        Builder suffix(String suffix);

        /**
         * Define a custom slider type which changes the visual style of the slider.
         *
         * @param type A slider type.
         * @see SliderType
         */
        Builder type(SliderType type);

        /**
         * Set a title, minimum, and maximum.
         *
         * @param title The slider title.
         * @param min   The slider minimum.
         * @param max   The slider maximum.
         */
        default Builder slider(Translation title, Number min, Number max)
        {
            this.langKey(title);
            return this.range(min, max);
        }

        /**
         * Set a title, minimum, and maximum.
         *
         * @param title  The slider title.
         * @param min    The slider minimum.
         * @param max    The slider maximum.
         * @param suffix The slider suffix that is printed after the current value number.
         */
        default Builder slider(Translation title, Number min, Number max, String suffix)
        {
            this.range(min, max);
            this.langKey(title);

            return this.suffix(suffix);
        }

        /**
         * Set a minimum, maximum, interval, and slider type.
         *
         * @param min        The slider minimum.
         * @param max        The slider maximum.
         * @param interval   The slider interval.
         * @param sliderType The {@link SliderType}.
         */
        default Builder slider(Number min, Number max, Number interval, SliderType sliderType)
        {
            this.range(min, max, interval);
            return this.type(sliderType);
        }

        /**
         * Set a minimum, maximum, and slider type.
         *
         * @param min        The slider minimum.
         * @param max        The slider maximum.
         * @param sliderType The {@link SliderType}.
         */
        default Builder slider(Number min, Number max, SliderType sliderType)
        {
            this.range(min, max);
            return this.type(sliderType);
        }

        /**
         * Set a min, max, and interval.
         *
         * @param min      The slider minimum.
         * @param max      The slider maximum.
         * @param interval The slider interval.
         */
        default Builder range(Number min, Number max, Number interval)
        {
            this.range(min, max);
            return this.interval(interval);
        }

        default Builder apply(Function<Factory<Builder>, Builder> function)
        {
            return function.apply(this);
        }
    }

    public static class Builder implements Factory<Builder>
    {
        private Number min;
        private Number max;
        private Number interval;
        private int roundTo;

        private Function<Number, String> formatter = TweakSlider::defaultFormatting;
        private SliderType type = SliderType.GENERIC;

        private Translation langKey = new Translation("");
        private String suffix = "";

        private Builder(Number min, Number max, Number interval)
        {
            this.min = min;
            this.max = max;
            this.interval = interval;
            this.roundTo = 2;
        }

        @Override
        public Builder range(Number min, Number max)
        {
            this.min = min;
            this.max = max;

            return this;
        }

        @Override
        public Builder interval(Number interval)
        {
            this.interval = interval;
            return this;
        }

        @Override
        public Builder roundTo(int place)
        {
            this.roundTo = place;
            return this;
        }

        @Override
        public Builder formatter(Function<Number, String> formatter)
        {
            this.formatter = formatter;
            return this;
        }

        @Override
        public Builder langKey(Translation langKey)
        {
            this.langKey = langKey;
            return this;
        }

        @Override
        public Builder suffix(String suffix)
        {
            this.suffix = suffix;
            return this;
        }

        @Override
        public Builder type(SliderType type)
        {
            this.type = type;
            return this;
        }

        /**
         * Finish the building process of this slider.
         *
         * @return A new tweak slider instance.
         */
        public TweakSlider build()
        {
            if (this.min == null)
                throw new NullPointerException("Minimum slider bound cannot be null");

            if (this.max == null)
                throw new NullPointerException("Maximum slider bound cannot be null");

            if (this.interval == null)
                throw new NullPointerException("Slider interval cannot be null");

            return new TweakSlider(this);
        }
    }
}
