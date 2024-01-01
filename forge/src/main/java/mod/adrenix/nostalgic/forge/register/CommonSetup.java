package mod.adrenix.nostalgic.forge.register;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.ArchCommonEvents;
import mod.adrenix.nostalgic.util.ModTracker;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Handler class that subscribes common setup instructions to Forge's event bus. This class is used by both the client
 * and server.
 */
@Mod.EventBusSubscriber(
    modid = NostalgicTweaks.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE
)
public abstract class CommonSetup
{
    /**
     * Initializes mod tracking and the network channel so that network protocol matching is enforced as well as
     * register needed events.
     *
     * @param event A FML common setup event instance.
     */
    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event)
    {
        // Mod tracking
        ModTracker.init(ModList.get()::isLoaded);

        // Register Architectury events
        ArchCommonEvents.register();
    }
}
