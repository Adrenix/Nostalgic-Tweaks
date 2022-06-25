package mod.adrenix.nostalgic.fabric;

import mod.adrenix.nostalgic.fabric.event.FabricCommonEventHandler;
import net.fabricmc.api.ModInitializer;

public class NostalgicCommonFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        // Register common
        FabricCommonEventHandler.register();
    }
}
