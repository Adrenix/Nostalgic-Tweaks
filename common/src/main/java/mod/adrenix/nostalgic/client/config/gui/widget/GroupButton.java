package mod.adrenix.nostalgic.client.config.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class GroupButton extends Button
{
    protected final ConfigRowList.CategoryRow row;
    protected final Component title;
    protected boolean isExpanded;

    public GroupButton(ConfigRowList.CategoryRow row, Component title, boolean isExpanded)
    {
        super(ConfigRowList.TEXT_START, 0, 0, 0, TextComponent.EMPTY, (ignored) -> {});

        this.row = row;
        this.title = title;
        this.isExpanded = isExpanded;
        this.width = 18;
        this.height = 16;
    }

    @Override
    public void onPress()
    {
        Screen screen = Minecraft.getInstance().screen;
        boolean isScrolling = false;

        if (screen instanceof ConfigScreen)
            isScrolling = ((ConfigScreen) screen).isScrollbarVisible();

        if (this.isExpanded)
            this.row.collapse();
        else
            this.row.expand();

        if (isScrolling && !((ConfigScreen) screen).isScrollbarVisible())
            ((ConfigScreen) screen).resetScrollbar();

        this.isExpanded = !this.isExpanded;
        super.onPress();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.WIDGETS_LOCATION);
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (screen == null) return;

        int uOffset = 33;
        int vOffset = 0;
        int uWidth = 12;
        int vHeight = 18;
        int blitX = this.x;
        int blitY = this.y;
        boolean isMouseOver = this.isMouseOver(this.isExpanded ? mouseX + 4 : mouseX, this.isExpanded ? mouseY - 4 : mouseY);

        if (isMouseOver)
        {
            uOffset = this.isExpanded ? 47 : 33;
            vOffset = 23;
        }
        else if (this.isExpanded)
            uOffset = 47;

        if (this.isExpanded)
        {
            uWidth = 18;
            vHeight = 12;
            blitX = this.x - 4;
            blitY = this.y + 4;
        }

        this.width = 20 + minecraft.font.width(this.title);
        screen.blit(poseStack, blitX, blitY, uOffset, vOffset, uWidth, vHeight);
        Screen.drawString(poseStack, minecraft.font, this.title, this.x + 20, this.y + 5, isMouseOver ? 0xFFD800 : 0xFFFFFF);
    }
}
