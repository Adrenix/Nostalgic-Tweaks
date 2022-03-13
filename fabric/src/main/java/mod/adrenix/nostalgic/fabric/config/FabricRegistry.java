package mod.adrenix.nostalgic.fabric.config;

import mod.adrenix.nostalgic.client.config.CommonRegistry;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public abstract class FabricRegistry
{
    /* Configuration Key */
    private static KeyMapping openConfig;

    public static void registerConfigurationKey()
    {
        openConfig = KeyBindingHelper.registerKeyBinding(CommonRegistry.getConfigurationKey());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openConfig.isDown() && Minecraft.getInstance().screen == null)
                Minecraft.getInstance().setScreen(new SettingsScreen(null, true));
        });
    }
}
