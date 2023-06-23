package mod.adrenix.nostalgic.forge.register;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import mod.adrenix.nostalgic.client.event.ArchitecturyClientEvents;
import mod.adrenix.nostalgic.forge.api.event.NostalgicHudEvent;
import mod.adrenix.nostalgic.forge.api.test.ApiTestEventHandler;
import mod.adrenix.nostalgic.forge.event.client.support.AppleSkinEvents;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.client.GuiUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Registry handler class for the client that subscribes mod events to Forge's event bus.
 * This class is focused on client events.
 */

@Mod.EventBusSubscriber(modid = NostalgicTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public abstract class ClientRegistry
{
    /**
     * Instructions for client initialization.
     * @param event A FML client setup event.
     */
    @SubscribeEvent
    public static void init(FMLClientSetupEvent event)
    {
        // Register config screen
        ModLoadingContext.get().registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class, () ->
                new ConfigScreenHandler.ConfigScreenFactory(((minecraft, screen) -> new SettingsScreen(screen, false)))
        );

        // Define mod screen
        GuiUtil.modScreen = ModListScreen::new;

        // Mod tracking
        ModTracker.init(ModList.get()::isLoaded);

        // Initialize the client
        NostalgicTweaks.initClient();

        // Register architectury events
        ArchitecturyClientEvents.register();

        // Register Nostalgic API main events
        NostalgicHudEvent.register();

        // Register Nostalgic API test events
        if (ModList.get().isLoaded(NostalgicTweaks.MOD_ID) && NostalgicTweaks.isEventTesting())
        {
            MinecraftForge.EVENT_BUS.register(new ApiTestEventHandler());
            NostalgicTweaks.LOGGER.debug("Registered Mod API (Forge) event tests");
        }

        // Mod support events
        if (ModTracker.APPLE_SKIN.isInstalled())
        {
            MinecraftForge.EVENT_BUS.register(new AppleSkinEvents());
            NostalgicTweaks.LOGGER.info("Registered AppleSkin support events");
        }
    }
}
