package mod.adrenix.nostalgic.init.listener.common;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.network.packet.ClientboundHandshake;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.tweak.enums.Hotbar;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public abstract class ServerPlayerListener
{
    /**
     * Registers common server player events.
     */
    public static void register()
    {
        PlayerEvent.PLAYER_JOIN.register(ServerPlayerListener::onPlayerJoin);
        InteractionEvent.INTERACT_ENTITY.register(ServerPlayerListener::milkSquid);
    }

    /**
     * This method provides instructions for the mod to perform after a player connects to the server level. The client
     * handles disconnection instructions. The server will verify network protocol and inform the client that it is
     * connected to a server with Nostalgic Tweaks installed.
     *
     * @param player A {@link ServerPlayer} instance.
     */
    private static void onPlayerJoin(ServerPlayer player)
    {
        String loader = TextUtil.toTitleCase(NostalgicTweaks.getLoader());
        String tiny = NostalgicTweaks.getTinyVersion();
        String beta = NostalgicTweaks.getBetaVersion();
        String version = beta.isEmpty() ? tiny : tiny + "-" + beta;
        String protocol = NostalgicTweaks.getProtocol();

        PacketUtil.sendToPlayer(player, new ClientboundHandshake(loader, version, protocol));
        TweakPool.filter(Tweak::isMultiplayerLike).forEach(tweak -> tweak.sendToPlayer(player));

        setCreativeHotbar(player);
    }

    /**
     * Allows squids to be milked by players with an empty bucket in their hand.
     *
     * @param player The {@link Player} instance.
     * @param entity The {@link Entity} that player is interacting with.
     * @param hand   The current {@link InteractionHand}.
     * @return A {@link InteractionResult} based on the entity interaction result.
     */
    private static EventResult milkSquid(Player player, Entity entity, InteractionHand hand)
    {
        ItemStack holding = player.getItemInHand(hand);

        if (GameplayTweak.OLD_SQUID_MILKING.get() && holding.is(Items.BUCKET) && entity instanceof Squid)
        {
            player.setItemInHand(hand, ItemUtils.createFilledResult(holding, player, Items.MILK_BUCKET.getDefaultInstance()));
            return EventResult.interruptTrue();
        }

        return EventResult.pass();
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
