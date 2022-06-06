package mod.adrenix.nostalgic.client.config.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.annotation.TweakEntry;
import mod.adrenix.nostalgic.client.config.tweak.GuiTweak;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.reflect.ConfigReflect;
import mod.adrenix.nostalgic.client.config.reflect.TweakCache;
import mod.adrenix.nostalgic.client.config.reflect.GroupType;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class TweakTag extends AbstractWidget
{
    public static final int U_NEW_OFFSET = 66;
    public static final int U_CLIENT_OFFSET = 69;
    public static final int U_SERVER_OFFSET = 72;
    public static final int U_RELOAD_OFFSET = 75;
    public static final int U_RESTART_OFFSET = 78;
    public static final int U_KEY_OFFSET = 81;
    public static final int U_SYNC_OFFSET = 84;
    public static final int U_WARNING_OFFSET = 87;
    public static final int V_GLOBAL_OFFSET = 0;
    public static final int U_GLOBAL_WIDTH = 1;
    public static final int V_GLOBAL_HEIGHT = 11;
    public static final int TAG_MARGIN = 5;

    protected final TweakCache<?> cache;
    protected final AbstractWidget anchor;
    protected final boolean isTooltip;

    public TweakTag(TweakCache<?> cache, AbstractWidget anchor, boolean isTooltip)
    {
        super(0, 0, 0, 0, TextComponent.EMPTY);

        this.cache = cache;
        this.anchor = anchor;
        this.isTooltip = isTooltip;
    }

    private static int getTagWidth(Component tag, int startX)
    {
        return startX + U_GLOBAL_WIDTH + Minecraft.getInstance().font.width(tag) + TAG_MARGIN;
    }

    public static int renderTag(Screen screen, PoseStack poseStack, Component tag, int startX, int startY, int uOffset)
    {
        RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.WIDGETS_LOCATION);
        Font font = Minecraft.getInstance().font;

        int tagWidth = font.width(tag);
        int endX = getTagWidth(tag, startX);

        screen.blit(poseStack, startX, startY, uOffset, V_GLOBAL_OFFSET, U_GLOBAL_WIDTH, V_GLOBAL_HEIGHT);

        for (int i = 0; i < tagWidth + TAG_MARGIN; i++)
            screen.blit(poseStack, startX + U_GLOBAL_WIDTH + i, startY, uOffset + 1, 0, U_GLOBAL_WIDTH, V_GLOBAL_HEIGHT);

        screen.blit(poseStack, endX, startY, uOffset, V_GLOBAL_OFFSET, U_GLOBAL_WIDTH, V_GLOBAL_HEIGHT);
        font.draw(poseStack, tag, startX + 4, startY + 2, 0xFFFFFF);

        return endX + TAG_MARGIN;
    }

    public static void renderTooltip(Screen screen, PoseStack poseStack, Component title, Component tooltip, int startX, int startY, int mouseX, int mouseY)
    {
        int endX = getTagWidth(title, startX);
        boolean isMouseOver = (mouseX >= startX && mouseX <= endX) && (mouseY >= startY && mouseY <= startY + V_GLOBAL_HEIGHT);

        if (isMouseOver && screen instanceof ConfigScreen)
            ((ConfigScreen) screen).renderLast.add(() ->
                    screen.renderComponentTooltip(poseStack, NostalgicUtil.Wrap.tooltips(tooltip, 38), mouseX, mouseY));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (screen == null) return;

        TweakEntry.Gui.New isNew = ConfigReflect.getAnnotation(this.cache.getGroup(), this.cache.getKey(), TweakEntry.Gui.New.class);
        TweakEntry.Gui.Client isClient = ConfigReflect.getAnnotation(this.cache.getGroup(), this.cache.getKey(), TweakEntry.Gui.Client.class);
        TweakEntry.Gui.Server isServer = ConfigReflect.getAnnotation(this.cache.getGroup(), this.cache.getKey(), TweakEntry.Gui.Server.class);
        TweakEntry.Gui.Reload isReload = ConfigReflect.getAnnotation(this.cache.getGroup(), this.cache.getKey(), TweakEntry.Gui.Reload.class);
        TweakEntry.Gui.Restart isRestart = ConfigReflect.getAnnotation(this.cache.getGroup(), this.cache.getKey(), TweakEntry.Gui.Restart.class);
        TweakEntry.Gui.Warning isWarning = ConfigReflect.getAnnotation(this.cache.getGroup(), this.cache.getKey(), TweakEntry.Gui.Warning.class);

        Component title = new TranslatableComponent(this.cache.getLangKey());
        Component newTag = new TranslatableComponent(NostalgicLang.Gui.TAG_NEW);
        Component clientTag = new TranslatableComponent(NostalgicLang.Gui.TAG_CLIENT);
        Component serverTag = new TranslatableComponent(NostalgicLang.Gui.TAG_SERVER);
        Component reloadTag = new TranslatableComponent(NostalgicLang.Gui.TAG_RELOAD).withStyle(ChatFormatting.ITALIC);
        Component restartTag = new TranslatableComponent(NostalgicLang.Gui.TAG_RESTART).withStyle(ChatFormatting.ITALIC);
        Component warningTag = new TranslatableComponent(NostalgicLang.Gui.TAG_WARNING).withStyle(ChatFormatting.RED);
        Component newTooltip = new TranslatableComponent(NostalgicLang.Gui.TAG_NEW_TOOLTIP);
        Component clientTooltip = new TranslatableComponent(NostalgicLang.Gui.TAG_CLIENT_TOOLTIP);
        Component serverTooltip = new TranslatableComponent(NostalgicLang.Gui.TAG_SERVER_TOOLTIP);
        Component reloadTooltip = new TranslatableComponent(NostalgicLang.Gui.TAG_RELOAD_TOOLTIP);
        Component restartTooltip = new TranslatableComponent(NostalgicLang.Gui.TAG_RESTART_TOOLTIP);
        Component warningTooltip = new TranslatableComponent(this.cache.getWarningKey());

        boolean isNewRenderable = (Boolean) TweakCache.get(GroupType.GUI, GuiTweak.DISPLAY_NEW_TAGS.getKey()).getCurrent();
        boolean isSidedRenderable = (Boolean) TweakCache.get(GroupType.GUI, GuiTweak.DISPLAY_SIDED_TAGS.getKey()).getCurrent();
        boolean isTooltipRenderable = (Boolean) TweakCache.get(GroupType.GUI, GuiTweak.DISPLAY_TAG_TOOLTIPS.getKey()).getCurrent();

        int startX = ConfigRowList.TEXT_START + minecraft.font.width(title) + (isTooltip ? 20 : 4);
        int startY = this.anchor.y + 4;
        int lastX = startX;

        if (isNew != null && isNewRenderable)
        {
            if (isTooltipRenderable)
                renderTooltip(screen, poseStack, newTag, newTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, newTag, lastX, startY, U_NEW_OFFSET);
        }

        if (isClient != null && isSidedRenderable)
        {
            if (isTooltipRenderable)
                renderTooltip(screen, poseStack, clientTag, clientTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, clientTag, lastX, startY, U_CLIENT_OFFSET);
        }

        if (isServer != null && isSidedRenderable)
        {
            if (isTooltipRenderable)
                renderTooltip(screen, poseStack, serverTag, serverTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, serverTag, lastX, startY, U_SERVER_OFFSET);
        }

        if (isReload != null)
        {
            renderTooltip(screen, poseStack, reloadTag, reloadTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, reloadTag, lastX, startY, U_RELOAD_OFFSET);
        }

        if (isRestart != null)
        {
            renderTooltip(screen, poseStack, restartTag, restartTooltip, lastX, startY, mouseX, mouseY);
            renderTag(screen, poseStack, restartTag, lastX, startY, U_RESTART_OFFSET);
        }

        if (isWarning != null)
        {
            renderTooltip(screen, poseStack, warningTag, warningTooltip, lastX, startY, mouseX, mouseY);
            renderTag(screen, poseStack, warningTag, lastX, startY, U_WARNING_OFFSET);
        }
    }

    @Override public void updateNarration(NarrationElementOutput narrationElementOutput) {}
}
