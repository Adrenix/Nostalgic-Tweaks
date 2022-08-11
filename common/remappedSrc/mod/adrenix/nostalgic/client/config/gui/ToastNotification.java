package mod.adrenix.nostalgic.client.config.gui;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.client.NetClientUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import mod.adrenix.nostalgic.util.NostalgicLang;

/**
 * Helper class for popping up toasts on the client screen.
 * This utility comes with a spam blocker.
 */

public abstract class ToastNotification
{
    private static long timeSinceLast = 0L;

    /**
     * Pops up a toast on the client if the time since the last pop-up is greater than 5 seconds.
     * @param toast The toast to add.
     */
    public static void add(Toast toast)
    {
        if (timeSinceLast == 0L)
            timeSinceLast = Util.getMeasuringTimeMs() - 5000L;

        if (Util.getMeasuringTimeMs() - timeSinceLast >= 5000L)
        {
            timeSinceLast = Util.getMeasuringTimeMs();
            MinecraftClient.getInstance().getToastManager().add(toast);
        }
    }

    private static void add(String headKey, String bodyKey)
    {
        Text header = Text.translatable(headKey).withStyle(Formatting.WHITE);
        Text body = Text.translatable(bodyKey).withStyle(Formatting.YELLOW);
        add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, header, body));
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
