package mod.adrenix.nostalgic.fabric.event;

import mod.adrenix.nostalgic.fabric.event.common.ConfigEvents;
import mod.adrenix.nostalgic.fabric.event.common.GameplayEvents;
import mod.adrenix.nostalgic.fabric.init.NostalgicSoundInit;

/**
 * Registers events that run on both client and server.
 */

public abstract class CommonEventHandler
{
    public static void register()
    {
        ConfigEvents.register();
        GameplayEvents.register();
        NostalgicSoundInit.register();
    }
}
