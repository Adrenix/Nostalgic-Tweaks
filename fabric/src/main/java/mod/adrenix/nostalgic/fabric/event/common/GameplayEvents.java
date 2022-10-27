package mod.adrenix.nostalgic.fabric.event.common;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import mod.adrenix.nostalgic.util.server.PlayerServerUtil;
import net.minecraft.world.InteractionResult;

public abstract class GameplayEvents
{
    public static void register() { onMilkSquid(); }

    /**
     * Brings back the old bug that allows players to milk squids.
     */
    public static void onMilkSquid()
    {
        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
            if (PlayerServerUtil.milkSquid(player, hand, entity).equals(InteractionResult.SUCCESS))
                return EventResult.interruptTrue();
            return EventResult.pass();
        });
    }
}
