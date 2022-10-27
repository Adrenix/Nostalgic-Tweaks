package mod.adrenix.nostalgic.fabric.event;

import mod.adrenix.nostalgic.fabric.event.server.ServerEvents;

/**
 * Registers events that should be available server-side.
 */

public abstract class ServerEventHandler
{
    public static void register() { ServerEvents.register(); }
}
