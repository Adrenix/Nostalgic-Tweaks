package mod.adrenix.nostalgic.mixin.common.server;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerListMixin
{
    /* Helpers */

    private static void add(ServerPlayerEntity player, int slot, Block block)
    {
        player.getInventory().insertStack(slot, block.asItem().getDefaultStack());
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
    private void NT$onPlayerJoinWorld(ClientConnection netManager, ServerPlayerEntity player, CallbackInfo callback)
    {
        TweakVersion.Hotbar hotbar = ModConfig.Candy.getHotbar();

        boolean isCreative = player.interactionManager.getGameMode() == GameMode.CREATIVE;
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
