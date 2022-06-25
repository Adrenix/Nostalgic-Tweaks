package mod.adrenix.nostalgic.fabric;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.fabric.event.FabricServerEventHandler;
import net.fabricmc.api.DedicatedServerModInitializer;

public class NostalgicServerFabric implements DedicatedServerModInitializer
{
    @Override
    public void onInitializeServer()
    {
        // Let the server know this mod isn't going to do anything in this version of the mod.
        NostalgicTweaks.initServer(NostalgicTweaks.Environment.FABRIC);

        // Register server
        FabricServerEventHandler.register();
    }
}
