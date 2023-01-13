package mod.adrenix.nostalgic.fabric.event;

import mod.adrenix.nostalgic.client.config.gui.toast.NostalgicToast;
import mod.adrenix.nostalgic.fabric.api.event.NostalgicHudEvent;
import mod.adrenix.nostalgic.fabric.event.client.CandyEvents;
import mod.adrenix.nostalgic.fabric.event.client.ClientNetworkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * Registers client-side Fabric events.
 */

public abstract class ClientEvents
{
    /**
     * Invokes the registration methods of various event group helper classes.
     * Extra instructions may be included in these helper classes.
     */
    public static void register()
    {
        CandyEvents.register();
        ClientNetworkEvents.register();
        NostalgicHudEvent.register();
        ClientTickEvents.END_CLIENT_TICK.register(client -> NostalgicToast.tick());
    }
}
