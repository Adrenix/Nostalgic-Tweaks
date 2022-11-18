package mod.adrenix.nostalgic.forge;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.forge.init.NostalgicSoundInit;
import mod.adrenix.nostalgic.forge.register.ClientRegistry;
import mod.adrenix.nostalgic.forge.register.CommonSetup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkConstants;

/**
 * The mod's main class for Forge. This is accessed by both the client and server.
 * Any immediate registration or field definitions are done here.
 */

@Mod(NostalgicTweaks.MOD_ID)
public class NostalgicForge
{
    /* Constructor */

    /**
     * Setup for the mod for both the client and server.
     * Important registration and main class fields are defined here.
     */
    public NostalgicForge()
    {
        // Mod Event Bus
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // Let the connection be rejected by the server if there is a protocol mismatch
        ModLoadingContext.get().registerExtensionPoint
        (
            IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest
            (
                () -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true
            )
        );

        // Initialize Common Setup
        bus.addListener(CommonSetup::init);

        // Development Environment
        NostalgicTweaks.setDevelopmentEnvironment(!FMLLoader.isProduction());

        // Register sounds
        NostalgicSoundInit.SOUNDS.register(bus);
        NostalgicSoundInit.init();

        // Perform Sided Initialization
        if (FMLLoader.getDist().isDedicatedServer())
            NostalgicTweaks.initServer(NostalgicTweaks.Environment.FORGE);
        else
            bus.addListener(ClientRegistry::init);
    }
}
