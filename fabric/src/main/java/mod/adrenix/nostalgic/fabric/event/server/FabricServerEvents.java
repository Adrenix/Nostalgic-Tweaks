package mod.adrenix.nostalgic.fabric.event.server;

import mod.adrenix.nostalgic.server.event.ServerEventHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public abstract class FabricServerEvents
{
    public static void register()
    {
        onFinishStartup();
    }

    /* Server Events */

    public static void onFinishStartup()
    {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerEventHelper::instantiate);
    }
}
