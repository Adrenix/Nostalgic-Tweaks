package mod.adrenix.nostalgic.fabric.config;

import mod.adrenix.nostalgic.client.config.ClientKeyMapping;
import mod.adrenix.nostalgic.util.client.KeyUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

/**
 * This class registers the common key mappings that are defined in {@link ClientKeyMapping}.
 * The key press handlers are registered to accept information at the end of a client tick.
 */

public abstract class KeyRegistry
{
    /* Configuration Key */

    private static KeyMapping openConfig;

    /* Toggle Fog Key */

    private static KeyMapping toggleFog;

    /**
     * Registers the mod's key mappings.
     */
    public static void register()
    {
        openConfig = KeyBindingHelper.registerKeyBinding(ClientKeyMapping.CONFIG_KEY);
        toggleFog = KeyBindingHelper.registerKeyBinding(ClientKeyMapping.FOG_KEY);

        ClientTickEvents.END_CLIENT_TICK.register(client -> KeyUtil.onOpenConfig(openConfig));
        ClientTickEvents.END_CLIENT_TICK.register(client -> KeyUtil.onToggleFog(toggleFog));
    }
}
