package mod.adrenix.nostalgic.init;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import mod.adrenix.nostalgic.network.PacketRegistry;

public abstract class NostalgicInitializer
{
    /**
     * Registers various elements of the mod for both the client and server.
     */
    public static void register()
    {
        PacketRegistry.register();
        CommonInitializer.register();

        EnvExecutor.runInEnv(Env.CLIENT, () -> ClientInitializer::register);
    }
}
