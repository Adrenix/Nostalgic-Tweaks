package mod.adrenix.nostalgic.helper.gameplay.stamina;

import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

/**
 * This utility is used by both the client and server.
 */
public abstract class StaminaHelper
{
    /**
     * This map is used by the server to cache all players and their stamina data. When a player disconnects, their data
     * is removed from the cache map. No stamina data is saved on disk since this system is very basic and does not
     * require any data to be saved on disk.
     */
    private static final HashMap<String, StaminaData> PLAYER_DATA = new HashMap<>();

    /**
     * Initialize the helper utility for when a stamina tweak changes its state.
     */
    public static void init()
    {
        GameplayTweak.STAMINA_SPRINT.whenChanged(StaminaHelper::reset);
    }

    /**
     * Get the stamina data associated with the given player.
     *
     * @param player The {@link Player} instance.
     * @return The {@link StaminaData} instance attached to the player.
     */
    public static StaminaData get(Player player)
    {
        if (!PLAYER_DATA.containsKey(player.getStringUUID()))
            PLAYER_DATA.put(player.getStringUUID(), new StaminaData());

        return PLAYER_DATA.get(player.getStringUUID());
    }

    /**
     * Remove the stamina data associated with the given player.
     *
     * @param player The {@link Player} instance.
     */
    public static void remove(Player player)
    {
        PLAYER_DATA.remove(player.getStringUUID());
    }

    /**
     * Run on-tick instructions for the given player's stamina data.
     *
     * @param player The {@link Player} instance.
     */
    public static void tick(Player player)
    {
        get(player).tick(player);
    }

    /**
     * Check if a player is actively using their stamina.
     *
     * @param player The {@link Player} instance to check.
     * @return Whether the player is using stamina or is exhausted.
     */
    public static boolean isActiveFor(Player player)
    {
        StaminaData data = get(player);

        return data.getStaminaLevel() < StaminaData.MAX_STAMINA_LEVEL || data.isExhausted() || player.isSprinting();
    }

    /**
     * Clears the stamina data cache map. This should be invoked after a singleplayer world is closed.
     */
    public static void reset()
    {
        PLAYER_DATA.clear();
    }
}
