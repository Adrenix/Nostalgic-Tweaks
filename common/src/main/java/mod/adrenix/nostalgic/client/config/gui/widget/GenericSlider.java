package mod.adrenix.nostalgic.client.config.gui.widget;

import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.DefaultConfig;
import mod.adrenix.nostalgic.client.config.annotation.NostalgicEntry;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericSlider extends AbstractSliderButton
{
    protected NostalgicEntry.Gui.Slider slider = NostalgicEntry.Gui.Slider.SWING_SLIDER;
    protected int min = ClientConfig.MIN;
    protected int max = ClientConfig.MAX;
    protected final Consumer<Integer> setCurrent;
    protected final Supplier<Integer> current;
    @Nullable protected final Supplier<String> text;

    public GenericSlider(Consumer<Integer> setCurrent, Supplier<Integer> current, @Nullable Supplier<String> text, int x, int y, int width, int height)
    {
        super(x, y, width, height, TextComponent.EMPTY, (double) current.get());

        this.setCurrent = setCurrent;
        this.current = current;
        this.text = text;
        this.updateMessage();
        this.setValue(current.get());
    }

    public void setSlider(NostalgicEntry.Gui.Slider slider) { this.slider = slider; }

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

    protected ChatFormatting getColorFromSpeed()
    {
        ChatFormatting color = ChatFormatting.GREEN;
        int speed = this.current.get();

        if (this.slider == NostalgicEntry.Gui.Slider.SWING_SLIDER)
        {
            if (speed == DefaultConfig.Swing.DISABLED) color = ChatFormatting.RED;
            else if (speed == DefaultConfig.Swing.PHOTOSENSITIVE) color = ChatFormatting.YELLOW;
            else if (speed <= DefaultConfig.Swing.NEW_SPEED) color = ChatFormatting.GOLD;
        }
        else if (this.slider == NostalgicEntry.Gui.Slider.INTENSITY_SLIDER)
        {
            if (speed == 0) color = ChatFormatting.RED;
            else if (speed <= 50) color = ChatFormatting.GOLD;
            else if (speed > 100) color = ChatFormatting.AQUA;
        }

        return color;
    }

    @Override protected void applyValue() { this.setCurrent.accept((int) (this.min + Math.abs(this.max - this.min) * this.value)); }
    @Override
    protected void updateMessage()
    {
        ChatFormatting color = this.getColorFromSpeed();
        String header = "";
        String suffix = "";

        if (this.text != null)
            header = this.text.get();
        else if (this.slider == NostalgicEntry.Gui.Slider.SWING_SLIDER)
            header = new TranslatableComponent(NostalgicLang.Gui.SETTINGS_SPEED).getString();
        else if (this.slider == NostalgicEntry.Gui.Slider.INTENSITY_SLIDER)
        {
            header = new TranslatableComponent(NostalgicLang.Gui.SETTINGS_INTENSITY).getString();
            suffix = "%";
        }

        String text = header + ": " + (this.active ? color : ChatFormatting.GRAY) + this.current.get().toString() + suffix;
        this.setMessage(new TextComponent(text));
    }

    public void setValue(int value)
    {
        this.value = (Mth.clamp(value, this.min, this.max) - this.min) / (double) Math.abs(this.max - this.min);
    }
}