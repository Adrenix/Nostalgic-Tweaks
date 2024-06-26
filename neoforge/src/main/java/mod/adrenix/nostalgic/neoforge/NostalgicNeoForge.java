package mod.adrenix.nostalgic.neoforge;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.ModTracker;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

@Mod(NostalgicTweaks.MOD_ID)
public class NostalgicNeoForge
{
    /**
     * Setup for the mod for both the client and server.
     */
    public NostalgicNeoForge()
    {
        // Mod tracking
        ModTracker.init(ModList.get()::isLoaded);

        // Initialize mod
        NostalgicTweaks.initialize();
    }
}
