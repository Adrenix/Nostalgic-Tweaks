package mod.adrenix.nostalgic.util.server;

import mod.adrenix.nostalgic.util.common.timer.TickTimer;

import java.util.HashSet;

public class ServerTimer implements TickTimer.Manager
{
    /* Singleton */

    private static final ServerTimer INSTANCE = new ServerTimer();

    public static ServerTimer getInstance()
    {
        return INSTANCE;
    }

    /* Fields */

    private final HashSet<TickTimer> timers = new HashSet<>();

    /* Constructor */

    private ServerTimer()
    {
    }

    /* Methods */

    @Override
    public HashSet<TickTimer> getTimers()
    {
        return this.timers;
    }
}
