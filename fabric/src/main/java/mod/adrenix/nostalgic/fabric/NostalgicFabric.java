package mod.adrenix.nostalgic.fabric;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.fabric.event.ClientEventListener;
import mod.adrenix.nostalgic.fabric.network.Networking;
import mod.adrenix.nostalgic.util.ModTracker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class NostalgicFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        // Mod tracking
        ModTracker.init(FabricLoader.getInstance()::isModLoaded);

        // Initialize mod
        NostalgicTweaks.initialize();

        // Register networking
        Networking.register();

        // Register events
        EnvExecutor.runInEnv(Env.CLIENT, () -> ClientEventListener::register);
    }
}
