package mod.adrenix.nostalgic.client.event;

import dev.architectury.event.events.common.PlayerEvent;

/**
 * This class contains a registration helper method that will be used by the client initializers in Fabric and Forge.
 * The events used in this class are provided by the Architectury mod.
 */

public abstract class ArchitecturyClientEvents
{
    /**
     * Registers Architectury events. This is used when there is not a Fabric related event to a Forge event. In this
     * instance, using the wrapper events provided by Architectury resolves this problem.
     */
    public static void register()
    {
        PlayerEvent.CHANGE_DIMENSION.register((player, oldDim, newDim) -> ClientEventHelper.onChangeDimension());
    }
}
