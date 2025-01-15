package mod.adrenix.nostalgic.util.client.network;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class NetUtil
{
    /**
     * Check if a certain player has operator permissions.
     *
     * @param player The player to check.
     * @return Whether the player is an operator.
     */
    public static boolean isPlayerOp(Player player)
    {
        if (Minecraft.getInstance().hasSingleplayerServer())
            return true;

        return player.hasPermissions(2);
    }

    /**
     * Use this method to bypass the need to provide a specific player instance. If a certain player is to be checked
     * {@link #isPlayerOp(Player)}.
     *
     * @return Whether the player is an operator.
     */
    public static boolean isPlayerOp()
    {
        if (Minecraft.getInstance().player == null)
            return true;
        else
            return isPlayerOp(Minecraft.getInstance().player);
    }

    /**
     * This method only checks if the player is not in singleplayer, is not the local host, and whether the mod's
     * connection is verified.
     *
     * @return Whether the player is not in singleplayer and has a verified connection with a server running Nostalgic
     * Tweaks.
     */
    public static boolean isConnected()
    {
        return !isSingleplayer() && !isLocalHost() && NostalgicTweaks.isNetworkVerified();
    }

    /**
     * Checks if the game is not in singleplayer, checks if the network is verified, and checks if the player is an
     * operator to the connected server with Nostalgic Tweaks installed.
     *
     * @return Whether the game is connected to a server with the mod installed and the player is an operator.
     */
    public static boolean isConnectedAndOperator()
    {
        return isConnected() && isPlayerOp();
    }

    /**
     * This is a functional shortcut and is opposite to {@link #isConnectedAndOperator()}.
     *
     * @return Whether the game is not connected and/or the player is not an operator.
     */
    public static boolean isNotConnectedOrOperator()
    {
        return !isConnectedAndOperator();
    }

    /**
     * Checks if the loaded world is singleplayer.
     *
     * @return Whether the current session is singleplayer.
     */
    public static boolean isSingleplayer()
    {
        return Minecraft.getInstance().getSingleplayerServer() != null && !Minecraft.getInstance()
            .getSingleplayerServer()
            .isPublished();
    }

    /**
     * Checks if the loaded world is currently hosting a LAN session.
     *
     * @return Whether the current session is an integrated server.
     */
    public static boolean isLocalHost()
    {
        return Minecraft.getInstance().getSingleplayerServer() != null && Minecraft.getInstance()
            .getSingleplayerServer()
            .isPublished();
    }

    /**
     * Gets the integrated server if the client is a local host.
     *
     * @return The client's integrated server or {@code null}.
     */
    @Nullable
    public static IntegratedServer getIntegratedServer()
    {
        return isLocalHost() ? Minecraft.getInstance().getSingleplayerServer() : null;
    }

    /**
     * Gets the integrated server's list of connected players.
     *
     * @return A list of players or an empty list of the integrated server does not exist.
     */
    public static List<ServerPlayer> getIntegratedPlayers()
    {
        IntegratedServer server = getIntegratedServer();

        if (server == null)
            return List.of();

        return server.getPlayerList().getPlayers();
    }

    /**
     * Checks if the current world is not singleplayer and that a connection is established with a server.
     *
     * @return Whether the current session is in multiplayer or a LAN session.
     */
    public static boolean isMultiplayer()
    {
        if (isLocalHost())
            return true;

        return !isSingleplayer() && Minecraft.getInstance().getConnection() != null;
    }
}
