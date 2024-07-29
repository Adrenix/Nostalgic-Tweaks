package mod.adrenix.nostalgic.forge;

import dev.architectury.platform.forge.EventBuses;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.ModTracker;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NostalgicTweaks.MOD_ID)
public class NostalgicForge
{
    /**
     * Setup for the mod for both the client and server.
     */
    public NostalgicForge()
    {
        // Submit our mod event bus to Architectury
        EventBuses.registerModEventBus(NostalgicTweaks.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Mod tracking
        ModTracker.init(ModList.get()::isLoaded);

        // Initialize mod
        NostalgicTweaks.initialize();
    }
}
