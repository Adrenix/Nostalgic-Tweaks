package mod.adrenix.nostalgic.client.config.gui.widget.slider;

import mod.adrenix.nostalgic.client.config.annotation.TweakClient;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericSlider extends AbstractSliderButton
{
    protected TweakClient.Gui.Slider slider = TweakClient.Gui.Slider.SWING;
    protected int min = ClientConfig.MIN;
    protected int max = ClientConfig.MAX;
    protected final Consumer<Integer> setCurrent;
    protected final Supplier<Integer> current;
    @Nullable protected final Supplier<String> text;
    @Nullable protected final TweakClientCache<Integer> cache;
    @Nullable protected final TweakClient.Gui.SliderType sliderType;

    public GenericSlider(TweakClientCache<Integer> cache, @Nullable Supplier<String> text, int x, int y, int width, int height)
    {
        super(x, y, width, height, Component.empty(), (double) cache.getCurrent());

        this.sliderType = CommonReflect.getAnnotation(cache, TweakClient.Gui.SliderType.class);
        this.setCurrent = cache::setCurrent;
        this.current = cache::getCurrent;
        this.cache = cache;
        this.text = text;
        this.updateMessage();
        this.setValue(current.get());
    }

    public GenericSlider(Consumer<Integer> setCurrent, Supplier<Integer> current, @Nullable Supplier<String> text, int x, int y, int width, int height)
    {
        super(x, y, width, height, Component.empty(), (double) current.get());

        this.setCurrent = setCurrent;
        this.current = current;
        this.text = text;
        this.cache = null;
        this.sliderType = null;
        this.updateMessage();
        this.setValue(current.get());
    }

    public void setSlider(TweakClient.Gui.Slider slider) { this.slider = slider; }

    protected void setMinimum(int min)
    {
        this.min = min;
        this.setValue(this.current.get());
    }

    protected void setMaximum(int max)
    {
        this.max = max;
        this.setValue(this.current.get());
    }

    protected ChatFormatting getColorFromInt()
    {
        ChatFormatting color = ChatFormatting.GREEN;
        int integer = this.current.get();

        if (this.slider == TweakClient.Gui.Slider.SWING)
        {
            if (integer == DefaultConfig.Swing.DISABLED) color = ChatFormatting.RED;
            else if (integer == DefaultConfig.Swing.PHOTOSENSITIVE) color = ChatFormatting.YELLOW;
            else if (integer <= DefaultConfig.Swing.NEW_SPEED) color = ChatFormatting.GOLD;
        }
        else if (this.slider == TweakClient.Gui.Slider.INTENSITY)
        {
            if (integer == 0) color = ChatFormatting.RED;
            else if (integer <= 50) color = ChatFormatting.GOLD;
            else if (integer > 100) color = ChatFormatting.AQUA;
        }
        else if (this.slider == TweakClient.Gui.Slider.CLOUD)
        {
            if (integer == 128) color = ChatFormatting.YELLOW;
            else if (integer == 192) color = ChatFormatting.GOLD;
            else color = ChatFormatting.LIGHT_PURPLE;
        }
        else if (this.slider == TweakClient.Gui.Slider.GENERIC)
            color = ChatFormatting.RESET;

        return color;
    }

    @Override protected void applyValue() { this.setCurrent.accept((int) (this.min + Math.abs(this.max - this.min) * this.value)); }
    @Override
    public void updateMessage()
    {
        ChatFormatting color = this.getColorFromInt();
        String title = "";
        String suffix = "";

        if (this.text != null)
            title = this.text.get();
        else if (this.slider == TweakClient.Gui.Slider.SWING)
            title = Component.translatable(LangUtil.Gui.SETTINGS_SPEED).getString();
        else if (this.slider == TweakClient.Gui.Slider.INTENSITY)
        {
            title = Component.translatable(LangUtil.Gui.SETTINGS_INTENSITY).getString();
            suffix = "%";
        }
        else if (this.slider == TweakClient.Gui.Slider.CLOUD)
        {
            int height = this.current.get();
            if (height == 108) title = Component.translatable(LangUtil.Gui.SETTINGS_ALPHA).getString();
            else if (height == 128) title = Component.translatable(LangUtil.Gui.SETTINGS_BETA).getString();
            else if (height == 192) title = Component.translatable(LangUtil.Gui.SETTINGS_MODERN).getString();
            else title = Component.translatable(LangUtil.Gui.SETTINGS_CUSTOM).getString();
        }

        if (this.sliderType != null && !this.sliderType.langKey().isEmpty())
            title = Component.translatable(this.sliderType.langKey()).getString();

        if (this.sliderType != null && !this.sliderType.suffix().isEmpty())
            suffix = this.sliderType.suffix();

        String text = title + ": " + (this.active ? color : ChatFormatting.GRAY) + this.current.get().toString() + suffix;
        this.setMessage(Component.literal(text));
    }

    public void setValue(int value)
    {
        this.value = (Mth.clamp(value, this.min, this.max) - this.min) / (double) Math.abs(this.max - this.min);
    }
}