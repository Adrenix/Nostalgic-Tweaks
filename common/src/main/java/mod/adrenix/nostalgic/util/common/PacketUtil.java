package mod.adrenix.nostalgic.util.common;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.client.NetUtil;
import net.fabricmc.api.EnvType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Keep client code out of this class.
 * This is a utility for both client and server.
 */

public abstract class PacketUtil
{
    /**
     * S2C - Server to Client:
     *
     * Send a packet to a connected server player.
     *
     * @param player The server player to send the packet to.
     * @param packet The packet to send to the server player.
     * @param <Packet> A packet class.
     */
    public static <Packet> void sendToPlayer(ServerPlayer player, Packet packet)
    {
        NostalgicTweaks.NETWORK.sendToPlayer(player, packet);
        NostalgicTweaks.LOGGER.debug("(S2C) Sent (%s) to (%s)", packet, player);
    }

    /**
     * S2C - Server to Client:
     *
     * This is the best method to use when sending a packet to multiple players.
     * If a player list is unavailable use {@link #sendToAll(Object)}.
     *
     * @param players A list of players to send the packet to.
     * @param packet The packet to send to the list of players.
     * @param <Packet> A packet class.
     */
    public static <Packet> void sendToAll(Iterable<ServerPlayer> players, Packet packet)
    {
        NostalgicTweaks.NETWORK.sendToPlayers(players, packet);
        NostalgicTweaks.LOGGER.debug("(S2C) Sent (%s) to all players", packet);
    }

    /**
     * S2C - Server to Client:
     *
     * This method can only be used in a dedicated server environment.
     *
     * The alternative {@link #sendToAll(Iterable, Object)} should be used unless there is no access to a player list,
     * which should only happen in a dedicated server environment anyway.
     *
     * @param packet The packet to send to all players.
     * @param <Packet> A packet class.
     * @throws AssertionError Will be thrown if there is no dedicated server instance.
     */
    public static <Packet> void sendToAll(Packet packet) throws AssertionError
    {
        MinecraftServer server = NostalgicTweaks.getServer();

        if (server == null)
            throw new AssertionError(String.format("Tried to send (%s) but there was no server instance available", packet.getClass()));

        sendToAll(server.getPlayerList().getPlayers(), packet);
    }

    /**
     * C2S - Client to Server:
     *
     * This method should be used when the client seeks to send a packet to the server.
     *
     * @param packet The packet to send to the server.
     * @param <Packet> A packet class.
     */
    public static <Packet> void sendToServer(Packet packet)
    {
        if (NostalgicTweaks.isNetworkVerified())
        {
            NostalgicTweaks.NETWORK.sendToServer(packet);
            NostalgicTweaks.LOGGER.debug("(C2S) Sent (%s) to server", packet);
        }
        else
            NostalgicTweaks.LOGGER.info("N.T network is unverified - not sending %s", packet.getClass());
    }

    /**
     * This is a server safe method to check if a player is an operator.
     * For client network utility see {@link NetUtil}.
     * @param player The server player to check permissions on.
     * @return Whether the server player is an operator. (level >= 2)
     */
    public static boolean isPlayerOp(ServerPlayer player) { return player.hasPermissions(2); }

    /**
     * This is a helper method that informs the console that a packet was processed on the wrong logical side.
     * @param side The side to the packet was accidentally sent to.
     * @param packet A packet class.
     */
    public static void warn(EnvType side, Class<?> packet)
    {
        String receiver = side.equals(EnvType.CLIENT) ? "Client" : "Server";
        String bound = side.equals(EnvType.CLIENT) ? "server" : "client";
        String warn = String.format
        (
            "%s is processing (%s) a %s bound packet. This shouldn't happen!",
            receiver,
            packet,
            bound
        );

        NostalgicTweaks.LOGGER.warn(warn);
    }
}
