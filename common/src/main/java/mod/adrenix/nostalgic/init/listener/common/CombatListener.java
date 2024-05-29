package mod.adrenix.nostalgic.init.listener.common;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.common.InteractionEvent;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public abstract class CombatListener
{
    /**
     * Registers common combat events.
     */
    public static void register()
    {
        InteractionEvent.RIGHT_CLICK_ITEM.register(CombatListener::onUseBowItem);
    }

    /**
     * Prevents bows from charging and instead immediately fires an arrow at a predefined velocity.
     *
     * @param player The {@link Player} firing the bow item.
     * @param hand   The {@link InteractionHand} the bow is in.
     * @return A {@link CompoundEventResult} instance.
     */
    private static CompoundEventResult<ItemStack> onUseBowItem(Player player, InteractionHand hand)
    {
        if (!GameplayTweak.INSTANT_BOW.get())
            return CompoundEventResult.pass();

        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getItem().equals(Items.BOW) && itemStack.getItem() instanceof BowItem bow)
        {
            int timeCharged = 72000 - (int) (((float) GameplayTweak.ARROW_SPEED.get() / 100.0F) * 20.0F);

            bow.releaseUsing(itemStack, player.level(), player, timeCharged);

            return CompoundEventResult.interruptTrue(itemStack);
        }

        return CompoundEventResult.pass();
    }
}
