package mod.adrenix.nostalgic.fabric;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.fabric.config.FabricRegistry;
import mod.adrenix.nostalgic.fabric.event.FabricEventHandler;
import mod.adrenix.nostalgic.fabric.init.FabricSoundInit;
import net.fabricmc.api.ClientModInitializer;

public class NostalgicClientFabric implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        // Initialize mod
        NostalgicTweaks.initClient(NostalgicTweaks.Environment.FABRIC);

        // Subscribe configuration key
        FabricRegistry.registerConfigurationKey();

        // Register fabric sounds & events
        FabricEventHandler.register();
        FabricSoundInit.register();
    }
}
