package mod.adrenix.nostalgic.fabric;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.fabric.event.ServerEventHandler;
import net.fabricmc.api.DedicatedServerModInitializer;

/**
 * This class implements Fabric's dedicated server mod initializer interface.
 * Mod initialization and required registrations are performed here.
 */

public class NostalgicServerFabric implements DedicatedServerModInitializer
{
    /**
     * Instructions for mod initialization and server event registration.
     */
    @Override
    public void onInitializeServer()
    {
        // Initialize mod
        NostalgicTweaks.initServer(NostalgicTweaks.Environment.FABRIC);

        // Register server
        ServerEventHandler.register();
    }
}
