package mod.adrenix.nostalgic.client.gui.widget.slider;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicField;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicFunction;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetCache;
import mod.adrenix.nostalgic.util.common.data.CacheValue;

import java.util.List;

class ValueSync<Builder extends AbstractSliderMaker<Builder, Slider>, Slider extends AbstractSlider<Builder, Slider>>
    implements DynamicFunction<Builder, Slider>
{
    final CacheValue<Double> value;

    ValueSync(Slider slider)
    {
        this.value = CacheValue.create(slider::getValue);
    }

    @Override
    public void apply(Slider slider, Builder builder)
    {
        slider.setValue(slider.getValue());
        slider.applyTitle();
    }

    @Override
    public boolean isReapplyNeeded(Slider slider, Builder builder, WidgetCache cache)
    {
        return this.value.isExpired();
    }

    @Override
    public List<DynamicField> getManaging(Builder builder)
    {
        return List.of();
    }
}
