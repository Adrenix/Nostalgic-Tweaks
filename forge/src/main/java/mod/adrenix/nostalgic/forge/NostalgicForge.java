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

/**
 * The mod's main class for Forge. This is accessed by both the client and server.
 * Any immediate registration or field definitions are done here.
 */

@Mod(NostalgicTweaks.MOD_ID)
public class NostalgicForge
{
    /* Network Verification */

    /**
     * Checks if the mod protocol sent from the server matches what is on the client.
     * @param remoteVersion The sent mod protocol version.
     * @param isFromServer Not used, but is intended to accept any version from a server.
     * @return Whether the mod's network protocol versions matched.
     */
    private static boolean isProtocolMatched(String remoteVersion, boolean isFromServer)
    {
        return remoteVersion.equals(NostalgicTweaks.PROTOCOL);
    }

    /**
     * Creates a new display test instance that checks the mod's network protocol version.
     * @return A new display test instance.
     */
    private static IExtensionPoint.DisplayTest getDisplayTest()
    {
        return new IExtensionPoint.DisplayTest(NostalgicTweaks::getProtocol, NostalgicForge::isProtocolMatched);
    }

    /* Constructor */

    /**
     * Setup for the mod for both the client and server.
     * Important registration and main class fields are defined here.
     */
    public NostalgicForge()
    {
        // Mod event bus
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // Let the connection be rejected by the server if there is a protocol mismatch
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, NostalgicForge::getDisplayTest);

        // Common setup
        bus.addListener(CommonSetup::init);

        // Register sounds
        NostalgicSoundInit.SOUNDS.register(bus);
        NostalgicSoundInit.init();

        // Initialization
        if (FMLLoader.getDist().isDedicatedServer())
            NostalgicTweaks.initServer();
        else
            bus.addListener(ClientRegistry::init);
    }
}
