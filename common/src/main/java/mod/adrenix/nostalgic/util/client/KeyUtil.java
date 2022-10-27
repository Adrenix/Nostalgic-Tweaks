package mod.adrenix.nostalgic.util.client;

import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for the mod's available key mappings.
 */

public abstract class KeyUtil
{
    /**
     * This cache prevents the need to cycle through all the key mappings.
     * Extremely useful for when there are a lot of key mappings.
     */
    private static final Map<String, KeyMapping> MAPPING_CACHE = new HashMap<>();

    /**
     * Checks if the fog key is currently being held down.
     */
    public static boolean isFogDown = false;

    /* Key Helpers */

    /**
     * Finds a key mapping based on the given <code>langKey</code>
     * @param langKey The key to search for in the game's key mappings.
     * @return A key mapping if one was found, <code>null</code> otherwise.
     */
    public static KeyMapping find(String langKey)
    {
        KeyMapping cache = MAPPING_CACHE.get(langKey);
        if (cache != null)
            return cache;

        KeyMapping[] allMappings = Minecraft.getInstance().options.keyMappings;

        for (KeyMapping keyMapping : allMappings)
        {
            if (keyMapping.getName().equals(langKey))
            {
                MAPPING_CACHE.put(langKey, keyMapping);
                return keyMapping;
            }
        }

        return null;
    }

    /**
     * Opens the mod's configuration screen if the configuration key mapping is being held down.
     * @param openConfig The key mapping to check.
     */
    public static void onOpenConfig(KeyMapping openConfig)
    {
        if (openConfig.isDown() && Minecraft.getInstance().screen == null)
            Minecraft.getInstance().setScreen(new SettingsScreen(null, true));
    }

    /**
     * Toggles the game's fog rendering distance if the fog key mapping is being held down.
     * @param toggleFog The key mapping to check.
     */
    public static void onToggleFog(KeyMapping toggleFog)
    {
        boolean isReleased = false;
        if (!isFogDown && toggleFog.isDown())
            isFogDown = true;
        else if (isFogDown && !toggleFog.isDown())
        {
            isFogDown = false;
            isReleased = true;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (isReleased && minecraft.level != null)
        {
            int distance = minecraft.options.renderDistance().get();
            if (distance >= 16)
                minecraft.options.renderDistance().set(8);
            else if (distance >= 8)
                minecraft.options.renderDistance().set(4);
            else if (distance >= 4)
                minecraft.options.renderDistance().set(2);
            else if (distance >= 2)
                minecraft.options.renderDistance().set(16);

            minecraft.levelRenderer.needsUpdate();
            minecraft.options.save();
        }
    }
}
