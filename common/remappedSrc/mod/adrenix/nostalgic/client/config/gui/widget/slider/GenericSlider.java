package mod.adrenix.nostalgic.client.config.gui.widget.slider;

import mod.adrenix.nostalgic.client.config.annotation.TweakClient;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericSlider extends SliderWidget
{
    protected TweakClient.Gui.Slider slider = TweakClient.Gui.Slider.SWING_SLIDER;
    protected int min = ClientConfig.MIN;
    protected int max = ClientConfig.MAX;
    protected final Consumer<Integer> setCurrent;
    protected final Supplier<Integer> current;
    @Nullable protected final Supplier<String> text;

    public GenericSlider(Consumer<Integer> setCurrent, Supplier<Integer> current, @Nullable Supplier<String> text, int x, int y, int width, int height)
    {
        super(x, y, width, height, Text.empty(), (double) current.get());

        this.setCurrent = setCurrent;
        this.current = current;
        this.text = text;
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

    protected Formatting getColorFromInt()
    {
        Formatting color = Formatting.GREEN;
        int integer = this.current.get();

        if (this.slider == TweakClient.Gui.Slider.SWING_SLIDER)
        {
            if (integer == DefaultConfig.Swing.DISABLED) color = Formatting.RED;
            else if (integer == DefaultConfig.Swing.PHOTOSENSITIVE) color = Formatting.YELLOW;
            else if (integer <= DefaultConfig.Swing.NEW_SPEED) color = Formatting.GOLD;
        }
        else if (this.slider == TweakClient.Gui.Slider.INTENSITY_SLIDER)
        {
            if (integer == 0) color = Formatting.RED;
            else if (integer <= 50) color = Formatting.GOLD;
            else if (integer > 100) color = Formatting.AQUA;
        }
        else if (this.slider == TweakClient.Gui.Slider.CLOUD_SLIDER)
        {
            if (integer == 128) color = Formatting.YELLOW;
            else if (integer == 192) color = Formatting.GOLD;
            else color = Formatting.LIGHT_PURPLE;
        }

        return color;
    }

    @Override protected void applyValue() { this.setCurrent.accept((int) (this.min + Math.abs(this.max - this.min) * this.value)); }
    @Override
    public void updateMessage()
    {
        Formatting color = this.getColorFromInt();
        String header = "";
        String suffix = "";

        if (this.text != null)
            header = this.text.get();
        else if (this.slider == TweakClient.Gui.Slider.SWING_SLIDER)
            header = Text.translatable(NostalgicLang.Gui.SETTINGS_SPEED).getString();
        else if (this.slider == TweakClient.Gui.Slider.INTENSITY_SLIDER)
        {
            header = Text.translatable(NostalgicLang.Gui.SETTINGS_INTENSITY).getString();
            suffix = "%";
        }
        else if (this.slider == TweakClient.Gui.Slider.CLOUD_SLIDER)
        {
            int height = this.current.get();
            if (height == 108) header = Text.translatable(NostalgicLang.Gui.SETTINGS_ALPHA).getString();
            else if (height == 128) header = Text.translatable(NostalgicLang.Gui.SETTINGS_BETA).getString();
            else if (height == 192) header = Text.translatable(NostalgicLang.Gui.SETTINGS_MODERN).getString();
            else header = Text.translatable(NostalgicLang.Gui.SETTINGS_CUSTOM).getString();
        }

        String text = header + ": " + (this.active ? color : Formatting.GRAY) + this.current.get().toString() + suffix;
        this.setMessage(Text.literal(text));
    }

    public void setValue(int value)
    {
        this.value = (MathHelper.clamp(value, this.min, this.max) - this.min) / (double) Math.abs(this.max - this.min);
    }
}