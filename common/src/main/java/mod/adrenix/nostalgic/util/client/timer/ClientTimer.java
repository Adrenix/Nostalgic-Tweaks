package mod.adrenix.nostalgic.util.client.timer;

import mod.adrenix.nostalgic.util.common.timer.TickTimer;

import java.util.HashSet;

public class ClientTimer implements TickTimer.Manager
{
    /* Singleton */

    private static final ClientTimer INSTANCE = new ClientTimer();

    public static ClientTimer getInstance()
    {
        return INSTANCE;
    }

    /* Fields */

    private final HashSet<TickTimer> timers = new HashSet<>();

    /* Constructor */

    private ClientTimer()
    {
    }

    /* Methods */

    @Override
    public HashSet<TickTimer> getTimers()
    {
        return this.timers;
    }
}
