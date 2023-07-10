package mod.adrenix.nostalgic.fabric;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.fabric.event.CommonEvents;
import mod.adrenix.nostalgic.util.ModTracker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

/**
 * This class implements Fabric's common mod initializer interface.
 * Development environment setup and common event registrations are handled here.
 */

public class NostalgicCommonFabric implements ModInitializer
{
    /**
     * Defines a resource location for Fabric protocol verification.
     * This is separated from the Architectury network handler since this is used to verify network protocol on Fabric.
     */
    public static final ResourceLocation VERIFY_PROTOCOL = new ResourceLocation(NostalgicTweaks.MOD_ID, "protocol");

    /**
     * Instructions for mod initialization, mod development environment, and common event registration.
     */
    @Override
    public void onInitialize()
    {
        // Mod tracking
        ModTracker.init(FabricLoader.getInstance()::isModLoaded);

        // Register common
        CommonEvents.register();
    }
}
