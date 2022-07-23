package mod.adrenix.nostalgic.client.config.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.screen.CustomizeScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

public class ToggleCheckbox extends Checkbox
{
    /* Fields */

    private final Screen screen;
    private OnPress onPress;
    private Component tooltip;
    private int tooltipWidth = 40;

    /* Click Interface */

    public interface OnPress { void press(boolean newState); }

    /* Constructors */

    public ToggleCheckbox(Screen screen, int x, int y, int width, int height, Component label, boolean state)
    {
        super(x, y, width, height, label, state, true);
        this.screen = screen;
        this.onPress = (newState) -> {};
        this.tooltip = null;
    }

    public ToggleCheckbox(Screen screen, Component label, Supplier<Boolean> state, OnPress onPress)
    {
        this(screen, ConfigRowList.TEXT_START, 0, 20, 20, label, state.get());
        this.onPress = onPress;
    }

    /* Setters */

    public void setTooltip(Component tooltip, int width)
    {
        this.tooltip = tooltip;
        this.tooltipWidth = width;
    }

    public void setTooltip(Component tooltip) { this.setTooltip(tooltip, this.tooltipWidth); }

    /* Widget Overrides */

    @Override
    public void onPress()
    {
        super.onPress();
        this.onPress.press(this.selected());
        if (this.screen instanceof CustomizeScreen)
        {
            ((CustomizeScreen) this.screen).getMinecraft().setScreen(this.screen);
            ((CustomizeScreen) this.screen).setSuggestionFocus(false);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.WIDGETS_LOCATION);

        int uOffset = 0;
        int vOffset = 103;
        int uWidth = 20;
        int vHeight = 20;

        if (this.isMouseOver(mouseX, mouseY))
        {
            uOffset = 20;
            vOffset = this.selected() ? 83 : vOffset;
        }
        else if (this.selected())
            vOffset = 83;

        this.screen.blit(poseStack, this.x, this.y, uOffset, vOffset, uWidth, vHeight);
        ToggleCheckbox.drawString(poseStack, Minecraft.getInstance().font, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 0xFFFFFF);

        if (this.isMouseOver(mouseX, mouseY) && this.tooltip != null)
        {
            List<Component> tip = NostalgicUtil.Wrap.tooltip(this.tooltip, this.tooltipWidth);
            this.screen.renderComponentTooltip(poseStack, tip, mouseX, mouseY);
        }
    }
}
