package mod.adrenix.nostalgic.fabric.event;

import mod.adrenix.nostalgic.fabric.event.common.FabricConfigEvents;
import mod.adrenix.nostalgic.fabric.init.FabricSoundInit;

/**
 * Registers events that run on both client and server.
 */

public abstract class FabricCommonEventHandler
{
    public static void register()
    {
        FabricConfigEvents.register();
        FabricSoundInit.register();
    }
}
