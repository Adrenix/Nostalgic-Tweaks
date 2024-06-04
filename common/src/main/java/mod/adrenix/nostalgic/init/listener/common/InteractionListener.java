package mod.adrenix.nostalgic.init.listener.common;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public abstract class InteractionListener
{
    /**
     * Registers common interaction events.
     */
    public static void register()
    {
        InteractionEvent.RIGHT_CLICK_BLOCK.register(InteractionListener::onRightClickBlock);
        InteractionEvent.INTERACT_ENTITY.register(InteractionListener::onInteractEntity);
    }

    /**
     * Handles right-click block interaction for tweaks that control this behavior.
     *
     * @param player   The {@link Player} instance.
     * @param hand     The {@link InteractionHand} that right-clicked the block.
     * @param blockPos The {@link BlockPos} of the right-click.
     * @param face     The {@link Direction} face that was right-clicked.
     * @return The {@link EventResult} instance.
     */
    private static EventResult onRightClickBlock(Player player, InteractionHand hand, BlockPos blockPos, Direction face)
    {
        BlockState blockState = player.level().getBlockState(blockPos);

        if (GameplayTweak.DISABLE_ANVIL.get() && blockState.is(BlockTags.ANVIL))
            return EventResult.interruptTrue();

        if (GameplayTweak.DISABLE_ENCHANT_TABLE.get() && blockState.is(Blocks.ENCHANTING_TABLE))
            return EventResult.interruptTrue();

        return EventResult.pass();
    }

    /**
     * Handles entity interaction for tweaks that control this behavior.
     *
     * @param player The {@link Player} instance.
     * @param entity The {@link Entity} that player is interacting with.
     * @param hand   The {@link InteractionHand} that is interacting with the entity.
     * @return An {@link EventResult} instance.
     */
    private static EventResult onInteractEntity(Player player, Entity entity, InteractionHand hand)
    {
        ItemStack itemInHand = player.getItemInHand(hand);

        if (GameplayTweak.OLD_SQUID_MILKING.get() && itemInHand.is(Items.BUCKET) && entity instanceof Squid)
        {
            player.setItemInHand(hand, ItemUtils.createFilledResult(itemInHand, player, Items.MILK_BUCKET.getDefaultInstance()));
            return EventResult.interruptTrue();
        }

        return EventResult.pass();
    }

}
