package mod.adrenix.nostalgic.client.config.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.reflect.EntryCache;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class TooltipButton extends Button
{
    protected final EntryCache<?> cache;
    protected final AbstractWidget anchor;

    public TooltipButton(EntryCache<?> cache, AbstractWidget anchor)
    {
        super(0, 0, 0, 0, TextComponent.EMPTY, (ignored) -> {});
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

        int startX = ConfigRowList.TEXT_START + minecraft.font.width(new TranslatableComponent(this.cache.getLangKey())) + 4;
        int startY = this.anchor.y + 4;
        int uWidth = 12;
        int vHeight = 14;
        boolean isMouseOver = (mouseX >= startX && mouseX <= startX + uWidth) && (mouseY >= startY && mouseY <= startY + vHeight);

        screen.blit(poseStack, startX, startY, 0, 0, uWidth, vHeight);
        if (isMouseOver && screen instanceof ConfigScreen)
        {
            ((ConfigScreen) screen).renderLast.add(() ->
                screen.renderComponentTooltip(poseStack, NostalgicUtil.Wrap.tooltips(new TranslatableComponent(cache.getTooltipKey()), 38), mouseX, mouseY));
        }
    }
}
