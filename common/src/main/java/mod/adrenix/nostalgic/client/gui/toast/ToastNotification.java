package mod.adrenix.nostalgic.client.gui.toast;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.client.network.NetUtil;
import mod.adrenix.nostalgic.util.common.timer.SimpleTimer;

import java.util.concurrent.TimeUnit;

public abstract class ToastNotification
{
    /**
     * This timer keeps track of when the last pop-up occurred on screen. To prevent spam, pop-up toasts can only appear
     * every 5 seconds.
     */
    private static final SimpleTimer TIMER = SimpleTimer.create(5L, TimeUnit.SECONDS).immediate().build();

    /**
     * Pops up toasts on the client if the time since the last pop-up is greater than 5 seconds.
     *
     * @param id   A nostalgic toast identifier.
     * @param time How long in milliseconds this toast will be open.
     */
    public static void add(ToastId id, long time)
    {
        if (TIMER.hasElapsed())
            ModToast.getInstance(id).setTimer(time).open();
    }

    /**
     * Notifies the client that a tweak update was sent to the client from the server.
     */
    public static void changeOnClient()
    {
        if (!NetUtil.isMultiplayer())
            return;

        ToastNotification.add(ToastId.CLIENTBOUND_TWEAK, 8500L);
    }

    /**
     * Notifies the client that a tweak was sent to the server.
     */
    public static void changeOnServer()
    {
        if (!NetUtil.isMultiplayer())
            return;

        ToastNotification.add(ToastId.SERVERBOUND_TWEAK, 8500L);
    }

    /**
     * Notifies the client that it connected to a world with Nostalgic Tweaks installed.
     */
    public static void handshake()
    {
        if (!NostalgicTweaks.isNetworkVerified() || NetUtil.isSingleplayer())
            return;

        ToastNotification.add(ToastId.HANDSHAKE, 10000L);
    }
}
