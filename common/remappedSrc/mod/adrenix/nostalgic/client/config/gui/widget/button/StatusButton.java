package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.annotation.TweakSide;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.tweak.GuiTweak;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.client.NetClientUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import org.jetbrains.annotations.Nullable;

public class StatusButton extends ButtonWidget
{
    protected static boolean flipState = false;
    protected static long currentTime = 0L;
    protected final ClickableWidget anchor;
    protected final TweakClientCache<?> cache;
    @Nullable protected final TweakServerCache<?> server;

    public StatusButton(TweakClientCache<?> cache, ClickableWidget anchor)
    {
        super(0, 0, 0, 0, Text.empty(), (ignored) -> {});
        this.cache = cache;
        this.anchor = anchor;
        this.server = this.cache.getServerTweak();
    }

    private boolean isTweakLocked()
    {
        if (this.cache.isClient())
            return false;
        else if (MinecraftClient.getInstance().player != null)
            return !NetClientUtil.isPlayerOp(MinecraftClient.getInstance().player);
        return false;
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        StatusType cacheStatus = this.cache.getStatus();
        StatusType serverStatus = this.server == null ? cacheStatus : this.server.getStatus();

        if (cacheStatus == StatusType.FAIL && serverStatus != StatusType.FAIL)
            cacheStatus = serverStatus;

        TweakSide.Dynamic dynamic = CommonReflect.getAnnotation(this.cache, TweakSide.Dynamic.class);
        boolean isTweakDynamic = dynamic != null && minecraft.world != null && cacheStatus != StatusType.FAIL && NetClientUtil.isMultiplayer();
        boolean isTweakLocked = this.isTweakLocked();
        boolean isNetVerified = NostalgicTweaks.isNetworkVerified() || this.cache.isClient() || MinecraftClient.getInstance().world == null;
        boolean isStatusProblem = !isNetVerified || isTweakLocked;

        if (cacheStatus == StatusType.LOADED && !isStatusProblem && !isTweakDynamic)
            return;

        if (!isTweakDynamic)
        {
            boolean isRenderable = (Boolean) TweakClientCache.get(GuiTweak.DISPLAY_FEATURE_STATUS).getCurrent();
            if (!isRenderable && cacheStatus != StatusType.WARN)
                return;
        }

        if (NetClientUtil.isMultiplayer() && cacheStatus == StatusType.WAIT)
            cacheStatus = StatusType.FAIL;

        StatusType status = flipState ? StatusType.LOADED : cacheStatus;
        RenderSystem.setShaderTexture(0, NostalgicUtil.Resource.WIDGETS_LOCATION);
        Screen screen = minecraft.currentScreen;

        if (screen == null) return;
        if (currentTime == 0) currentTime = Util.getMeasuringTimeMs();
        if (Util.getMeasuringTimeMs() - currentTime > 1000)
        {
            currentTime = 0;
            flipState = !flipState;
        }

        int uWidth = 4;
        int vHeight = 20;
        int xStart = this.anchor.x - ConfigRowList.ROW_WIDGET_GAP - uWidth;
        int yStart = this.anchor.y;
        boolean isMouseOver = (mouseX >= xStart && mouseX <= xStart + uWidth) && (mouseY >= yStart && mouseY <= yStart + vHeight);

        if (isStatusProblem && !flipState)
            screen.drawTexture(poseStack, xStart, yStart, 21, 0, uWidth, vHeight);
        else if (isTweakDynamic && !flipState)
            screen.drawTexture(poseStack, xStart, yStart, 21, 21, uWidth, vHeight);
        else
        {
            switch (status)
            {
                case LOADED -> screen.blit(poseStack, xStart, yStart, 14, 0, uWidth, vHeight);
                case WAIT -> screen.blit(poseStack, xStart, yStart, 14, 21, uWidth, vHeight);
                case WARN -> screen.blit(poseStack, xStart, yStart, 21, 0, uWidth, vHeight);
                case FAIL -> screen.blit(poseStack, xStart, yStart, 27, 0, uWidth, vHeight);
            }
        }

        if (isMouseOver && screen instanceof ConfigScreen)
        {
            if (!isNetVerified)
            {
                ((ConfigScreen) screen).renderLast.add(() ->
                    screen.renderTooltip(poseStack, NostalgicUtil.Wrap.tooltip(Text.translatable(NostalgicLang.Gui.STATUS_NET), 40), mouseX, mouseY));
                return;
            }
            else if (isTweakLocked)
            {
                ((ConfigScreen) screen).renderLast.add(() ->
                    screen.renderTooltip(poseStack, NostalgicUtil.Wrap.tooltip(Text.translatable(NostalgicLang.Gui.STATUS_PERM), 40), mouseX, mouseY));
                return;
            }
            else if (isTweakDynamic)
            {
                String state = NostalgicTweaks.isNetworkVerified() && NetClientUtil.isPlayerOp() ? NostalgicLang.Gui.STATUS_DYNAMIC_OP :
                    NostalgicTweaks.isNetworkVerified() ? NostalgicLang.Gui.STATUS_DYNAMIC_OFF : NostalgicLang.Gui.STATUS_DYNAMIC_ON
                ;

                ((ConfigScreen) screen).renderLast.add(() ->
                    screen.renderTooltip(poseStack, NostalgicUtil.Wrap.tooltip(Text.translatable(state), 40), mouseX, mouseY));
                return;
            }

            switch (cacheStatus)
            {
                case WAIT ->
                    ((ConfigScreen) screen).renderLast.add(() ->
                        screen.renderComponentTooltip(poseStack, NostalgicUtil.Wrap.tooltip(Component.translatable(NostalgicLang.Gui.STATUS_WAIT), 40), mouseX, mouseY));
                case WARN ->
                    ((ConfigScreen) screen).renderLast.add(() ->
                        screen.renderComponentTooltip(poseStack, NostalgicUtil.Wrap.tooltip(Component.translatable(NostalgicLang.Gui.STATUS_WARN), 40), mouseX, mouseY));
                case FAIL ->
                    ((ConfigScreen) screen).renderLast.add(() ->
                        screen.renderComponentTooltip(poseStack, NostalgicUtil.Wrap.tooltip(Component.translatable(NostalgicLang.Gui.STATUS_FAIL), 40), mouseX, mouseY));
            }
        }
    }
}
