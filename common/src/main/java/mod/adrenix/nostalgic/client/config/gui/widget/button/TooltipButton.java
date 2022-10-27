package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.common.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TooltipButton extends Button
{
    protected final TweakClientCache<?> cache;
    protected final AbstractWidget anchor;
    private String title;

    public TooltipButton(TweakClientCache<?> cache, AbstractWidget anchor)
    {
        super(0, 0, 0, 0, Component.empty(), (ignored) -> {});
        this.cache = cache;
        this.anchor = anchor;
        this.title = Component.translatable(this.cache.getLangKey()).getString();
    }

    public void setTitle(String title) { this.title = title; }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.setShaderTexture(0, ModUtil.Resource.WIDGETS_LOCATION);
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (screen == null) return;

        int startX = ConfigRowList.getStartX() + minecraft.font.width(this.title) + 4;
        int startY = this.anchor.y + 4;
        int uWidth = 12;
        int vHeight = 14;
        boolean isMouseOver = (mouseX >= startX && mouseX <= startX + uWidth) && (mouseY >= startY && mouseY <= startY + vHeight);

        screen.blit(poseStack, startX, startY, 0, 0, uWidth, vHeight);
        if (isMouseOver && screen instanceof ConfigScreen)
        {
            ((ConfigScreen) screen).renderLast.add(() ->
                screen.renderComponentTooltip(poseStack, ModUtil.Wrap.tooltip(Component.translatable(cache.getTooltipKey()), 38), mouseX, mouseY));
        }
    }
}
