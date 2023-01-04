package mod.adrenix.nostalgic.fabric.event;

import mod.adrenix.nostalgic.fabric.event.server.ConnectionEvents;
import mod.adrenix.nostalgic.fabric.event.server.LifecycleEvents;

/**
 * Registers server-side Fabric events.
 */

public abstract class ServerEvents
{
    /**
     * Invokes the registration methods of various event group helper classes.
     * Extra instructions may be included in these helper classes.
     */
    public static void register()
    {
        LifecycleEvents.register();
        ConnectionEvents.register();
    }
}
