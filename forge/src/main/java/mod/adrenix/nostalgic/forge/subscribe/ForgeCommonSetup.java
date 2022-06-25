package mod.adrenix.nostalgic.forge.subscribe;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.forge.network.ForgeNetwork;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = NostalgicTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class ForgeCommonSetup
{
    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event)
    {
        ForgeNetwork.init();
    }
}
