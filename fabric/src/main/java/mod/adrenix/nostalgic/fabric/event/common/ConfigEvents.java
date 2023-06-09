package mod.adrenix.nostalgic.fabric.event.common;

import mod.adrenix.nostalgic.server.event.ServerEventHelper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

/**
 * Fabric configuration related event instructions and registration.
 * Registration is invoked by client/server event handlers.
 */

public abstract class ConfigEvents
{
    /**
     * Register config related Fabric events.
     */
    public static void register() { ServerPlayConnectionEvents.JOIN.register(ConfigEvents::connect); }

    /* Config Events */

    /**
     * Requests the server to sync the current server tweaks with the client.
     * This will fire on each world join. Since tweak caches work universally, no extra work is required.
     */
    private static void connect(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server)
    {
        ServerEventHelper.connect(handler.getPlayer());
    }
}
