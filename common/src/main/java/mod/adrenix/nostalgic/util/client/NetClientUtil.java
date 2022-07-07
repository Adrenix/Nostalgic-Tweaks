package mod.adrenix.nostalgic.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Network utility methods for the client.
 * This class must not be used by the server.
 */

public abstract class NetClientUtil
{
    /* Minecraft Instance */

    private static final Minecraft minecraft = Minecraft.getInstance();

    /**
     * Check if a certain player has operator permissions.
     * @param player The player to check.
     * @return Whether the player is an operator.
     */
    public static boolean isPlayerOp(Player player)
    {
        if (minecraft.hasSingleplayerServer())
            return true;
        return player.hasPermissions(2);
    }

    /**
     * Use this method to bypass the need to provide a specific player instance.
     * If a certain player is to be checked {@link #isPlayerOp(Player)}.
     * @return Whether the player is an operator.
     */
    public static boolean isPlayerOp()
    {
        if (minecraft.player == null)
            return true;
        else
            return isPlayerOp(minecraft.player);
    }

    /**
     * Checks if there is a connection made with a world. If not, then we are at the game's menu.
     * @return Whether there is a live connection with a world session.
     */
    public static boolean isConnected()
    {
        return minecraft.getConnection() != null && minecraft.getConnection().getConnection().isConnected();
    }

    /**
     * Checks if the loaded world is singleplayer.
     * @return Whether the current session is singleplayer.
     */
    public static boolean isSingleplayer()
    {
        return minecraft.getSingleplayerServer() != null && !minecraft.getSingleplayerServer().isPublished();
    }

    /**
     * Checks if the loaded world is current a LAN session.
     * @return Whether the current session is an integrated server.
     */
    public static boolean isLocalHost()
    {
        return minecraft.getSingleplayerServer() != null && minecraft.getSingleplayerServer().isPublished();
    }

    /**
     * Gets the integrated server if the client is a local host.
     * @return The client's integrated server (or null).
     */
    @Nullable
    public static IntegratedServer getIntegratedServer()
    {
        return isLocalHost() ? minecraft.getSingleplayerServer() : null;
    }

    /**
     * Checks if the current world is not singleplayer and that a connection is established
     * with a server.
     *
     * Will return true if the client is hosting a LAN session.
     *
     * @return Whether the current session is in multiplayer or a LAN session.
     */
    public static boolean isMultiplayer()
    {
        if (isLocalHost())
            return true;
        return !isSingleplayer() && minecraft.getConnection() != null;
    }
}
