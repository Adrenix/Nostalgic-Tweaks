package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakCache;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TooltipButton extends Button
{
    protected final TweakCache<?> cache;
    protected final AbstractWidget anchor;

    public TooltipButton(TweakCache<?> cache, AbstractWidget anchor)
    {
        super(0, 0, 0, 0, Component.empty(), (ignored) -> {});
        this.cache = cache;
        this.anchor = anchor;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.WIDGETS_LOCATION);
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (screen == null) return;

        int startX = ConfigRowList.TEXT_START + minecraft.font.width(Component.translatable(this.cache.getLangKey())) + 4;
        int startY = this.anchor.y + 4;
        int uWidth = 12;
        int vHeight = 14;
        boolean isMouseOver = (mouseX >= startX && mouseX <= startX + uWidth) && (mouseY >= startY && mouseY <= startY + vHeight);

        screen.blit(poseStack, startX, startY, 0, 0, uWidth, vHeight);
        if (isMouseOver && screen instanceof ConfigScreen)
        {
            ((ConfigScreen) screen).renderLast.add(() ->
                screen.renderComponentTooltip(poseStack, NostalgicUtil.Wrap.tooltips(Component.translatable(cache.getTooltipKey()), 38), mouseX, mouseY));
        }
    }
}
