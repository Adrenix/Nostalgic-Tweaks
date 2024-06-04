package mod.adrenix.nostalgic.init.listener.common;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public abstract class InteractionListener
{
    /**
     * Registers common block interaction events.
     */
    public static void register()
    {
        InteractionEvent.RIGHT_CLICK_BLOCK.register(InteractionListener::onRightClickBlock);
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
}
