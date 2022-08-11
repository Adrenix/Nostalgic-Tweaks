package mod.adrenix.nostalgic.client.config.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class TextWidget extends ClickableWidget
{
    private final Text text;
    public final int startX;

    public TextWidget(int startX, Text component)
    {
        super(startX, 0, 0, 0, component);
        this.text = component;
        this.startX = startX;
    }

    @Override // Prevents the clicking sound from playing when left-clicking a text group widget
    public boolean mouseClicked(double mouseX, double mouseY, int button) { return false; }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        drawTextWithShadow(poseStack, MinecraftClient.getInstance().textRenderer, this.text, this.x, this.y, 0xFFFFFF);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder narrationElementOutput) { }
}
