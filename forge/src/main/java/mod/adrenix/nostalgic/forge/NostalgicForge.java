package mod.adrenix.nostalgic.forge;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.forge.init.ForgeSoundInit;
import mod.adrenix.nostalgic.forge.subscribe.ForgeClientRegistry;
import mod.adrenix.nostalgic.forge.subscribe.ForgeCommonSetup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
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

        // Let the connection be rejected by the server if there is a protocol mismatch
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () ->
            new IExtensionPoint.DisplayTest(() ->
                NetworkConstants.IGNORESERVERONLY, (a, b) -> true
            )
        );

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ForgeCommonSetup::init);

        // Register sounds
        ForgeSoundInit.SOUNDS.register(bus);
        ForgeSoundInit.init();

        if (FMLLoader.getDist().isDedicatedServer())
        {
            /* Server */

            NostalgicTweaks.initServer(NostalgicTweaks.Environment.FORGE);
        }
        else
        {
            /* Client */

            // Set up the config on client startup
            bus.addListener(ForgeClientRegistry::init);
        }
    }
}
