package mod.adrenix.nostalgic.client.config.gui.widget.slider;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.client.config.annotation.TweakGui;
import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.annotation.TweakData;
import mod.adrenix.nostalgic.common.config.tweak.Tweak;
import mod.adrenix.nostalgic.util.common.LangUtil;
import mod.adrenix.nostalgic.util.common.MathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This slider can be used in many places but is used mostly by the configuration screen and custom swing speed screen.
 * Since generic sliders can only use client cache tweaks, no permission lock is required.
 */

public class GenericSlider extends AbstractSliderButton
{
    /* Fields */

    private TweakGui.SliderType sliderType = TweakGui.SliderType.SWING;
    private int min = DefaultConfig.Swing.MIN_SPEED;
    private int max = DefaultConfig.Swing.MAX_SPEED;
    private final Consumer<Integer> setCurrent;
    private final Supplier<Integer> current;

    private final TweakGui.Slider sliderData;

    /* Constructors */

    /**
     * This constructor should be used when the slider controls a client tweak instance.
     * @param tweak The tweak client cache instance.
     * @param x A starting x-position.
     * @param y A starting y-position.
     * @param width The slider's width.
     * @param height The slider's height.
     */
    public GenericSlider(TweakClientCache<Integer> tweak, int x, int y, int width, int height)
    {
        super(x, y, width, height, Component.empty(), (double) tweak.getValue());

        this.sliderData = tweak.getMetadata(TweakGui.Slider.class);
        this.setCurrent = tweak::setValue;
        this.current = tweak::getValue;

        this.updateMessage();
        this.setValue(current.get());
    }

    /**
     * This constructor is used by the config row map entry system.
     * @param setCurrent A consumer that accepts an integer.
     * @param current A supplier that provides a starting integer.
     * @param tweak An object that inherits the tweak interface.
     * @param x A starting x-position.
     * @param y A starting y-position.
     * @param width The slider's width.
     * @param height The slider's height.
     */
    public GenericSlider(Consumer<Integer> setCurrent, Supplier<Integer> current, Tweak tweak, int x, int y, int width, int height)
    {
        super(x, y, width, height, Component.empty(), (double) current.get());

        this.setCurrent = setCurrent;
        this.current = current;

        this.sliderData = TweakClientCache.get(tweak).getMetadata(TweakGui.Slider.class);

        if (this.sliderData != null)
            this.sliderType = sliderData.type();

        TweakData.BoundedSlider bounds = TweakClientCache.get(tweak).getMetadata(TweakData.BoundedSlider.class);

        if (bounds != null)
        {
            this.setMinimum((int) bounds.min());
            this.setMaximum((int) bounds.max());
        }

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
     * Send an update message to this slider that the value has changed and an update is needed for visuals.
     */
    public void update()
    {
        this.setValue(this.current.get());
        this.updateMessage();
    }

    /**
     * Logic that is applied the current slider value.
     */
    @Override
    protected void applyValue() { this.setCurrent.accept((int) (this.min + Math.abs(this.max - this.min) * this.value)); }

    /**
     * Change the slider type associated with a generic slider.
     * This will only change visual aspects of the slider.
     * @param sliderType A slider type enumeration value.
     */
    public void setType(TweakGui.SliderType sliderType) { this.sliderType = sliderType; }

    /**
     * Gets chat formatting based on this slider's type.
     * @return A chat formatter.
     */
    private ChatFormatting getColorFromInt()
    {
        ChatFormatting color = ChatFormatting.GREEN;
        int integer = this.current.get();

        if (this.sliderType == TweakGui.SliderType.SWING)
        {
            if (integer == DefaultConfig.Swing.DISABLED) color = ChatFormatting.RED;
            else if (integer == DefaultConfig.Swing.PHOTOSENSITIVE) color = ChatFormatting.YELLOW;
            else if (integer <= DefaultConfig.Swing.NEW_SPEED) color = ChatFormatting.GOLD;
        }
        else if (this.sliderType == TweakGui.SliderType.INTENSITY)
        {
            if (integer == 0) color = ChatFormatting.RED;
            else if (integer <= 50) color = ChatFormatting.GOLD;
            else if (integer > 100) color = ChatFormatting.AQUA;
        }
        else if (this.sliderType == TweakGui.SliderType.CLOUD)
        {
            if (integer == 128) color = ChatFormatting.YELLOW;
            else if (integer == 192) color = ChatFormatting.GOLD;
            else color = ChatFormatting.LIGHT_PURPLE;
        }
        else if (this.sliderType == TweakGui.SliderType.GENERIC)
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
        if (this.sliderType == TweakGui.SliderType.HEARTS)
        {
            this.setMessage(Component.empty());
            return;
        }

        ChatFormatting color = this.getColorFromInt();
        String title = "";
        String suffix = "";

        if (this.sliderType == TweakGui.SliderType.SWING)
            title = Component.translatable(LangUtil.Gui.SETTINGS_SPEED).getString();
        else if (this.sliderType == TweakGui.SliderType.INTENSITY)
        {
            suffix = "%";
            title = Component.translatable(LangUtil.Gui.SETTINGS_INTENSITY).getString();
        }
        else if (this.sliderType == TweakGui.SliderType.CLOUD)
        {
            title = switch (this.current.get())
            {
                case 108 -> Component.translatable(LangUtil.Gui.SETTINGS_ALPHA).getString();
                case 128 -> Component.translatable(LangUtil.Gui.SETTINGS_BETA).getString();
                case 192 -> Component.translatable(LangUtil.Gui.SETTINGS_MODERN).getString();
                default -> Component.translatable(LangUtil.Gui.SETTINGS_CUSTOM).getString();
            };
        }

        if (this.sliderData != null && !this.sliderData.langKey().isEmpty())
            title = Component.translatable(this.sliderData.langKey()).getString();

        if (this.sliderData != null && !this.sliderData.suffix().isEmpty())
            suffix = this.sliderData.suffix();

        String text = title + ": " + color + this.current.get().toString() + suffix;

        if (!this.active)
        {
            text = ChatFormatting.stripFormatting(text);
            text = ChatFormatting.GRAY + ChatFormatting.STRIKETHROUGH.toString() + text;
        }

        if (Overlay.isOpened())
        {
            text = ChatFormatting.stripFormatting(text);
            text = ChatFormatting.GRAY + text;
        }

        this.setMessage(Component.literal(text));
    }

    /**
     * Handler method for rendering the background of this widget.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     */
    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);

        // Render Hearts

        if (this.sliderType == TweakGui.SliderType.HEARTS)
        {
            if (!this.active)
                RenderSystem.setShaderColor(0.3F, 0.3F, 0.3F, 1.0F);

            int x = this.getX() + (this.width / 2) - 45;
            int y = this.getY() + 6;
            int dx = x;

            for (int i = 0; i < 10; i++)
            {
                graphics.blit(SLIDER_LOCATION, dx, y, 16, 0, 9, 9);
                dx += 9;
            }

            dx = x;

            for (int i = 0; i < this.current.get(); i++)
            {
                if (MathUtil.isOdd(i))
                {
                    graphics.blit(SLIDER_LOCATION, dx, y, 52, 0, 9, 9);
                    dx += 9;
                }
                else
                    graphics.blit(SLIDER_LOCATION, dx, y, 61, 0, 9, 9);
            }
        }
    }
}