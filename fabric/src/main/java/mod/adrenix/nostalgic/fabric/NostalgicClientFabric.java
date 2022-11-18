package mod.adrenix.nostalgic.fabric;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.fabric.config.KeyRegistry;
import mod.adrenix.nostalgic.fabric.event.ClientEventHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * This class implements Fabric's client mod initializer interface.
 * Mod tracking and required registrations are performed here.
 */

public class NostalgicClientFabric implements ClientModInitializer
{
    /**
     * Instructions for mod tracking, mod initialization, mod key mappings, and event registration.
     */
    @Override
    public void onInitializeClient()
    {
        // Mod tracking
        NostalgicTweaks.isSodiumInstalled = FabricLoader.getInstance().getModContainer("sodium").isPresent();

        // Initialize mod
        NostalgicTweaks.initClient(NostalgicTweaks.Environment.FABRIC);

        // Subscribe key mappings
        KeyRegistry.registerKeyMappings();

        // Register client
        ClientEventHandler.register();
    }
}
