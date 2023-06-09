package mod.adrenix.nostalgic.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Network utility methods for the client.
 * This class must not be used by the server.
 */

public abstract class NetUtil
{
    /* Minecraft Instance */

    /**
     * Gets the current Minecraft instance. This may be <code>null</code> if the network utility is accessed before the
     * main game class is instantiated. This should only be <code>null</code> during registry setup.
     *
     * @return The current {@link Minecraft} instance.
     */
    @Nullable
    @SuppressWarnings("DataFlowIssue") // Prevents NPEs in case this utility is invoked too early
    private static Minecraft getMinecraft() { return Minecraft.getInstance(); }

    /**
     * Check if a certain player has operator permissions.
     * @param player The player to check.
     * @return Whether the player is an operator.
     */
    public static boolean isPlayerOp(Player player)
    {
        if (getMinecraft() == null || getMinecraft().hasSingleplayerServer())
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
        if (getMinecraft() == null || getMinecraft().player == null)
            return true;
        else
            return isPlayerOp(getMinecraft().player);
    }

    /**
     * Checks if there is a connection made with a world. If not, then we are at the game's menu.
     * @return Whether there is a live connection with a world session.
     */
    public static boolean isConnected()
    {
        if (getMinecraft() == null)
            return false;

        return getMinecraft().getConnection() != null && getMinecraft().getConnection().getConnection().isConnected();
    }

    /**
     * Checks if the loaded world is singleplayer.
     * @return Whether the current session is singleplayer.
     */
    public static boolean isSingleplayer()
    {
        if (getMinecraft() == null)
            return false;

        return getMinecraft().getSingleplayerServer() != null && !getMinecraft().getSingleplayerServer().isPublished();
    }

    /**
     * Checks if the loaded world is currently hosting a LAN session.
     * @return Whether the current session is an integrated server.
     */
    public static boolean isLocalHost()
    {
        if (getMinecraft() == null)
            return false;

        return getMinecraft().getSingleplayerServer() != null && getMinecraft().getSingleplayerServer().isPublished();
    }

    /**
     * Gets the integrated server if the client is a local host.
     * @return The client's integrated server (or null).
     */
    @Nullable
    public static IntegratedServer getIntegratedServer()
    {
        return isLocalHost() && getMinecraft() != null ? getMinecraft().getSingleplayerServer() : null;
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
        if (getMinecraft() == null)
            return false;

        if (isLocalHost())
            return true;

        return !isSingleplayer() && getMinecraft().getConnection() != null;
    }
}
