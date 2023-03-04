package mod.adrenix.nostalgic.fabric;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.event.ArchitecturyClientEvents;
import mod.adrenix.nostalgic.fabric.api.NostalgicFabricApi;
import mod.adrenix.nostalgic.fabric.config.KeyRegistry;
import mod.adrenix.nostalgic.fabric.event.ClientEvents;
import mod.adrenix.nostalgic.util.ModTracker;
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
        ModTracker.APPLE_SKIN.load(FabricLoader.getInstance()::isModLoaded);
        ModTracker.SODIUM.load(FabricLoader.getInstance()::isModLoaded);
        ModTracker.FLYWHEEL.load(FabricLoader.getInstance()::isModLoaded);

        // Initialize mod
        NostalgicTweaks.initClient();

        // Subscribe key mappings
        KeyRegistry.register();

        // Register client events
        ClientEvents.register();

        // Register architectury events
        ArchitecturyClientEvents.register();

        // Register Nostalgic API events
        FabricLoader.getInstance().getEntrypointContainers(NostalgicTweaks.MOD_ID, NostalgicFabricApi.class).forEach
        (
            entrypoint ->
            {
                try
                {
                    entrypoint.getEntrypoint().registerEvents();
                }
                catch (Throwable exception)
                {
                    NostalgicTweaks.LOGGER.error
                    (
                        "Failed to load entrypoint for mod %s\n%s",
                        entrypoint.getProvider().getMetadata().getId(),
                        exception
                    );
                }
            }
        );
    }
}
