package mod.adrenix.nostalgic.fabric.register;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.event.ArchClientEvents;
import net.fabricmc.api.ClientModInitializer;

public class ClientInitializer implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        // Initialize mod
        NostalgicTweaks.initClient();

        // Register Architectury events
        ArchClientEvents.register();
    }
}
