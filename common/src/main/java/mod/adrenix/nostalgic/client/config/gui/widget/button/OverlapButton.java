package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigWidgets;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;

public class OverlapButton extends Button
{
    /* Fields */

    protected final ConfigScreen screen;

    /* Constructors */

    public OverlapButton(ConfigScreen screen, int startX, int startY, int width, int height, Component text, OnPress onPress)
    {
        super(startX, startY, width, height, text, onPress);
        this.screen = screen;
    }

    public OverlapButton(ConfigScreen screen, Component text, OnPress onPress)
    {
        this(screen, 0, ConfigWidgets.TOP_ROW, screen.getFont().width(text) + ConfigWidgets.WIDTH_PADDING, ConfigWidgets.BUTTON_HEIGHT, text, onPress);
    }

    /* Helpers */

    protected boolean shouldRenderToolTip(int mouseX, int mouseY)
    {
        boolean isOtherHovered = false;
        for (Widget child : this.screen.getWidgets().children)
        {
            if (child instanceof AbstractWidget widget)
            {
                if (!widget.equals(this) && (mouseX >= widget.x && mouseY >= widget.y && mouseX < widget.x + widget.getWidth() && mouseY < widget.y + widget.getHeight()))
                    isOtherHovered = true;
            }
        }

        return !isOtherHovered;
    }

    /* Overrides */

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        for (Widget child : this.screen.getWidgets().children)
        {
            if (child instanceof AbstractWidget widget)
            {
                if (!widget.equals(this) && (mouseX >= widget.x && mouseY >= widget.y && mouseX < widget.x + widget.getWidth() && mouseY < widget.y + widget.getHeight()))
                    this.isHovered = false;
            }
        }

        super.renderButton(poseStack, mouseX, mouseY, partialTick);
    }
}
