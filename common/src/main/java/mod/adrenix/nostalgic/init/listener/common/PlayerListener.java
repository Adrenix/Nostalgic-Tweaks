package mod.adrenix.nostalgic.init.listener.common;

import dev.architectury.event.events.common.PlayerEvent;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.packet.ClientboundHandshake;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.Hotbar;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public abstract class PlayerListener
{
    /**
     * Registers common server player events.
     */
    public static void register()
    {
        PlayerEvent.PLAYER_JOIN.register(PlayerListener::onPlayerJoin);
    }

    /**
     * This method provides instructions for the mod to perform after a player connects to the server level.
     *
     * @param player A {@link ServerPlayer} instance.
     */
    private static void onPlayerJoin(ServerPlayer player)
    {
        String loader = NostalgicTweaks.getLoader();
        String tiny = NostalgicTweaks.getTinyVersion();
        String beta = NostalgicTweaks.getBetaVersion();
        String version = beta.isEmpty() ? tiny : tiny + "-" + beta;
        String protocol = NostalgicTweaks.getProtocol();

        NostalgicTweaks.NETWORK.sendToPlayer(player, new ClientboundHandshake(loader, version, protocol));

        setCreativeHotbar(player);
    }

    /**
     * Utility method for {@link #setCreativeHotbar(ServerPlayer)}.
     *
     * @param player The {@link ServerPlayer} to modify.
     * @param slot   The slot to place the given block in.
     * @param block  The {@link Block} to place in the given slot.
     */
    private static void setBlockInSlot(ServerPlayer player, int slot, Block block)
    {
        player.getInventory().add(slot, block.asItem().getDefaultInstance());
    }

    /**
     * Changes the player's hotbar in creative mode based on the defined hotbar version.
     *
     * @param player A {@link ServerPlayer} instance.
     */
    private static void setCreativeHotbar(ServerPlayer player)
    {
        Hotbar hotbar = CandyTweak.OLD_CREATIVE_HOTBAR.get();

        boolean isCreative = player.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
        boolean isNostalgic = hotbar != Hotbar.MODERN;

        if (player.getInventory().isEmpty() && isCreative && isNostalgic)
        {
            setBlockInSlot(player, 0, Blocks.STONE);
            setBlockInSlot(player, 1, Blocks.COBBLESTONE);
            setBlockInSlot(player, 2, Blocks.BRICKS);
            setBlockInSlot(player, 3, Blocks.DIRT);
            setBlockInSlot(player, 4, Blocks.OAK_PLANKS);
            setBlockInSlot(player, 5, Blocks.OAK_LOG);
            setBlockInSlot(player, 6, Blocks.OAK_LEAVES);
            setBlockInSlot(player, 8, Blocks.SMOOTH_STONE_SLAB);

            if (hotbar == Hotbar.BETA)
                setBlockInSlot(player, 7, Blocks.TORCH);
            else
                setBlockInSlot(player, 7, Blocks.GLASS);
        }
    }
}
