package mod.adrenix.nostalgic.util.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.integrated.IntegratedServer;
import org.jetbrains.annotations.Nullable;

/**
 * Network utility methods for the client.
 * This class must not be used by the server.
 */

public abstract class NetClientUtil
{
    /* Minecraft Instance */

    private static final MinecraftClient minecraft = MinecraftClient.getInstance();

    /**
     * Check if a certain player has operator permissions.
     * @param player The player to check.
     * @return Whether the player is an operator.
     */
    public static boolean isPlayerOp(PlayerEntity player)
    {
        if (minecraft.isIntegratedServerRunning())
            return true;
        return player.hasPermissionLevel(2);
    }

    /**
     * Use this method to bypass the need to provide a specific player instance.
     * If a certain player is to be checked {@link #isPlayerOp(PlayerEntity)}.
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
        return minecraft.getNetworkHandler() != null && minecraft.getNetworkHandler().getConnection().isOpen();
    }

    /**
     * Checks if the loaded world is singleplayer.
     * @return Whether the current session is singleplayer.
     */
    public static boolean isSingleplayer()
    {
        return minecraft.getServer() != null && !minecraft.getServer().isRemote();
    }

    /**
     * Checks if the loaded world is current a LAN session.
     * @return Whether the current session is an integrated server.
     */
    public static boolean isLocalHost()
    {
        return minecraft.getServer() != null && minecraft.getServer().isRemote();
    }

    /**
     * Gets the integrated server if the client is a local host.
     * @return The client's integrated server (or null).
     */
    @Nullable
    public static IntegratedServer getIntegratedServer()
    {
        return isLocalHost() ? minecraft.getServer() : null;
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
        return !isSingleplayer() && minecraft.getNetworkHandler() != null;
    }
}
