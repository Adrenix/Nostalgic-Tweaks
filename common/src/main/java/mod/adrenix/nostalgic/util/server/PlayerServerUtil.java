package mod.adrenix.nostalgic.util.server;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
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

/**
 * This utility is used by the server.
 * For safety, keep vanilla client-only code out.
 */

public abstract class PlayerServerUtil
{
    private static void add(ServerPlayer player, int slot, Block block)
    {
        player.getInventory().add(slot, block.asItem().getDefaultInstance());
    }

    public static void setCreativeHotbar(ServerPlayer player)
    {
        TweakVersion.Hotbar hotbar = ModConfig.Candy.getHotbar();

        boolean isCreative = player.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
        boolean isOldHotbar = hotbar != TweakVersion.Hotbar.MODERN;

        if (player.getInventory().isEmpty() && isCreative && isOldHotbar)
        {
            add(player, 0, Blocks.STONE);

            if (hotbar == TweakVersion.Hotbar.BETA)
            {
                add(player, 1, Blocks.COBBLESTONE);
                add(player, 2, Blocks.BRICKS);
                add(player, 3, Blocks.DIRT);
                add(player, 4, Blocks.OAK_PLANKS);
                add(player, 5, Blocks.OAK_LOG);
                add(player, 6, Blocks.OAK_LEAVES);
                add(player, 7, Blocks.TORCH);
                add(player, 8, Blocks.SMOOTH_STONE_SLAB);
            }
            else
            {
                add(player, 1, Blocks.DIRT);
                add(player, 2, Blocks.OAK_PLANKS);
                add(player, 3, Blocks.COBBLESTONE);
                add(player, 4, Blocks.SAND);
                add(player, 5, Blocks.GRAVEL);
                add(player, 6, Blocks.OAK_LOG);
                add(player, 7, Blocks.OAK_LEAVES);
                add(player, 8, Blocks.RED_MUSHROOM);
            }
        }
    }

    public static InteractionResult milkSquid(Player player, InteractionHand hand, Entity entityToInteractOn)
    {
        ItemStack holding = player.getItemInHand(hand);

        if (ModConfig.Gameplay.oldSquidMilk() && holding.is(Items.BUCKET) && entityToInteractOn instanceof Squid)
        {
            player.setItemInHand(hand, ItemUtils.createFilledResult(holding, player, Items.MILK_BUCKET.getDefaultInstance()));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
