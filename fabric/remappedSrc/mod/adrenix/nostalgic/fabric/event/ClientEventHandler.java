package mod.adrenix.nostalgic.fabric.event;

import mod.adrenix.nostalgic.fabric.event.client.CandyEvents;
import mod.adrenix.nostalgic.fabric.event.client.ClientNetworkEvents;

/**
 * Registers events that should be available client-side.
 */

public abstract class ClientEventHandler
{
    public static void register()
    {
        CandyEvents.register();
        ClientNetworkEvents.register();
    }
}
