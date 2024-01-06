package mod.adrenix.nostalgic.util.server.world;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.tweak.enums.Hotbar;
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

public abstract class ServerPlayerUtil
{
    /**
     * Utility method for {@link ServerPlayerUtil#setCreativeHotbar(ServerPlayer)}.
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
    public static void setCreativeHotbar(ServerPlayer player)
    {
        Hotbar hotbar = CandyTweak.OLD_CREATIVE_HOTBAR.get();

        boolean isCreative = player.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
        boolean isNostalgic = hotbar != Hotbar.MODERN;

        if (player.getInventory().isEmpty() && isCreative && isNostalgic)
        {
            setBlockInSlot(player, 0, Blocks.STONE);

            if (hotbar == Hotbar.BETA)
            {
                setBlockInSlot(player, 1, Blocks.COBBLESTONE);
                setBlockInSlot(player, 2, Blocks.BRICKS);
                setBlockInSlot(player, 3, Blocks.DIRT);
                setBlockInSlot(player, 4, Blocks.OAK_PLANKS);
                setBlockInSlot(player, 5, Blocks.OAK_LOG);
                setBlockInSlot(player, 6, Blocks.OAK_LEAVES);
                setBlockInSlot(player, 7, Blocks.TORCH);
                setBlockInSlot(player, 8, Blocks.SMOOTH_STONE_SLAB);
            }
            else
            {
                setBlockInSlot(player, 1, Blocks.DIRT);
                setBlockInSlot(player, 2, Blocks.OAK_PLANKS);
                setBlockInSlot(player, 3, Blocks.COBBLESTONE);
                setBlockInSlot(player, 4, Blocks.SAND);
                setBlockInSlot(player, 5, Blocks.GRAVEL);
                setBlockInSlot(player, 6, Blocks.OAK_LOG);
                setBlockInSlot(player, 7, Blocks.OAK_LEAVES);
                setBlockInSlot(player, 8, Blocks.RED_MUSHROOM);
            }
        }
    }

    /**
     * Allows squids to be milked by players with an empty bucket in their hand.
     *
     * @param player The {@link Player} instance.
     * @param hand   The current {@link InteractionHand}.
     * @param entity The {@link Entity} that player is interacting with.
     * @return A {@link InteractionResult} based on the entity interaction result.
     */
    public static InteractionResult milkSquid(Player player, InteractionHand hand, Entity entity)
    {
        ItemStack holding = player.getItemInHand(hand);

        if (GameplayTweak.OLD_SQUID_MILKING.get() && holding.is(Items.BUCKET) && entity instanceof Squid)
        {
            player.setItemInHand(hand, ItemUtils.createFilledResult(holding, player, Items.MILK_BUCKET.getDefaultInstance()));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
