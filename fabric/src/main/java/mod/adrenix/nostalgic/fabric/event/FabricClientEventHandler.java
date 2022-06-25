package mod.adrenix.nostalgic.fabric.event;

import mod.adrenix.nostalgic.fabric.event.client.FabricCandyEvents;
import mod.adrenix.nostalgic.fabric.event.client.FabricClientNetworkEvents;

/**
 * Registers events that should be available client-side.
 */

public abstract class FabricClientEventHandler
{
    public static void register()
    {
        FabricCandyEvents.register();
        FabricClientNetworkEvents.register();
    }
}
