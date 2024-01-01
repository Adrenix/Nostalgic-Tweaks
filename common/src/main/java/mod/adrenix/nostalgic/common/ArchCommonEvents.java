package mod.adrenix.nostalgic.common;

import dev.architectury.event.events.common.TickEvent;
import mod.adrenix.nostalgic.util.server.ServerTimer;

/**
 * This class contains a registration helper method that will be used by the common initializers in Fabric and Forge.
 * The Architectury API provides the events used in this class.
 */
public class ArchCommonEvents
{
    /**
     * Registers common Architectury events.
     */
    public static void register()
    {
        // Handles ticking of server-side timer instances
        TickEvent.SERVER_PRE.register(server -> ServerTimer.getInstance().onTick());
    }
}
