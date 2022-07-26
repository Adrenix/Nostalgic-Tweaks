package mod.adrenix.nostalgic.forge;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.forge.register.NostalgicRegistry;
import mod.adrenix.nostalgic.forge.init.NostalgicSoundInit;
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

        // Register sounds
        NostalgicSoundInit.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        NostalgicSoundInit.init();

        // Let the server know this mod isn't going to do anything in this version of the mod, and setup config on client.
        if (FMLLoader.getDist().isDedicatedServer())
            NostalgicTweaks.initServer(NostalgicTweaks.Environment.FORGE);
        else
            FMLJavaModLoadingContext.get().getModEventBus().addListener(NostalgicRegistry::setup);
    }
}
