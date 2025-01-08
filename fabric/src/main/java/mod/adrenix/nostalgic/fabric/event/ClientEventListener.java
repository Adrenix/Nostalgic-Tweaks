package mod.adrenix.nostalgic.fabric.event;

import mod.adrenix.nostalgic.helper.gameplay.InteractionHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;

public abstract class ClientEventListener
{
    /**
     * Registers event listeners that tap into the Fabric API on the client.
     */
    public static void register()
    {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            Item itemInHand = player.getItemInHand(hand).getItem();

            if (InteractionHelper.shouldNotUseItem(itemInHand))
                return InteractionResult.FAIL;

            return InteractionResult.PASS;
        });
    }
}
