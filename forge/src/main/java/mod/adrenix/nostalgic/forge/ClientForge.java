package mod.adrenix.nostalgic.forge;

import dev.architectury.platform.forge.EventBuses;
import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NostalgicTweaks.MOD_ID)
public class ClientForge
{
    public ClientForge()
    {
        // Submit Architectury event bus so Architectury can register content at the right time
        EventBuses.registerModEventBus(NostalgicTweaks.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
    }
}
