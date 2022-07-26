package mod.adrenix.nostalgic.mixin.common.server;

import mod.adrenix.nostalgic.client.config.ModConfig;
import mod.adrenix.nostalgic.client.config.tweak.TweakVersion;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin
{
    /* Helpers */

    private static void add(ServerPlayer player, int slot, Block block)
    {
        player.getInventory().add(slot, block.asItem().getDefaultInstance());
    }

    /* Injections */

    /**
     * Adds the default blocks from classic or beta.
     * Controlled by the old classic hotbar tweak.
     */
    @Inject
    (
        method = "placeNewPlayer",
        at = @At
        (
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;initInventoryMenu()V"
        )
    )
    private void NT$onPlayerJoinWorld(Connection netManager, ServerPlayer player, CallbackInfo callback)
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
}
