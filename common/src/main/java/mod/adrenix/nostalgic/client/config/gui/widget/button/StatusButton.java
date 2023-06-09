package mod.adrenix.nostalgic.client.config.gui.widget.button;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.config.gui.screen.config.ConfigWidgets;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.annotation.TweakData;
import mod.adrenix.nostalgic.common.config.reflect.TweakStatus;
import mod.adrenix.nostalgic.common.config.tweak.GuiTweak;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.client.RunUtil;
import mod.adrenix.nostalgic.util.common.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This is the flashing (!) exclamation mark symbol that sits next to control widgets in configuration rows.
 * The flashing mechanism is publicly static shared between other widgets so visible flashes is in sync.
 */

public class StatusButton extends Button
{
    /* Static Fields & Methods */

    private static boolean flipState = false;
    private static long currentTime = 0L;

    /**
     * @return The current state of the flashing flag.
     */
    public static boolean isFlashOff() { return flipState; }

    /**
     * Update the flip state flag for exclamation or alert tag flashing.
     */
    public static void update()
    {
        if (currentTime == 0)
            currentTime = Util.getMillis();

        if (Util.getMillis() - currentTime > 1000)
        {
            currentTime = 0;
            flipState = !flipState;
        }
    }

    /* Fields */

    private final AbstractWidget anchor;
    private final TweakClientCache<?> tweak;
    @Nullable private final TweakServerCache<?> server;

    /* Constructor */

    public StatusButton(TweakClientCache<?> tweak, AbstractWidget anchor)
    {
        super(0, 0, 0, 0, Component.empty(), RunUtil::nothing, DEFAULT_NARRATION);

        this.tweak = tweak;
        this.anchor = anchor;
        this.server = this.tweak.getServerTweak();
    }

    /* Methods */

    /**
     * @return Checks if the player has permission to change the value of a tweak. Locking will only happen when a
     * player is connected to a modded server with this mod installed.
     */
    private boolean isTweakLocked()
    {
        if (this.tweak.isClient())
            return false;
        else if (Minecraft.getInstance().player != null)
            return !NetUtil.isPlayerOp(Minecraft.getInstance().player);

        return false;
    }

    /**
     * Renders a tooltip based on the given language file key.
     * @param screen A config screen instance.
     * @param langKey A language file key.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     */
    private void renderTooltip(ConfigScreen screen, String langKey, GuiGraphics graphics, int mouseX, int mouseY)
    {
        List<Component> tooltip = TextUtil.Wrap.tooltip(Component.translatable(langKey), 40);
        screen.renderLast.add(() -> graphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY));
    }

    /**
     * Renders the flashing (!) symbol.
     * @param graphics The current GuiGraphics object.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param partialTick The change in frame time.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        Minecraft minecraft = Minecraft.getInstance();
        TweakStatus tweakStatus = this.tweak.getStatus();
        TweakStatus serverStatus = this.server == null ? tweakStatus : this.server.getStatus();
        TweakData.Dynamic dynamic = this.tweak.getMetadata(TweakData.Dynamic.class);

        if (tweakStatus == TweakStatus.FAIL && serverStatus != TweakStatus.FAIL)
            tweakStatus = serverStatus;

        boolean isTweakDynamic = dynamic != null && minecraft.level != null && tweakStatus != TweakStatus.FAIL && NetUtil.isMultiplayer();
        boolean isTweakLocked = this.isTweakLocked();
        boolean isNetVerified = NostalgicTweaks.isNetworkVerified() || this.tweak.isClient() || Minecraft.getInstance().level == null;
        boolean isStatusProblem = !isNetVerified || isTweakLocked;

        if (tweakStatus == TweakStatus.LOADED && !isStatusProblem && !isTweakDynamic)
            return;

        if (!isTweakDynamic)
        {
            boolean isRenderable = (Boolean) TweakClientCache.get(GuiTweak.DISPLAY_FEATURE_STATUS).getValue();
            if (!isRenderable && tweakStatus != TweakStatus.WARN)
                return;
        }

        if (NetUtil.isMultiplayer() && tweakStatus == TweakStatus.WAIT)
            tweakStatus = TweakStatus.FAIL;

        if (ClassUtil.isNotInstanceOf(minecraft.screen, ConfigScreen.class))
            return;

        ConfigScreen screen = (ConfigScreen) minecraft.screen;
        TweakStatus status = flipState ? TweakStatus.LOADED : tweakStatus;

        int uWidth = 4;
        int vHeight = 20;
        int xStart = this.anchor.getX() - ConfigRowList.ROW_WIDGET_GAP - uWidth;
        int yStart = this.anchor.getY();

        StatusButton.update();

        if (isStatusProblem && !flipState)
            graphics.blit(TextureLocation.WIDGETS, xStart, yStart, 21, 0, uWidth, vHeight);
        else if (isTweakDynamic && !flipState)
            graphics.blit(TextureLocation.WIDGETS, xStart, yStart, 21, 21, uWidth, vHeight);
        else
        {
            switch (status)
            {
                case LOADED -> graphics.blit(TextureLocation.WIDGETS, xStart, yStart, 14, 0, uWidth, vHeight);
                case WAIT -> graphics.blit(TextureLocation.WIDGETS, xStart, yStart, 14, 21, uWidth, vHeight);
                case WARN -> graphics.blit(TextureLocation.WIDGETS, xStart, yStart, 21, 0, uWidth, vHeight);
                case FAIL -> graphics.blit(TextureLocation.WIDGETS, xStart, yStart, 27, 0, uWidth, vHeight);
            }
        }

        boolean isOverSymbol = MathUtil.isWithinBox(mouseX, mouseY, xStart, yStart, uWidth, vHeight);
        boolean isWithinList = ConfigWidgets.isInsideRowList(mouseY);

        if (isOverSymbol && isWithinList)
        {
            if (!isNetVerified)
                this.renderTooltip(screen, LangUtil.Gui.STATUS_NET, graphics, mouseX, mouseY);
            else if (isTweakLocked)
                this.renderTooltip(screen, LangUtil.Gui.STATUS_PERM, graphics, mouseX, mouseY);
            else if (isTweakDynamic)
            {
                String state = NostalgicTweaks.isNetworkVerified() && NetUtil.isPlayerOp() ? LangUtil.Gui.STATUS_DYNAMIC_OP :
                    NostalgicTweaks.isNetworkVerified() ? LangUtil.Gui.STATUS_DYNAMIC_OFF : LangUtil.Gui.STATUS_DYNAMIC_ON
                ;

                this.renderTooltip(screen, state, graphics, mouseX, mouseY);
            }
            else
            {
                switch (tweakStatus)
                {
                    case WAIT -> this.renderTooltip(screen, LangUtil.Gui.STATUS_WAIT, graphics, mouseX, mouseY);
                    case WARN -> this.renderTooltip(screen, LangUtil.Gui.STATUS_WARN, graphics, mouseX, mouseY);
                    case FAIL -> this.renderTooltip(screen, LangUtil.Gui.STATUS_FAIL, graphics, mouseX, mouseY);
                }
            }
        }
    }
}
