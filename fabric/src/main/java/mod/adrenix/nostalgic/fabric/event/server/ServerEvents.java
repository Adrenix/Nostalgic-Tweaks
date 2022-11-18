package mod.adrenix.nostalgic.fabric.event.server;

import mod.adrenix.nostalgic.server.event.ServerEventHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Fabric server related event instructions and registration.
 * Registration is invoked by server event handlers.
 */

public abstract class ServerEvents
{
    /**
     * Register server related Fabric events.
     */
    public static void register() { onFinishStartup(); }

    /* Server Events */

    /**
     * Defines the dedicated server reference field in the mod's main class.
     * This is needed to perform configuration syncing events later on.
     */
    public static void onFinishStartup()
    {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerEventHelper::instantiate);
    }
}
