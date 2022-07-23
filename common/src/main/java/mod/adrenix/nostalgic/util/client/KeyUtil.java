package mod.adrenix.nostalgic.util.client;

import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;

public abstract class KeyUtil
{
    /* Key Trackers */

    private static final Map<String, KeyMapping> MAPPING_CACHE = new HashMap<>();

    public static boolean isFogDown = false;

    /* Key Helpers */

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

    public static void onOpenConfig(KeyMapping openConfig)
    {
        if (openConfig.isDown() && Minecraft.getInstance().screen == null)
            Minecraft.getInstance().setScreen(new SettingsScreen(null, true));
    }

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
