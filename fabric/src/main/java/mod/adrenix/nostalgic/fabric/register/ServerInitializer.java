package mod.adrenix.nostalgic.fabric.register;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.server.ArchServerEvents;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ServerInitializer implements DedicatedServerModInitializer
{
    @Override
    public void onInitializeServer()
    {
        // Initialize mod
        NostalgicTweaks.initServer();

        // Register Architectury events
        ArchServerEvents.register();
    }
}
