package mod.adrenix.nostalgic.fabric.event.common;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import mod.adrenix.nostalgic.util.server.PlayerServerUtil;
import net.minecraft.world.InteractionResult;

/**
 * Fabric gameplay related event instructions and registration.
 * Registration is invoked by client/server event handlers.
 */

public abstract class GameplayEvents
{
    /**
     * Register gameplay related Fabric events.
     */
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
