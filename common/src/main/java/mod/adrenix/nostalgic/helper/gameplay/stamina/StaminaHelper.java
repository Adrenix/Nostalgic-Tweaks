package mod.adrenix.nostalgic.helper.gameplay.stamina;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.packet.stamina.ClientboundStaminaSync;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * This utility is used by both the client and server.
 */
public abstract class StaminaHelper
{
    /**
     * This map is used by the server to cache all players and their stamina. When a player disconnects, their data is
     * removed from the cache map. Stamina is saved on disk using the player's NBT.
     */
    private static final HashMap<UUID, PlayerStamina> PLAYER_DATA = new HashMap<>();

    /**
     * Initialize the helper utility for when a stamina tweak changes its state.
     */
    public static void init()
    {
        GameplayTweak.STAMINA_DURATION.whenSaved(StaminaHelper::reset);
        GameplayTweak.STAMINA_RECHARGE.whenSaved(StaminaHelper::reset);
        GameplayTweak.STAMINA_COOLDOWN.whenSaved(StaminaHelper::reset);
    }

    /**
     * Reset all players' stamina data and send stamina synchronization packets to all connected players.
     */
    public static void reset()
    {
        MinecraftServer server = NostalgicTweaks.getIntegratedOrDedicatedServer();

        if (server == null)
            return;

        PLAYER_DATA.values().forEach(PlayerStamina::reset);

        server.getPlayerList()
            .getPlayers()
            .forEach(player -> PacketUtil.sendToPlayer(player, ClientboundStaminaSync.create(player)));
    }

    /**
     * Get the stamina data associated with the given player.
     *
     * @param player The {@link Player} instance.
     * @return The {@link PlayerStamina} instance attached to the player.
     */
    public static PlayerStamina get(Player player)
    {
        return PLAYER_DATA.computeIfAbsent(player.getUUID(), uuid -> PlayerStamina.create(player));
    }

    /**
     * Find stamina data associated with the given UUID if it exists.
     *
     * @param uuid The {@link UUID} to find stamina data for.
     * @return An {@link Optional} that maybe contains {@link PlayerStamina}.
     */
    public static Optional<PlayerStamina> find(UUID uuid)
    {
        return Optional.ofNullable(PLAYER_DATA.get(uuid));
    }

    /**
     * Remove runtime stamina data associated with the given player.
     *
     * @param player The {@link Player} instance.
     */
    public static void remove(Player player)
    {
        PLAYER_DATA.remove(player.getUUID());
    }
}
