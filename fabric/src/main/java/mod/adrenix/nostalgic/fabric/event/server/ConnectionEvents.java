package mod.adrenix.nostalgic.fabric.event.server;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.fabric.NostalgicCommonFabric;
import mod.adrenix.nostalgic.server.config.ServerConfigCache;
import mod.adrenix.nostalgic.util.common.ComponentUtil;
import mod.adrenix.nostalgic.util.common.LinkLocation;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

/**
 * Fabric server related connection events. Registration is handled by
 * {@link mod.adrenix.nostalgic.fabric.event.ServerEvents}.
 */

public abstract class ConnectionEvents
{
    /**
     * Register connection events.
     */
    public static void register()
    {
        ServerLoginConnectionEvents.QUERY_START.register(ConnectionEvents::request);
        ServerLoginNetworking.registerGlobalReceiver(NostalgicCommonFabric.VERIFY_PROTOCOL, ConnectionEvents::verify);
    }

    /**
     * Sends a packet to the client asking for a network protocol version. If the request was understood, then the
     * protocol version will be verified in a separate method.
     */
    private static void request(ServerLoginPacketListenerImpl handler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer)
    {
        sender.sendPacket(NostalgicCommonFabric.VERIFY_PROTOCOL, PacketByteBufs.empty());
    }

    /**
     * Ensures that the connecting player has the mod installed with the correct protocol version. This is needed since
     * players connecting to this server without the correct protocol may have a bad experience. Server administrators
     * have the option to enable an experimental server-side-only (SSO) mode. This will allow players to connect to the
     * server without Nostalgic Tweaks installed.
     */
    private static void verify(MinecraftServer server, ServerLoginPacketListenerImpl handler, boolean understood, FriendlyByteBuf buffer, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender sender)
    {
        if (ServerConfigCache.getRoot().serverSideOnlyMode)
            return;

        final MutableComponent SERVER_VERSION = ComponentUtil.color(NostalgicTweaks.getFullVersion(), 0xFAEEAA);
        final MutableComponent MOD_NAME = ComponentUtil.color(NostalgicTweaks.MOD_NAME, 0xFFFF00);
        final MutableComponent MOD_LINK = ComponentUtil.color(LinkLocation.DOWNLOAD, 0x11BDED);

        if (understood)
        {
            final String CLIENT_PROTOCOL = buffer.readUtf();
            final String SERVER_PROTOCOL = NostalgicTweaks.PROTOCOL;

            if (!CLIENT_PROTOCOL.equals(SERVER_PROTOCOL))
            {
                final String MESSAGE = """
                        Your %s protocol (%s) did not match the server protocol (%s).
                        Network protocols must match; however, mod versions don't need to match.
                                            
                        This server is running %s (%s).
                        %s
                    """;

                handler.disconnect(Component.translatable(MESSAGE, MOD_NAME, ComponentUtil.color(CLIENT_PROTOCOL, 0xF87C73), ComponentUtil.color(SERVER_PROTOCOL, 0xF8BE73), MOD_NAME, SERVER_VERSION, MOD_LINK));
            }
        }
        else
            handler.disconnect(Component.translatable("You need %s (%s) to join this server.\n%s", MOD_NAME, SERVER_VERSION, MOD_LINK));
    }
}
