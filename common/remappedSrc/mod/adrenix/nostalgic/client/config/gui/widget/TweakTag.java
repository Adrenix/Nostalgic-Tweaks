package mod.adrenix.nostalgic.client.config.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.client.config.annotation.TweakClient;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.common.config.annotation.TweakSide;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.tweak.GuiTweak;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TweakTag extends ClickableWidget
{
    public static final int U_NEW_OFFSET = 66;
    public static final int U_CLIENT_OFFSET = 69;
    public static final int U_SERVER_OFFSET = 72;
    public static final int U_DYNAMIC_OFFSET = 81;
    public static final int U_RELOAD_OFFSET = 75;
    public static final int U_RESTART_OFFSET = 78;
    public static final int U_KEY_OFFSET = 81;
    public static final int U_SYNC_OFFSET = 84;
    public static final int U_WARNING_OFFSET = 87;
    public static final int V_GLOBAL_OFFSET = 0;
    public static final int U_GLOBAL_WIDTH = 1;
    public static final int V_GLOBAL_HEIGHT = 11;
    public static final int TAG_MARGIN = 5;

    private String title;
    private boolean render = true;
    protected final TweakClientCache<?> cache;
    protected final ClickableWidget anchor;
    protected final boolean isTooltip;

    public TweakTag(TweakClientCache<?> cache, ClickableWidget anchor, boolean isTooltip)
    {
        super(0, 0, 0, 0, Text.empty());

        this.cache = cache;
        this.anchor = anchor;
        this.isTooltip = isTooltip;
        this.title = Text.translatable(this.cache.getLangKey()).getString();
    }

    public String getTitle() { return this.title; }
    public void setTitle(String title) { this.title = title; }
    public void setRender(boolean state) { this.render = state; }

    private static int getTagWidth(Text tag, int startX)
    {
        return startX + U_GLOBAL_WIDTH + MinecraftClient.getInstance().textRenderer.getWidth(tag) + TAG_MARGIN;
    }

    private static void draw(Screen screen, MatrixStack poseStack, int x, int y, int uOffset, int vOffset, boolean render)
    {
        if (render)
            screen.drawTexture(poseStack, x, y, uOffset, vOffset, U_GLOBAL_WIDTH, V_GLOBAL_HEIGHT);
    }

    public static int renderTag(Screen screen, MatrixStack poseStack, Text tag, int startX, int startY, int uOffset, boolean render)
    {
        RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.WIDGETS_LOCATION);
        TextRenderer font = MinecraftClient.getInstance().textRenderer;

        int tagWidth = font.getWidth(tag);
        int endX = getTagWidth(tag, startX);

        TweakTag.draw(screen, poseStack, startX, startY, uOffset, V_GLOBAL_OFFSET, render);

        for (int i = 0; i < tagWidth + TAG_MARGIN; i++)
            TweakTag.draw(screen, poseStack, startX + U_GLOBAL_WIDTH + i, startY, uOffset + 1, 0, render);

        TweakTag.draw(screen, poseStack, endX, startY, uOffset, V_GLOBAL_OFFSET, render);

        font.draw(poseStack, tag, startX + 4, startY + 2, 0xFFFFFF);

        return endX + TAG_MARGIN;
    }

    public static int renderTag(Screen screen, MatrixStack poseStack, Text tag, int startX, int startY, int uOffset)
    {
        return renderTag(screen, poseStack, tag, startX, startY, uOffset, true);
    }

    public static void renderTooltip(Screen screen, MatrixStack poseStack, Text title, Text tooltip, int startX, int startY, int mouseX, int mouseY)
    {
        int endX = getTagWidth(title, startX);
        boolean isMouseOver = (mouseX >= startX && mouseX <= endX) && (mouseY >= startY && mouseY <= startY + V_GLOBAL_HEIGHT);

        if (isMouseOver && screen instanceof ConfigScreen)
            ((ConfigScreen) screen).renderLast.add(() ->
                screen.renderTooltip(poseStack, NostalgicUtil.Wrap.tooltip(tooltip, 38), mouseX, mouseY));
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        Screen screen = minecraft.currentScreen;
        if (screen == null) return;

        TweakClient.Gui.New isNew = CommonReflect.getAnnotation(this.cache, TweakClient.Gui.New.class);
        TweakSide.Client isClient = CommonReflect.getAnnotation(this.cache, TweakSide.Client.class);
        TweakSide.Server isServer = CommonReflect.getAnnotation(this.cache, TweakSide.Server.class);
        TweakSide.Dynamic isDynamic = CommonReflect.getAnnotation(this.cache, TweakSide.Dynamic.class);
        TweakClient.Gui.Restart isRestart = CommonReflect.getAnnotation(this.cache, TweakClient.Gui.Restart.class);
        TweakClient.Gui.Warning isWarning = CommonReflect.getAnnotation(this.cache, TweakClient.Gui.Warning.class);
        TweakClient.Run.ReloadResources isReload = CommonReflect.getAnnotation(this.cache, TweakClient.Run.ReloadResources.class);

        Text title = Text.literal(this.title);
        Text newTag = Text.translatable(NostalgicLang.Gui.TAG_NEW);
        Text clientTag = Text.translatable(NostalgicLang.Gui.TAG_CLIENT);
        Text serverTag = Text.translatable(NostalgicLang.Gui.TAG_SERVER);
        Text dynamicTag = Text.translatable(NostalgicLang.Gui.TAG_DYNAMIC);
        Text reloadTag = Text.translatable(NostalgicLang.Gui.TAG_RELOAD).withStyle(Formatting.ITALIC);
        Text restartTag = Text.translatable(NostalgicLang.Gui.TAG_RESTART).withStyle(Formatting.ITALIC);
        Text warningTag = Text.translatable(NostalgicLang.Gui.TAG_WARNING).withStyle(Formatting.RED);
        Text newTooltip = Text.translatable(NostalgicLang.Gui.TAG_NEW_TOOLTIP);
        Text clientTooltip = Text.translatable(NostalgicLang.Gui.TAG_CLIENT_TOOLTIP);
        Text serverTooltip = Text.translatable(NostalgicLang.Gui.TAG_SERVER_TOOLTIP);
        Text dynamicTooltip = Text.translatable(NostalgicLang.Gui.TAG_DYNAMIC_TOOLTIP);
        Text reloadTooltip = Text.translatable(NostalgicLang.Gui.TAG_RELOAD_TOOLTIP);
        Text restartTooltip = Text.translatable(NostalgicLang.Gui.TAG_RESTART_TOOLTIP);
        Text warningTooltip = Text.translatable(this.cache.getWarningKey());

        boolean isNewRenderable = (Boolean) TweakClientCache.get(GuiTweak.DISPLAY_NEW_TAGS).getCurrent();
        boolean isSidedRenderable = (Boolean) TweakClientCache.get(GuiTweak.DISPLAY_SIDED_TAGS).getCurrent();
        boolean isTooltipRenderable = (Boolean) TweakClientCache.get(GuiTweak.DISPLAY_TAG_TOOLTIPS).getCurrent();

        int startX = ConfigRowList.getStartX() + minecraft.textRenderer.getWidth(title) + (isTooltip ? 20 : 4);
        int startY = this.anchor.y + 4;
        int lastX = startX;

        if (isNew != null && isNewRenderable)
        {
            if (isTooltipRenderable)
                renderTooltip(screen, poseStack, newTag, newTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, newTag, lastX, startY, U_NEW_OFFSET, this.render);
        }

        if (isClient != null && isSidedRenderable)
        {
            if (isTooltipRenderable)
                renderTooltip(screen, poseStack, clientTag, clientTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, clientTag, lastX, startY, U_CLIENT_OFFSET, this.render);
        }

        if (isServer != null && isSidedRenderable)
        {
            if (isTooltipRenderable)
                renderTooltip(screen, poseStack, serverTag, serverTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, serverTag, lastX, startY, U_SERVER_OFFSET, this.render);
        }

        if (isDynamic != null && isSidedRenderable)
        {
            if (isTooltipRenderable)
                renderTooltip(screen, poseStack, dynamicTag, dynamicTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, dynamicTag, lastX, startY, U_DYNAMIC_OFFSET, this.render);
        }

        if (isReload != null)
        {
            renderTooltip(screen, poseStack, reloadTag, reloadTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, reloadTag, lastX, startY, U_RELOAD_OFFSET, this.render);
        }

        if (isRestart != null)
        {
            renderTooltip(screen, poseStack, restartTag, restartTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, restartTag, lastX, startY, U_RESTART_OFFSET, this.render);
        }

        if (isWarning != null)
        {
            renderTooltip(screen, poseStack, warningTag, warningTooltip, lastX, startY, mouseX, mouseY);
            lastX = renderTag(screen, poseStack, warningTag, lastX, startY, U_WARNING_OFFSET, this.render);
        }

        this.x = startX;
        this.setWidth(lastX - startX);
    }

    @Override public void appendNarrations(NarrationMessageBuilder narrationElementOutput) {}
}
