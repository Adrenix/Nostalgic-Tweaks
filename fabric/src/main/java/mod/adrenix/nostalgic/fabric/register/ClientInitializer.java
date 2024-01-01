package mod.adrenix.nostalgic.fabric.register;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.fabricmc.api.ClientModInitializer;

public class ClientInitializer implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        // Initialize mod
        NostalgicTweaks.initClient();

        // TODO: Register Architectury events
    }
}
