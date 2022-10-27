package mod.adrenix.nostalgic.client.config.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class TextWidget extends AbstractWidget
{
    private final Component text;
    public final int startX;

    public TextWidget(int startX, Component component)
    {
        super(startX, 0, 0, 0, component);
        this.text = component;
        this.startX = startX;
    }

    @Override // Prevents the clicking sound from playing when left-clicking a text group widget
    public boolean mouseClicked(double mouseX, double mouseY, int button) { return false; }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        drawString(poseStack, Minecraft.getInstance().font, this.text, this.x, this.y, 0xFFFFFF);
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) { }
}
