package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class TooltipButton extends ButtonWidget
{
    protected final TweakClientCache<?> cache;
    protected final ClickableWidget anchor;
    private String title;

    public TooltipButton(TweakClientCache<?> cache, ClickableWidget anchor)
    {
        super(0, 0, 0, 0, Text.empty(), (ignored) -> {});
        this.cache = cache;
        this.anchor = anchor;
        this.title = Text.translatable(this.cache.getLangKey()).getString();
    }

    public void setTitle(String title) { this.title = title; }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.WIDGETS_LOCATION);
        MinecraftClient minecraft = MinecraftClient.getInstance();
        Screen screen = minecraft.currentScreen;
        if (screen == null) return;

        int startX = ConfigRowList.getStartX() + minecraft.textRenderer.getWidth(this.title) + 4;
        int startY = this.anchor.y + 4;
        int uWidth = 12;
        int vHeight = 14;
        boolean isMouseOver = (mouseX >= startX && mouseX <= startX + uWidth) && (mouseY >= startY && mouseY <= startY + vHeight);

        screen.drawTexture(poseStack, startX, startY, 0, 0, uWidth, vHeight);
        if (isMouseOver && screen instanceof ConfigScreen)
        {
            ((ConfigScreen) screen).renderLast.add(() ->
                screen.renderTooltip(poseStack, NostalgicUtil.Wrap.tooltip(Text.translatable(cache.getTooltipKey()), 38), mouseX, mouseY));
        }
    }
}
