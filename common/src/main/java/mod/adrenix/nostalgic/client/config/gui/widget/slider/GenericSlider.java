package mod.adrenix.nostalgic.client.config.gui.widget.slider;

import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.DefaultConfig;
import mod.adrenix.nostalgic.client.config.annotation.TweakEntry;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericSlider extends AbstractSliderButton
{
    protected TweakEntry.Gui.Slider slider = TweakEntry.Gui.Slider.SWING_SLIDER;
    protected int min = ClientConfig.MIN;
    protected int max = ClientConfig.MAX;
    protected final Consumer<Integer> setCurrent;
    protected final Supplier<Integer> current;
    @Nullable protected final Supplier<String> text;

    public GenericSlider(Consumer<Integer> setCurrent, Supplier<Integer> current, @Nullable Supplier<String> text, int x, int y, int width, int height)
    {
        super(x, y, width, height, Component.empty(), (double) current.get());

        this.setCurrent = setCurrent;
        this.current = current;
        this.text = text;
        this.updateMessage();
        this.setValue(current.get());
    }

    public void setSlider(TweakEntry.Gui.Slider slider) { this.slider = slider; }

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

        if (this.slider == TweakEntry.Gui.Slider.SWING_SLIDER)
        {
            if (integer == DefaultConfig.Swing.DISABLED) color = ChatFormatting.RED;
            else if (integer == DefaultConfig.Swing.PHOTOSENSITIVE) color = ChatFormatting.YELLOW;
            else if (integer <= DefaultConfig.Swing.NEW_SPEED) color = ChatFormatting.GOLD;
        }
        else if (this.slider == TweakEntry.Gui.Slider.INTENSITY_SLIDER)
        {
            if (integer == 0) color = ChatFormatting.RED;
            else if (integer <= 50) color = ChatFormatting.GOLD;
            else if (integer > 100) color = ChatFormatting.AQUA;
        }
        else if (this.slider == TweakEntry.Gui.Slider.CLOUD_SLIDER)
        {
            if (integer == 128) color = ChatFormatting.YELLOW;
            else if (integer == 192) color = ChatFormatting.GOLD;
            else color = ChatFormatting.LIGHT_PURPLE;
        }

        return color;
    }

    @Override protected void applyValue() { this.setCurrent.accept((int) (this.min + Math.abs(this.max - this.min) * this.value)); }
    @Override
    public void updateMessage()
    {
        ChatFormatting color = this.getColorFromInt();
        String header = "";
        String suffix = "";

        if (this.text != null)
            header = this.text.get();
        else if (this.slider == TweakEntry.Gui.Slider.SWING_SLIDER)
            header = Component.translatable(NostalgicLang.Gui.SETTINGS_SPEED).getString();
        else if (this.slider == TweakEntry.Gui.Slider.INTENSITY_SLIDER)
        {
            header = Component.translatable(NostalgicLang.Gui.SETTINGS_INTENSITY).getString();
            suffix = "%";
        }
        else if (this.slider == TweakEntry.Gui.Slider.CLOUD_SLIDER)
        {
            int height = this.current.get();
            if (height == 108) header = Component.translatable(NostalgicLang.Gui.SETTINGS_ALPHA).getString();
            else if (height == 128) header = Component.translatable(NostalgicLang.Gui.SETTINGS_BETA).getString();
            else if (height == 192) header = Component.translatable(NostalgicLang.Gui.SETTINGS_MODERN).getString();
            else header = Component.translatable(NostalgicLang.Gui.SETTINGS_CUSTOM).getString();
        }

        String text = header + ": " + (this.active ? color : ChatFormatting.GRAY) + this.current.get().toString() + suffix;
        this.setMessage(Component.literal(text));
    }

    public void setValue(int value)
    {
        this.value = (Mth.clamp(value, this.min, this.max) - this.min) / (double) Math.abs(this.max - this.min);
    }
}