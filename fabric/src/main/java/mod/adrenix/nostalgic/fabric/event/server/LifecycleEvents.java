package mod.adrenix.nostalgic.fabric.event.server;

import mod.adrenix.nostalgic.server.event.ServerEventHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Fabric server lifecycle events.
 * Registration is handled by {@link mod.adrenix.nostalgic.fabric.event.ServerEvents}.
 */

public abstract class LifecycleEvents
{
    /**
     * Register lifecycle events.
     */
    public static void register()
    {
        // This is needed to perform configuration syncing events later on
        ServerLifecycleEvents.SERVER_STARTED.register(ServerEventHelper::instantiate);
    }
}
