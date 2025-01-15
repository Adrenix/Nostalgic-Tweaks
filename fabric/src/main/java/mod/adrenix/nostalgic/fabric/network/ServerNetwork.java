package mod.adrenix.nostalgic.fabric.network;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.LoginReply;
import mod.adrenix.nostalgic.network.ModConnection;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import net.fabricmc.fabric.api.networking.v1.LoginPacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

abstract class ServerNetwork
{
    /**
     * Sends a packet to the client requesting information on the mod's network protocol version, if the mod is
     * installed.
     */
    @SuppressWarnings("unused")
    static void sendProtocolRequest(ServerLoginPacketListenerImpl handler, MinecraftServer server, LoginPacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer)
    {
        sender.sendPacket(ModConnection.PROTOCOL_ID, PacketByteBufs.empty());
    }

    /**
     * Ensures that the connecting player is eligible to join the server based on the server's configuration. If
     * server-side-only mode is not enabled, then the player must have the mod installed. Regardless of server-side-only
     * mode, players with the mod installed must have a matching network protocol with the server.
     */
    @SuppressWarnings("unused")
    static void receiveProtocol(MinecraftServer server, ServerLoginPacketListenerImpl handler, boolean understood, FriendlyByteBuf buffer, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender sender)
    {
        if (understood)
        {
            final String CLIENT_PROTOCOL = buffer.readUtf();
            final String SERVER_PROTOCOL = NostalgicTweaks.PROTOCOL;

            if (!CLIENT_PROTOCOL.equals(SERVER_PROTOCOL))
                handler.disconnect(LoginReply.getProtocolMismatchReason(CLIENT_PROTOCOL, SERVER_PROTOCOL));
        }
        else if (!ModTweak.SERVER_SIDE_ONLY.get())
            handler.disconnect(LoginReply.getMissingModReason());
    }
}
