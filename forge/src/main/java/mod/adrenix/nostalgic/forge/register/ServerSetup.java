package mod.adrenix.nostalgic.forge.register;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.server.ArchServerEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

@Mod.EventBusSubscriber(
    modid = NostalgicTweaks.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.MOD,
    value = Dist.DEDICATED_SERVER
)
public abstract class ServerSetup
{
    /**
     * Instructions for server initialization.
     *
     * @param event A {@link FMLDedicatedServerSetupEvent} event instance.
     */
    @SubscribeEvent
    public static void init(FMLDedicatedServerSetupEvent event)
    {
        // Initialize mod
        NostalgicTweaks.initServer();

        // Register Architectury events
        ArchServerEvents.register();
    }
}
