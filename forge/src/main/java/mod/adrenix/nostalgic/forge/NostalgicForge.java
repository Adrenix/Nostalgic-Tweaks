package mod.adrenix.nostalgic.forge;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.forge.config.ForgeRegistry;
import mod.adrenix.nostalgic.forge.init.ForgeSoundInit;
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
        // Let everyone know this is a client-side mod for now
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () ->
            new IExtensionPoint.DisplayTest(() ->
                NetworkConstants.IGNORESERVERONLY, (a, b) -> true
            )
        );

        // Let the server know this mod isn't going to do anything in this version of the mod.
        if (FMLLoader.getDist().isDedicatedServer())
            NostalgicTweaks.initServer(NostalgicTweaks.Environment.FORGE);
        else
        {
            // Set up the config on client startup.
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeRegistry::setup);

            // Register sounds
            ForgeSoundInit.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
            ForgeSoundInit.register();
        }
    }
}
