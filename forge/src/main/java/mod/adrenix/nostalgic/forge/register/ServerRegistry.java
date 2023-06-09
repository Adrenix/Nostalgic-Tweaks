package mod.adrenix.nostalgic.forge.register;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.server.event.ServerEventHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerLifecycleEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handler class that subscribes mod events to Forge's event bus.
 * This class is focused on server events.
 */

@Mod.EventBusSubscriber(modid = NostalgicTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public abstract class ServerRegistry
{
    /**
     * Instructions for server initialization.
     * @param event A server lifecycle event instance.
     */
    @SubscribeEvent
    public static void init(ServerLifecycleEvent event) { ServerEventHelper.instantiate(event.getServer()); }
}
