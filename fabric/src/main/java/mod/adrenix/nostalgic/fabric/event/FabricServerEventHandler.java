package mod.adrenix.nostalgic.fabric.event;

import mod.adrenix.nostalgic.fabric.event.server.FabricServerEvents;

/**
 * Registers events that should be available server-side.
 */

public abstract class FabricServerEventHandler
{
    public static void register()
    {
        FabricServerEvents.register();
    }
}
