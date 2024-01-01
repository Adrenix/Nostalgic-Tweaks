package mod.adrenix.nostalgic.fabric.register;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ServerInitializer implements DedicatedServerModInitializer
{
    @Override
    public void onInitializeServer()
    {
        // Initialize mod
        NostalgicTweaks.initServer();
    }
}
