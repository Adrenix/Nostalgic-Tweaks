package mod.adrenix.nostalgic.client.config.gui.widget.slider;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A color slider widget is an extension of the vanilla abstract slider button. When creating a color slider, there are
 * four slider types available: a red, green, blue, or alpha slider. Depending on the provided type during construction,
 * the slider will change message appearance.
 */

public class ColorSlider extends AbstractSliderButton
{
    /* Color Slider Types */

    public enum Type { R, G, B, A }

    /* Fields */

    private static final int MIN = 0;
    private static final int MAX = 255;

    private final Consumer<Integer> setter;
    private final Supplier<Integer> current;
    private final Type type;

    /* Constructor */

    /**
     * Create a new color slider instance. The appearance of the slider will change depending on the color slider type
     * provided.
     *
     * @param setter A consumer that accepts a new integer value for the slider.
     * @param current A supplier that provides an integer value for the slider.
     * @param type A slider color type. This must be red, green, blue, or alpha.
     * @param x A starting x-position for the slider.
     * @param y A starting y-position for the slider.
     * @param width The width of the slider.
     * @param height The height of the slider.
     */
    public ColorSlider(Consumer<Integer> setter, Supplier<Integer> current, Type type, int x, int y, int width, int height)
    {
        super(x, y, width, height, Component.empty(), (double) current.get());

        this.setter = setter;
        this.current = current;
        this.type = type;

        this.setValue(current.get());
        this.updateMessage();
    }

    /* Methods */

    /**
     * Set the value for this slider.
     * @param value A new slider value.
     */
    public void setValue(int value) { this.value = (Mth.clamp(value, MIN, MAX) - MIN) / (double) Math.abs(MAX - MIN); }

    /* Overrides */

    /**
     * Sends the current slider value to the setter supplier.
     */
    @Override
    protected void applyValue() { this.setter.accept((int) (MIN + Math.abs(MAX - MIN) * this.value)); }

    /**
     * Handler method for when the mouse is scrolled over a color slider.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param delta The change in scroll direction.
     * @return Whether this method handled the scroll event.
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        this.setValue(this.current.get() + (delta > 0.0D ? 1 : -1));
        this.applyValue();

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    /**
     * Handler method for updating the title message for the color slider.
     */
    @Override
    protected void updateMessage()
    {
        ChatFormatting color = switch (this.type)
        {
            case R -> ChatFormatting.RED;
            case G -> ChatFormatting.GREEN;
            case B -> ChatFormatting.BLUE;
            case A -> ChatFormatting.WHITE;
        };

        String text = this.type + ": " + color + this.current.get().toString();
        this.setMessage(Component.literal(text));
    }

    /**
     * Handler method for rendering a color slider widget.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param partialTick The change in frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        this.updateMessage();
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
