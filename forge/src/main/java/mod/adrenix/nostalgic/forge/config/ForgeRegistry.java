package mod.adrenix.nostalgic.forge.config;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.CommonRegistry;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import mod.adrenix.nostalgic.util.KeyUtil;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NostalgicTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public abstract class ForgeRegistry
{
    /* Configuration Key */
    public static final KeyMapping OPEN_CONFIG = CommonRegistry.getConfigurationKey();

    /* Toggle Fog key */
    public static final KeyMapping TOGGLE_FOG = CommonRegistry.getFogKey();

    /* Client setup */
    @SuppressWarnings("unused")
    public static void setup(final FMLClientSetupEvent event)
    {
        // Register config screen
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () ->
                new ConfigGuiHandler.ConfigGuiFactory(((minecraft, screen) ->
                        new SettingsScreen(screen, false))))
        ;

        // Register key that opens config while in-game
        ClientRegistry.registerKeyBinding(OPEN_CONFIG);

        // Register key that toggles the fog while in-game
        ClientRegistry.registerKeyBinding(TOGGLE_FOG);

        // Initialize the client
        NostalgicTweaks.initClient(NostalgicTweaks.Environment.FORGE);
    }

    /* Subscribe Configuration Key */
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            KeyUtil.onOpenConfig(OPEN_CONFIG);
            KeyUtil.onToggleFog(TOGGLE_FOG);
        }
    }
}
