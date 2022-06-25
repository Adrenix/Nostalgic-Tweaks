package mod.adrenix.nostalgic.fabric.event.common;

import mod.adrenix.nostalgic.server.event.ServerEventHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public abstract class FabricConfigEvents
{
    public static void register()
    {
        onJoinWorld();
    }

    /* Config Events */

    /**
     * Requests the server to sync the current server tweaks with the client.
     * This will fire on each world join. Since tweak caches work universally, no extra work is required.
     */
    public static void onJoinWorld()
    {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ServerEventHelper.connect(handler.getPlayer()));
    }
}
