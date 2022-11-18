package mod.adrenix.nostalgic.client.config.gui.widget.slider;

import mod.adrenix.nostalgic.client.config.annotation.TweakClient;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This slider can be used in many places but is used mostly by the configuration screen and custom swing speed screen.
 * Since generic sliders can only use client cache tweaks, no permission lock is required.
 */

public class GenericSlider extends AbstractSliderButton
{
    /* Fields */

    private TweakClient.Gui.Slider slider = TweakClient.Gui.Slider.SWING;
    private int min = ClientConfig.MIN;
    private int max = ClientConfig.MAX;
    private final Consumer<Integer> setCurrent;
    private final Supplier<Integer> current;

    @Nullable private final Supplier<String> text;
    @Nullable private final TweakClient.Gui.SliderType sliderType;

    /* Constructors */

    /**
     * This constructor should be used when the slider controls a client tweak instance.
     * @param tweak The tweak client cache instance.
     * @param text A supplier that provides a string for slider text.
     * @param x A starting x-position.
     * @param y A starting y-position.
     * @param width The slider's width.
     * @param height The slider's height.
     */
    public GenericSlider(TweakClientCache<Integer> tweak, @Nullable Supplier<String> text, int x, int y, int width, int height)
    {
        super(x, y, width, height, Component.empty(), (double) tweak.getValue());

        this.sliderType = tweak.getMetadata(TweakClient.Gui.SliderType.class);
        this.setCurrent = tweak::setValue;
        this.current = tweak::getValue;
        this.text = text;

        this.updateMessage();
        this.setValue(current.get());
    }

    /**
     * This constructor should be used when the slider does not control a tweak instance.
     * @param setCurrent A consumer that accepts an integer.
     * @param current A supplier that provides a starting integer.
     * @param text A supplier that provides a string for slider text.
     * @param x A starting x-position.
     * @param y A starting y-position.
     * @param width The slider's width.
     * @param height The slider's height.
     */
    public GenericSlider(Consumer<Integer> setCurrent, Supplier<Integer> current, @Nullable Supplier<String> text, int x, int y, int width, int height)
    {
        super(x, y, width, height, Component.empty(), (double) current.get());

        this.setCurrent = setCurrent;
        this.current = current;
        this.text = text;
        this.sliderType = null;

        this.updateMessage();
        this.setValue(current.get());
    }

    /**
     * Changes the minimum value accepted by this slider.
     * @param min The new minimum value.
     */
    protected void setMinimum(int min)
    {
        this.min = min;
        this.setValue(this.current.get());
    }

    /**
     * Changes the maximum value accepted by the slider.
     * @param max The new maximum value.
     */
    protected void setMaximum(int max)
    {
        this.max = max;
        this.setValue(this.current.get());
    }

    /**
     * Change the current value of this slider.
     * @param value The new slider value.
     */
    public void setValue(int value)
    {
        this.value = (Mth.clamp(value, this.min, this.max) - this.min) / (double) Math.abs(this.max - this.min);
    }

    /**
     * Logic that is applied the current slider value.
     */
    @Override
    protected void applyValue() { this.setCurrent.accept((int) (this.min + Math.abs(this.max - this.min) * this.value)); }

    /**
     * Change the slider type associated with a generic slider.
     * This will only change visual aspects of the slider.
     * @param slider A slider type enumeration value.
     */
    public void setSlider(TweakClient.Gui.Slider slider) { this.slider = slider; }

    /**
     * Gets chat formatting based on this slider's type.
     * @return A chat formatter.
     */
    private ChatFormatting getColorFromInt()
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

    /**
     * Changes the title or "message" for this slider.
     * This will change based on slider type.
     */
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
            suffix = "%";
            title = Component.translatable(LangUtil.Gui.SETTINGS_INTENSITY).getString();
        }
        else if (this.slider == TweakClient.Gui.Slider.CLOUD)
        {
            title = switch (this.current.get())
            {
                case 108 -> Component.translatable(LangUtil.Gui.SETTINGS_ALPHA).getString();
                case 128 -> Component.translatable(LangUtil.Gui.SETTINGS_BETA).getString();
                case 192 -> Component.translatable(LangUtil.Gui.SETTINGS_MODERN).getString();
                default -> Component.translatable(LangUtil.Gui.SETTINGS_CUSTOM).getString();
            };
        }

        if (this.sliderType != null && !this.sliderType.langKey().isEmpty())
            title = Component.translatable(this.sliderType.langKey()).getString();

        if (this.sliderType != null && !this.sliderType.suffix().isEmpty())
            suffix = this.sliderType.suffix();

        String text = title + ": " + (this.active ? color : ChatFormatting.GRAY) + this.current.get().toString() + suffix;

        this.setMessage(Component.literal(text));
    }
}