package mod.adrenix.nostalgic.fabric.event.client;

import mod.adrenix.nostalgic.client.event.ClientEventHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

/**
 * Fabric Networking
 */

public abstract class FabricClientNetworkEvents
{
    public static void register()
    {
        onLeaveWorld();
    }

    /**
     * Changes the network verification state of the mod.
     *
     * Network verification is authenticated when the client receives a
     * {@link mod.adrenix.nostalgic.network.packet.PacketS2CHandshake PacketS2CHandshake} packet from a modded server.
     * If a server is not N.T supported, then network verification is false, and we shouldn't be sending packets to
     * the server.
     */
    public static void onLeaveWorld()
    {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientEventHelper.disconnect());
    }
}
