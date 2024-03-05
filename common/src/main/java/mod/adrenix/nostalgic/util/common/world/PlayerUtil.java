package mod.adrenix.nostalgic.util.common.world;

import net.minecraft.world.entity.player.Player;

public abstract class PlayerUtil
{
    /**
     * Check if the given player is in creative mode or spectating.
     *
     * @param player The {@link Player} to check.
     * @return Whether the player is in creative or spectating.
     */
    public static boolean isCreativeOrSpectator(Player player)
    {
        return player.isCreative() || player.isSpectator();
    }
}
