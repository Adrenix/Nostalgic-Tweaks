package mod.adrenix.nostalgic.forge;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.forge.init.NostalgicSoundInit;
import mod.adrenix.nostalgic.forge.register.ClientRegistry;
import mod.adrenix.nostalgic.forge.register.CommonSetup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkConstants;

@Mod(NostalgicTweaks.MOD_ID)
public class NostalgicForge
{
    public NostalgicForge()
    {
        /* Common */

        // Check if optifine is installed
        NostalgicTweaks.isOptifineInstalled = ModList.get().isLoaded("optifine");

        if (NostalgicTweaks.isOptifineInstalled)
            NostalgicTweaks.LOGGER.warn("Optifine is installed - some tweaks may not work as intended");

        // Let the connection be rejected by the server if there is a protocol mismatch
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () ->
            new IExtensionPoint.DisplayTest(() ->
                NetworkConstants.IGNORESERVERONLY, (a, b) -> true
            )
        );

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(CommonSetup::init);

        // Development Environment
        NostalgicTweaks.setDevelopmentEnvironment(!FMLLoader.isProduction());

        // Register sounds
        NostalgicSoundInit.SOUNDS.register(bus);
        NostalgicSoundInit.init();

        if (FMLLoader.getDist().isDedicatedServer())
        {
            /* Server */

            NostalgicTweaks.initServer(NostalgicTweaks.Environment.FORGE);
        }
        else
        {
            /* Client */

            // Set up the config on client startup
            bus.addListener(ClientRegistry::init);
        }
    }
}
