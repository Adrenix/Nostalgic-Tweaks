package mod.adrenix.nostalgic.fabric.event.client;

import mod.adrenix.nostalgic.client.event.ClientEventHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public abstract class FabricClientNetworkEvents
{
    public static void register()
    {
        onLeaveWorld();
    }

    public static void onLeaveWorld()
    {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientEventHelper.disconnect());
    }
}
