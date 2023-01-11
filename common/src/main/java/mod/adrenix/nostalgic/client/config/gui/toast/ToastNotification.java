package mod.adrenix.nostalgic.client.config.gui.toast;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.client.NetUtil;
import mod.adrenix.nostalgic.util.common.TimeWatcher;

/**
 * Helper class for popping up toasts on the client screen.
 * This utility comes with a spam blocker.
 */

public abstract class ToastNotification
{
    /* Fields */

    /**
     * This time watcher keeps track of when the last pop-up occurred on screen.
     * To prevent spam, a pop-up toast can only appear every 5 seconds.
     */
    private static final TimeWatcher TIMER = new TimeWatcher(5000L);

    /* Methods */

    /**
     * Pops up a toast on the client if the time since the last pop-up is greater than 5 seconds.
     * @param id A nostalgic toast identifier.
     * @param time How long in milliseconds is this toast open for.
     */
    public static void add(ToastId id, long time)
    {
        if (TIMER.isReady())
            NostalgicToast.getInstance(id).setTimer(new TimeWatcher(time)).open();
    }

    /**
     * Notifies the client that a tweak update was sent to the client from the server.
     */
    public static void gotChanges()
    {
        if (!NetUtil.isMultiplayer())
            return;

        ToastNotification.add(ToastId.TWEAK_S2C, 7500L);
    }

    /**
     * Notifies the client that a tweak was sent to the server.
     */
    public static void sentChanges()
    {
        if (!NetUtil.isMultiplayer())
            return;

        ToastNotification.add(ToastId.TWEAK_C2S, 7500L);
    }

    /**
     * Notifies the client that it connected to a world with Nostalgic Tweaks installed.
     */
    public static void handshake()
    {
        if (!NostalgicTweaks.isNetworkVerified() || NetUtil.isSingleplayer())
            return;

        ToastNotification.add(ToastId.HANDSHAKE, 8000L);
    }
}
