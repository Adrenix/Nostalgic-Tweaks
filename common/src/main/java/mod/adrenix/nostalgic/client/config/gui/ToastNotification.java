package mod.adrenix.nostalgic.client.config.gui;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.client.NetClientUtil;
import mod.adrenix.nostalgic.util.NostalgicLang;
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
    private static long timeSinceLast = 0L;

    /**
     * Pops up a toast on the client if the time since the last pop up is greater than 5 seconds.
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

    private static void add(String headKey, String bodyKey)
    {
        Component header = Component.translatable(headKey).withStyle(ChatFormatting.WHITE);
        Component body = Component.translatable(bodyKey).withStyle(ChatFormatting.YELLOW);
        add(new SystemToast(SystemToast.SystemToastIds.TUTORIAL_HINT, header, body));
    }

    /**
     * Notifies the client that a tweak update was sent to the client from the server.
     */
    public static void addTweakUpdate()
    {
        if (!NetClientUtil.isMultiplayer())
            return;

        add(NostalgicLang.Gui.TOAST_TWEAK_UPDATE_HEADER, NostalgicLang.Gui.TOAST_TWEAK_UPDATE_BODY);
    }

    /**
     * Notifies the client that a tweak was sent to the server.
     */
    public static void addTweakChange()
    {
        if (!NetClientUtil.isMultiplayer())
            return;

        add(NostalgicLang.Gui.TOAST_TWEAK_SENT_HEADER, NostalgicLang.Gui.TOAST_TWEAK_SENT_BODY);
    }

    /**
     * Notifies the client that it connected to a world with Nostalgic Tweaks installed.
     */
    public static void addServerHandshake()
    {
        if (!NostalgicTweaks.isNetworkVerified() || NetClientUtil.isSingleplayer())
            return;

        add(NostalgicLang.Gui.TOAST_SYNC_HEADER, NostalgicLang.Gui.TOAST_SYNC_BODY);
    }
}
