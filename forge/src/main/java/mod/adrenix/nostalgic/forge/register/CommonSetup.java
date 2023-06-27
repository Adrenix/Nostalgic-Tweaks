package mod.adrenix.nostalgic.forge.register;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.forge.network.NostalgicNetwork;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.common.TextureLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.nio.file.Files;
import java.util.Optional;

/**
 * Handler class that subscribes common setup instructions to Forge's event bus. This class is used by both the client
 * and server.
 */

@Mod.EventBusSubscriber(
    modid = NostalgicTweaks.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE
)
public abstract class CommonSetup
{
    /**
     * Initializes mod tracking and the network channel so that network protocol matching is enforced.
     *
     * @param event A FML common setup event instance.
     */
    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event)
    {
        // Mod Tacking
        ModTracker.init(ModList.get()::isLoaded);

        // Resource Tracking
        ModContainer mod = ModList.get().getModContainerById(NostalgicTweaks.MOD_ID).orElseThrow();

        TextureLocation.findResource = (path) -> Optional.of(mod.getModInfo()
            .getOwningFile()
            .getFile()
            .findResource(path)).filter(Files::exists);

        // Network
        NostalgicNetwork.init();
    }
}
