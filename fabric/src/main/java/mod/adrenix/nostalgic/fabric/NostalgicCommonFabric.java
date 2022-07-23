package mod.adrenix.nostalgic.fabric;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.fabric.event.CommonEventHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class NostalgicCommonFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        // Development Environment
        NostalgicTweaks.setDevelopmentEnvironment(FabricLoader.getInstance().isDevelopmentEnvironment());

        // Register common
        CommonEventHandler.register();
    }
}
