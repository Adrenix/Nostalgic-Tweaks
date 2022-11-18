package mod.adrenix.nostalgic.forge.register;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.forge.network.NostalgicNetwork;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Handler class that subscribes common setup instructions to Forge's event bus.
 * This class is used by both the client and server.
 */

@Mod.EventBusSubscriber(modid = NostalgicTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class CommonSetup
{
    /**
     * Initializes the network channel so that network protocol matching is enforced.
     * @param event A FML common setup event instance.
     */
    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) { NostalgicNetwork.init(); }
}
