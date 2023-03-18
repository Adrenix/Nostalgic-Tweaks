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

    private static String defaultFormatting(Number input)
    {
        return input.toString();
    }

    public SliderType getType()
    {
        return this.type;
    }

    public String getLangKey()
    {
        return this.langKey;
    }

    public String getSuffix()
    {
        return this.suffix;
    }

    public Number getValue()
    {
        return this.value;
    }

    public Number getMin()
    {
        return this.min;
    }

    public Number getMax()
    {
        return this.max;
    }

    public Number getInterval()
    {
        return this.interval;
    }

    public Function<Number, String> getFormatter()
    {
        return this.formatter;
    }

    /* Builder */

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

        public Builder(Number value, Number min, Number max, Number interval)
        {
            this.value = value;
            this.min = min;
            this.max = max;
            this.interval = interval;
        }

        public Builder formatter(Function<Number, String> formatter)
        {
            this.formatter = formatter;
            return this;
        }

        public Builder langKey(String langKey)
        {
            this.langKey = langKey;
            return this;
        }

        public Builder suffix(String suffix)
        {
            this.suffix = suffix;
            return this;
        }

        public Builder type(SliderType type)
        {
            this.type = type;
            return this;
        }

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
