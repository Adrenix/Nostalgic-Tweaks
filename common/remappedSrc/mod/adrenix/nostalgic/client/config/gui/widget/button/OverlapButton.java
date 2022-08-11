package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigWidgets;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class OverlapButton extends ButtonWidget
{
    /* Fields */

    protected final ConfigScreen screen;
    private boolean isListButton = false;

    /* Constructors */

    public OverlapButton(ConfigScreen screen, int startX, int startY, int width, int height, Text text, PressAction onPress)
    {
        super(startX, startY, width, height, text, onPress);
        this.screen = screen;
    }

    public OverlapButton(ConfigScreen screen, Text text, PressAction onPress)
    {
        this(screen, 0, ConfigWidgets.TOP_ROW, screen.getFont().getWidth(text) + ConfigWidgets.WIDTH_PADDING, ConfigWidgets.BUTTON_HEIGHT, text, onPress);
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
        for (Drawable child : this.screen.getWidgets().children)
        {
            if (child instanceof ClickableWidget widget)
            {
                if (!widget.equals(this) && (mouseX >= widget.x && mouseY >= widget.y && mouseX < widget.x + widget.getWidth() && mouseY < widget.y + widget.getHeight()))
                    isOtherHovered = true;
            }
        }

        return !isOtherHovered;
    }

    /* Overrides */

    @Override
    public void renderButton(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        for (Drawable child : this.screen.getWidgets().children)
        {
            if (child instanceof ClickableWidget widget)
            {
                if (!widget.equals(this) && (mouseX >= widget.x && mouseY >= widget.y && mouseX < widget.x + widget.getWidth() && mouseY < widget.y + widget.getHeight()))
                    this.hovered = false;
            }
        }

        super.renderButton(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        super.render(poseStack, mouseX, mouseY, partialTick);
        if (this.isListButton)
        {
            RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.WIDGETS_LOCATION);
            drawTexture(poseStack, this.x, this.y, this.hovered && this.active ? 20 : 0, 163, 20, 20);
        }
    }
}
