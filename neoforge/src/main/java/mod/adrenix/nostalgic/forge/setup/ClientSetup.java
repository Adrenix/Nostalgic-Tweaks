package mod.adrenix.nostalgic.forge.setup;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.screen.home.HomeScreen;
import mod.adrenix.nostalgic.forge.event.AppleSkinHandler;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.client.gui.ModListScreen;
import net.neoforged.neoforge.common.NeoForge;

@Mod.EventBusSubscriber(
    modid = NostalgicTweaks.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT
)
public abstract class ClientSetup
{
    /**
     * Instructions for client initialization.
     *
     * @param event A {@link FMLClientSetupEvent} event instance.
     */
    @SubscribeEvent
    public static void init(FMLClientSetupEvent event)
    {
        // Register config screen
        ModLoadingContext.get()
            .registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, ClientSetup::getScreenFactory);

        // Define mod screen
        GuiUtil.modListScreen = ModListScreen::new;

        // Listen for AppleSkin
        if (ModTracker.APPLE_SKIN.isInstalled())
        {
            NeoForge.EVENT_BUS.register(AppleSkinHandler.class);
            NostalgicTweaks.LOGGER.info("Registered NeoForge AppleSkin Listener");
        }
    }

    /**
     * @return A {@link HomeScreen} instance that represents the mod's {@link ConfigScreenHandler.ConfigScreenFactory}.
     */
    private static ConfigScreenHandler.ConfigScreenFactory getScreenFactory()
    {
        return new ConfigScreenHandler.ConfigScreenFactory(((minecraft, screen) -> new HomeScreen(screen, false)));
    }
}
