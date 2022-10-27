package mod.adrenix.nostalgic.client.config.gui.widget.slider;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Supplier;

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

    public ColorSlider(Consumer<Integer> setter, Supplier<Integer> current, Type type, int x, int y, int width, int height)
    {
        super(x, y, width, height, Component.empty(), (double) current.get());

        this.setter = setter;
        this.current = current;
        this.type = type;
        this.setValue(current.get());
        this.updateMessage();
    }

    /* Utility Methods */

    public void setValue(int value) { this.value = (Mth.clamp(value, MIN, MAX) - MIN) / (double) Math.abs(MAX - MIN); }

    /* Overrides */

    @Override
    protected void applyValue() { this.setter.accept((int) (MIN + Math.abs(MAX - MIN) * this.value)); }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        this.setValue(this.current.get() + (delta > 0.0D ? 1 : -1));
        this.applyValue();
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

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

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        this.updateMessage();
        super.render(poseStack, mouseX, mouseY, partialTick);
    }
}
