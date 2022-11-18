package mod.adrenix.nostalgic.fabric;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.fabric.event.CommonEventHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * This class implements Fabric's common mod initializer interface.
 * Development environment setup and common event registrations are handled here.
 */

public class NostalgicCommonFabric implements ModInitializer
{
    /**
     * Instructions for mod initialization, mod development environment, and common event registration.
     */
    @Override
    public void onInitialize()
    {
        // Development Environment
        NostalgicTweaks.setDevelopmentEnvironment(FabricLoader.getInstance().isDevelopmentEnvironment());

        // Register common
        CommonEventHandler.register();
    }
}
