package mod.adrenix.nostalgic.fabric.event;

import mod.adrenix.nostalgic.fabric.event.common.ConfigEvents;
import mod.adrenix.nostalgic.fabric.event.common.GameplayEvents;
import mod.adrenix.nostalgic.fabric.init.NostalgicSoundInit;

/**
 * Registers common Fabric events that run on both client and server.
 */

public abstract class CommonEventHandler
{
    /**
     * Invokes the registration methods of various event group helper classes.
     * Extra instructions may be included in these helper classes.
     */
    public static void register()
    {
        ConfigEvents.register();
        GameplayEvents.register();
        NostalgicSoundInit.register();
    }
}
