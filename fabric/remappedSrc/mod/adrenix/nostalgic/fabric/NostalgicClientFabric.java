package mod.adrenix.nostalgic.fabric;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.fabric.config.KeyRegistry;
import mod.adrenix.nostalgic.fabric.event.ClientEventHandler;
import net.fabricmc.api.ClientModInitializer;

public class NostalgicClientFabric implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        // Initialize mod
        NostalgicTweaks.initClient(NostalgicTweaks.Environment.FABRIC);

        // Subscribe configuration key
        KeyRegistry.registerConfigurationKey();

        // Register client
        ClientEventHandler.register();
    }
}
