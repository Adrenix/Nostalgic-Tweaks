package mod.adrenix.nostalgic.client.config.gui;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.network.chat.Component;

/**
 * Helper class for popping up toasts on the client screen.
 * This utility comes with a spam blocker.
 */

public abstract class ToastNotification
{
    /* Fields */

    /**
     * This long integer keeps track of when the last pop-up occurred on screen.
     * To prevent spam, a pop-up can only appear every 5 seconds.
     */
    private static long timeSinceLast = 0L;

    /* Methods */

    /**
     * Pops up a toast on the client if the time since the last pop-up is greater than 5 seconds.
     * @param toast The toast to add.
     */
    public static void add(Toast toast)
    {
        if (timeSinceLast == 0L)
            timeSinceLast = Util.getMillis() - 5000L;

        if (Util.getMillis() - timeSinceLast >= 5000L)
        {
            timeSinceLast = Util.getMillis();
            Minecraft.getInstance().getToasts().addToast(toast);
        }
    }

    /**
     * Helper method that adds a new toast to the game screen.
     * @param headerKey A language file key for the toast header.
     * @param bodyKey A language file key for the toast body.
     */
    private static void add(String headerKey, String bodyKey)
    {
        Component header = Component.translatable(headerKey).withStyle(ChatFormatting.WHITE);
        Component body = Component.translatable(bodyKey).withStyle(ChatFormatting.YELLOW);

        add(new SystemToast(SystemToast.SystemToastIds.TUTORIAL_HINT, header, body));
    }

    /**
     * Notifies the client that a tweak update was sent to the client from the server.
     */
    public static void addTweakUpdate()
    {
        if (!NetUtil.isMultiplayer())
            return;

        add(LangUtil.Gui.TOAST_TWEAK_UPDATE_HEADER, LangUtil.Gui.TOAST_TWEAK_UPDATE_BODY);
    }

    /**
     * Notifies the client that a tweak was sent to the server.
     */
    public static void addTweakChange()
    {
        if (!NetUtil.isMultiplayer())
            return;

        add(LangUtil.Gui.TOAST_TWEAK_SENT_HEADER, LangUtil.Gui.TOAST_TWEAK_SENT_BODY);
    }

    /**
     * Notifies the client that it connected to a world with Nostalgic Tweaks installed.
     */
    public static void addServerHandshake()
    {
        if (!NostalgicTweaks.isNetworkVerified() || NetUtil.isSingleplayer())
            return;

        add(LangUtil.Gui.TOAST_SYNC_HEADER, LangUtil.Gui.TOAST_SYNC_BODY);
    }
}
