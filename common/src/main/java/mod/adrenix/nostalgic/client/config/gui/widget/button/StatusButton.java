package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.annotation.TweakSide;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.tweak.GuiTweak;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.widget.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.client.NetClientUtil;
import mod.adrenix.nostalgic.util.NostalgicLang;
import mod.adrenix.nostalgic.util.NostalgicUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class StatusButton extends Button
{
    protected static boolean flipState = false;
    protected static long currentTime = 0L;
    protected final AbstractWidget anchor;
    protected final TweakClientCache<?> cache;
    @Nullable protected final TweakServerCache<?> server;

    public StatusButton(TweakClientCache<?> cache, AbstractWidget anchor)
    {
        super(0, 0, 0, 0, Component.empty(), (ignored) -> {});
        this.cache = cache;
        this.anchor = anchor;
        this.server = this.cache.getServerTweak();
    }

    private boolean isTweakLocked()
    {
        if (this.cache.isClientSide())
            return false;
        else if (Minecraft.getInstance().player != null)
            return !NetClientUtil.isPlayerOp(Minecraft.getInstance().player);
        return false;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        Minecraft minecraft = Minecraft.getInstance();
        StatusType cacheStatus = this.cache.getStatus();
        StatusType serverStatus = this.server == null ? cacheStatus : this.server.getStatus();

        if (cacheStatus == StatusType.FAIL && serverStatus != StatusType.FAIL)
            cacheStatus = serverStatus;

        TweakSide.Dynamic dynamic = CommonReflect.getAnnotation(this.cache, TweakSide.Dynamic.class);
        boolean isTweakDynamic = dynamic != null && minecraft.level != null && cacheStatus != StatusType.FAIL && NetClientUtil.isMultiplayer();
        boolean isTweakLocked = this.isTweakLocked();
        boolean isNetVerified = NostalgicTweaks.isNetworkVerified() || this.cache.isClientSide() || Minecraft.getInstance().level == null;
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
        Screen screen = minecraft.screen;

        if (screen == null) return;
        if (currentTime == 0) currentTime = Util.getMillis();
        if (Util.getMillis() - currentTime > 1000)
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
            screen.blit(poseStack, xStart, yStart, 21, 0, uWidth, vHeight);
        else if (isTweakDynamic && !flipState)
            screen.blit(poseStack, xStart, yStart, 21, 21, uWidth, vHeight);
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
                    screen.renderComponentTooltip(poseStack, NostalgicUtil.Wrap.tooltip(Component.translatable(NostalgicLang.Gui.STATUS_NET), 40), mouseX, mouseY));
                return;
            }
            else if (isTweakLocked)
            {
                ((ConfigScreen) screen).renderLast.add(() ->
                    screen.renderComponentTooltip(poseStack, NostalgicUtil.Wrap.tooltip(Component.translatable(NostalgicLang.Gui.STATUS_PERM), 40), mouseX, mouseY));
                return;
            }
            else if (isTweakDynamic)
            {
                String state = NostalgicTweaks.isNetworkVerified() && NetClientUtil.isPlayerOp() ? NostalgicLang.Gui.STATUS_DYNAMIC_OP :
                    NostalgicTweaks.isNetworkVerified() ? NostalgicLang.Gui.STATUS_DYNAMIC_OFF : NostalgicLang.Gui.STATUS_DYNAMIC_ON
                ;

                ((ConfigScreen) screen).renderLast.add(() ->
                    screen.renderComponentTooltip(poseStack, NostalgicUtil.Wrap.tooltip(Component.translatable(state), 40), mouseX, mouseY));
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
