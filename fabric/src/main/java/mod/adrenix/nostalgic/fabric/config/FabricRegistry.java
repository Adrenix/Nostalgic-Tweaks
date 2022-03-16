package mod.adrenix.nostalgic.fabric.config;

import mod.adrenix.nostalgic.client.config.CommonRegistry;
import mod.adrenix.nostalgic.util.KeyUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public abstract class FabricRegistry
{
    /* Configuration Key */
    private static KeyMapping openConfig;

    /* Toggle Fog Key */
    private static KeyMapping toggleFog;

    /* Fabric Key Handler */
    public static void registerConfigurationKey()
    {
        openConfig = KeyBindingHelper.registerKeyBinding(CommonRegistry.getConfigurationKey());
        toggleFog = KeyBindingHelper.registerKeyBinding(CommonRegistry.getFogKey());

        ClientTickEvents.END_CLIENT_TICK.register(client -> KeyUtil.onOpenConfig(openConfig));
        ClientTickEvents.END_CLIENT_TICK.register(client -> KeyUtil.onToggleFog(toggleFog));
    }
}
