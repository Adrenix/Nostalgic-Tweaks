package mod.adrenix.nostalgic.fabric.event;

import mod.adrenix.nostalgic.fabric.event.client.CandyEvents;
import mod.adrenix.nostalgic.fabric.event.client.ClientNetworkEvents;

/**
 * Registers client-side Fabric events.
 */

public abstract class ClientEventHandler
{
    /**
     * Invokes the registration methods of various event group helper classes.
     * Extra instructions may be included in these helper classes.
     */
    public static void register()
    {
        CandyEvents.register();
        ClientNetworkEvents.register();
    }
}
