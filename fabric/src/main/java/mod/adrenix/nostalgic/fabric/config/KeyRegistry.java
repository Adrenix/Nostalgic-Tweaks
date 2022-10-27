package mod.adrenix.nostalgic.fabric.config;

import mod.adrenix.nostalgic.client.config.ClientKeyMapping;
import mod.adrenix.nostalgic.util.client.KeyUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public abstract class KeyRegistry
{
    /* Configuration Key */
    private static KeyMapping openConfig;

    /* Toggle Fog Key */
    private static KeyMapping toggleFog;

    /* Fabric Key Handler */
    public static void registerConfigurationKey()
    {
        openConfig = KeyBindingHelper.registerKeyBinding(ClientKeyMapping.CONFIG_KEY);
        toggleFog = KeyBindingHelper.registerKeyBinding(ClientKeyMapping.FOG_KEY);

        ClientTickEvents.END_CLIENT_TICK.register(client -> KeyUtil.onOpenConfig(openConfig));
        ClientTickEvents.END_CLIENT_TICK.register(client -> KeyUtil.onToggleFog(toggleFog));
    }
}
