package mod.adrenix.nostalgic.forge;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.ModTracker;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;

@Mod(NostalgicTweaks.MOD_ID)
public class NostalgicForge
{
    /* Network Verification */

    /**
     * Checks if the mod protocol sent from the server matches what is on the client.
     *
     * @param remoteVersion The sent mod protocol version.
     * @param isFromServer  Not used, but is intended to accept any version from a server.
     * @return Whether the mod's network protocol versions matched.
     */
    private static boolean isProtocolMatched(String remoteVersion, boolean isFromServer)
    {
        return remoteVersion.equals(NostalgicTweaks.PROTOCOL);
    }

    /**
     * Creates a new display test instance that checks the mod's network protocol version.
     *
     * @return A new display test instance.
     */
    private static IExtensionPoint.DisplayTest getDisplayTest()
    {
        return new IExtensionPoint.DisplayTest(NostalgicTweaks::getProtocol, NostalgicForge::isProtocolMatched);
    }

    /* Mod Constructor */

    /**
     * Setup for the mod for both the client and server. Important registration and main class fields are defined here.
     */
    public NostalgicForge()
    {
        // Let the connection be rejected by the server if there is a protocol mismatch
        ModLoadingContext.get()
            .registerExtensionPoint(IExtensionPoint.DisplayTest.class, NostalgicForge::getDisplayTest);

        // Mod tracking
        ModTracker.init(ModList.get()::isLoaded);

        // Initialize mod
        NostalgicTweaks.initialize();
    }
}
