package mod.adrenix.nostalgic.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import mod.adrenix.nostalgic.client.event.ClientEventHelper;
import mod.adrenix.nostalgic.tweak.gui.KeybindingId;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.client.KeyMapping;

import java.util.Optional;

/**
 * This helper class keeps a record of all key mappings that are used by the mod. These mappings are used by both mod
 * loaders.
 */
public abstract class ClientKeyMapping
{
    /**
     * The config key, when pressed, opens the mod's configuration user interface.
     */
    public static final KeyMapping CONFIG_KEY = new KeyMapping(Lang.Binding.OPEN_CONFIG.getString(), InputConstants.KEY_O, Lang.TITLE.getString());

    /**
     * The fog key, when pressed, toggles the game's rendering distance. The value is changed by the old predefined
     * rendering distances used before the game switched to a slider. This will cycle between the following distance;
     * far (16), normal (8), short (4), and tiny (2).
     */
    public static final KeyMapping FOG_KEY = new KeyMapping(Lang.Binding.TOGGLE_FOG.getString(), -1, Lang.TITLE.getString());

    /**
     * Get a key mapping based on the given {@link KeybindingId}.
     *
     * @param id A {@link KeybindingId} enumeration.
     * @return A {@link KeyMapping} that is linked to the given key type.
     */
    public static KeyMapping getFromId(KeybindingId id)
    {
        return switch (id)
        {
            case CONFIG -> CONFIG_KEY;
            case FOG -> FOG_KEY;
        };
    }

    /**
     * Register the client's key mappings using Architectury.
     */
    public static void register()
    {
        KeyMappingRegistry.register(CONFIG_KEY);
        KeyMappingRegistry.register(FOG_KEY);

        ClientScreenInputEvent.KEY_PRESSED_POST.register((minecraft, screen, keyCode, scanCode, modifiers) -> {
            Optional<KeyMapping> mapping = KeyboardUtil.find(Lang.Binding.OPEN_CONFIG);

            if (mapping.isPresent() && mapping.get().matches(keyCode, scanCode))
                ClientEventHelper.gotoSettingsIfPossible(minecraft);

            return EventResult.pass();
        });

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (CONFIG_KEY.consumeClick())
                ClientEventHelper.gotoSettingsIfPossible(minecraft);

            while (FOG_KEY.consumeClick())
                ClientEventHelper.cycleRenderDistance(minecraft);
        });
    }
}
