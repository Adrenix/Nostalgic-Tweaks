package mod.adrenix.nostalgic.init.listener.common;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.world.PlayerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class InteractionListener
{
    /**
     * Registers common interaction events.
     */
    public static void register()
    {
        InteractionEvent.RIGHT_CLICK_ITEM.register(InteractionListener::onRightClickItem);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(InteractionListener::onRightClickBlock);
        InteractionEvent.LEFT_CLICK_BLOCK.register(InteractionListener::onLeftClickBlock);
        InteractionEvent.INTERACT_ENTITY.register(InteractionListener::onInteractEntity);
    }

    /**
     * Handles right-click item interaction for tweaks that control this behavior.
     *
     * @param player The {@link Player} instance.
     * @param hand   The {@link InteractionHand} that right-clicked the item.
     * @return A {@link CompoundEventResult} instance.
     */
    private static CompoundEventResult<ItemStack> onRightClickItem(Player player, InteractionHand hand)
    {
        ItemStack itemStackInHand = player.getItemInHand(hand);
        Item itemInHand = itemStackInHand.getItem();

        if (GameplayTweak.INSTANT_BOW.get() && itemInHand.equals(Items.BOW) && itemInHand instanceof BowItem bow)
        {
            int timeCharged = 72000 - (int) (((float) GameplayTweak.ARROW_SPEED.get() / 100.0F) * 20.0F);

            bow.releaseUsing(itemStackInHand, player.level(), player, timeCharged);

            return CompoundEventResult.interruptTrue(itemStackInHand);
        }

        return CompoundEventResult.pass();
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
     * Handles left-click block interaction for tweaks that control this behavior.
     *
     * @param player   The {@link Player} instance.
     * @param hand     The {@link InteractionHand} that left-clicked the block.
     * @param blockPos The {@link BlockPos} of the left-click.
     * @param face     The {@link Direction} face that was left-clicked.
     * @return The {@link EventResult} instance.
     */
    private static EventResult onLeftClickBlock(Player player, InteractionHand hand, BlockPos blockPos, Direction face)
    {
        if (player.swinging)
            return EventResult.pass();

        Level level = player.getCommandSenderWorld();
        BlockHitResult blockHitResult = new BlockHitResult(Vec3.atCenterOf(blockPos), face, blockPos, false);
        BlockState blockState = level.getBlockState(blockPos);
        Block block = blockState.getBlock();

        if (GameplayTweak.LEFT_CLICK_DOOR.get() && PlayerUtil.isSurvival(player))
        {
            if (block instanceof DoorBlock || block instanceof TrapDoorBlock || block instanceof FenceGateBlock)
                blockState.use(level, player, hand, blockHitResult);
        }

        if (GameplayTweak.LEFT_CLICK_LEVER.get() && PlayerUtil.isSurvival(player))
        {
            if (block instanceof LeverBlock)
                blockState.use(level, player, hand, blockHitResult);
        }

        if (GameplayTweak.LEFT_CLICK_BUTTON.get() && PlayerUtil.isSurvival(player))
        {
            if (block instanceof ButtonBlock)
                blockState.use(level, player, hand, blockHitResult);
        }

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
