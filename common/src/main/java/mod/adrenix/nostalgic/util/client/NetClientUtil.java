package mod.adrenix.nostalgic.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

/**
 * Network utility methods for the client.
 * This class must not be used by the server.
 */

public abstract class NetClientUtil
{
    /**
     * Check if a certain player has operator permissions.
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
     * Use this method to bypass the need to provide a specific player instance.
     * If a certain player is to be checked {@link #isPlayerOp(Player)}.
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
     * Checks if the loaded world is singleplayer.
     * @return Whether the current session is singleplayer.
     */
    public static boolean isSingleplayer() { return Minecraft.getInstance().hasSingleplayerServer(); }

    /**
     * Checks if the loaded world is an integrated server.
     * @return Whether the current session is an integrated server.
     */
    public static boolean isIntegratedServer() { return Minecraft.getInstance().isLocalServer(); }

    /**
     * Checks if the current world is not singleplayer, not an integrated session, and a connection is established
     * with a server.
     *
     * @return Whether the current session is in multiplayer.
     */
    public static boolean isMultiplayer()
    {
        return !isSingleplayer() && !isIntegratedServer() && Minecraft.getInstance().getConnection() != null;
    }
}
