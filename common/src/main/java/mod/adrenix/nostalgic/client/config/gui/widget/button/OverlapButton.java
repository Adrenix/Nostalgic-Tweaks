package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigWidgets;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;

public class OverlapButton extends Button
{
    /* Fields */

    protected final ConfigScreen screen;
    private boolean isListButton = false;

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

    public OverlapButton setAsList()
    {
        this.isListButton = true;
        this.width = 20;
        this.height = 20;
        return this;
    }

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

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        super.render(poseStack, mouseX, mouseY, partialTick);
        if (this.isListButton)
        {
            RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.WIDGETS_LOCATION);
            blit(poseStack, this.x, this.y, this.isHovered && this.active ? 20 : 0, 163, 20, 20);
        }
    }
}
