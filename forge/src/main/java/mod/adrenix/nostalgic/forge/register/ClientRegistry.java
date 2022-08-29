package mod.adrenix.nostalgic.forge.register;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.network.NetworkHooks;

@Mod.EventBusSubscriber(modid = NostalgicTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public abstract class ClientRegistry
{
    /* Client setup */
    @SubscribeEvent
    public static void setup(final FMLClientSetupEvent event)
    {
        // Register config screen
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
            new ConfigScreenHandler.ConfigScreenFactory(((minecraft, screen) -> new SettingsScreen(screen, false))));

        // Initialize the client
        NostalgicTweaks.initClient(NostalgicTweaks.Environment.FORGE);

        // Setup forge connection status
        NostalgicTweaks.isForgeConnected = () -> {
            Minecraft minecraft = Minecraft.getInstance();
            ClientPacketListener connection = minecraft.getConnection();

            if (minecraft.getSingleplayerServer() != null && !minecraft.getSingleplayerServer().isPublished())
                return false;

            if (connection != null)
                return !NetworkHooks.isVanillaConnection(connection.getConnection());
            return false;
        };
    }
}
