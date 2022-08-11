package mod.adrenix.nostalgic.util.client;

import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import java.util.HashMap;
import java.util.Map;

public abstract class KeyUtil
{
    /* Key Trackers */

    private static final Map<String, KeyBinding> MAPPING_CACHE = new HashMap<>();

    public static boolean isFogDown = false;

    /* Key Helpers */

    public static KeyBinding find(String langKey)
    {
        KeyBinding cache = MAPPING_CACHE.get(langKey);
        if (cache != null)
            return cache;

        KeyBinding[] allMappings = MinecraftClient.getInstance().options.allKeys;

        for (KeyBinding keyMapping : allMappings)
        {
            if (keyMapping.getTranslationKey().equals(langKey))
            {
                MAPPING_CACHE.put(langKey, keyMapping);
                return keyMapping;
            }
        }

        return null;
    }

    public static void onOpenConfig(KeyBinding openConfig)
    {
        if (openConfig.isPressed() && MinecraftClient.getInstance().currentScreen == null)
            MinecraftClient.getInstance().setScreen(new SettingsScreen(null, true));
    }

    public static void onToggleFog(KeyBinding toggleFog)
    {
        boolean isReleased = false;
        if (!isFogDown && toggleFog.isPressed())
            isFogDown = true;
        else if (isFogDown && !toggleFog.isPressed())
        {
            isFogDown = false;
            isReleased = true;
        }

        MinecraftClient minecraft = MinecraftClient.getInstance();

        if (isReleased && minecraft.world != null)
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

            minecraft.worldRenderer.scheduleTerrainUpdate();
            minecraft.options.write();
        }
    }
}
