package mod.adrenix.nostalgic.client.config;

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.EntryCache;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionResult;
import org.lwjgl.glfw.GLFW;

public abstract class CommonRegistry
{
    /* Configuration & Fog Key */

    public static KeyMapping getConfigurationKey()
    {
        return new KeyMapping(
            NostalgicLang.Key.OPEN_CONFIG,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            NostalgicLang.Key.CATEGORY_NAME
        );
    }

    public static KeyMapping getFogKey()
    {
        return new KeyMapping(
            NostalgicLang.Key.TOGGLE_FOG,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            NostalgicLang.Key.CATEGORY_NAME
        );
    }

    /* Configuration Caching */

    public static ClientConfig cache;
    public static ClientConfig getRoot() { return cache; }
    public static ClientConfig.Sound getSound() { return cache.sound; }
    public static ClientConfig.EyeCandy getCandy() { return cache.eyeCandy; }
    public static ClientConfig.Animation getAnimation() { return cache.animation; }
    public static ClientConfig.Swing getSwing() { return cache.swing; }
    public static ClientConfig.Gui getGui() { return cache.gui; }
    private static boolean isInitialized = false;

    public static InteractionResult reloadConfiguration()
    {
        // Retrieve new config and validate its data
        CommonRegistry.cache = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();
        CustomSwings.validate();

        // Let debugger screens know what happened and what was loaded
        NostalgicTweaks.LOGGER.info("Config was reloaded");

        return InteractionResult.SUCCESS;
    }

    public static void preloadConfiguration()
    {
        NostalgicTweaks.LOGGER.info("Initializing config prematurely for mixin compatibility");
        initializeConfiguration();
    }

    public static void initializeConfiguration()
    {
        // Do not initialize again if this method was run prematurely.
        if (!isInitialized)
            isInitialized = true;
        else
            return;

        // Register and cache config
        AutoConfig.register(ClientConfig.class, GsonConfigSerializer::new);
        AutoConfig.getConfigHolder(ClientConfig.class).registerLoadListener((manager, update) -> reloadConfiguration());
        AutoConfig.getConfigHolder(ClientConfig.class).registerSaveListener((manager, data) -> reloadConfiguration());
        reloadConfiguration();

        // List loaded features
        NostalgicTweaks.LOGGER.info(String.format("Loaded %d mod features", EntryCache.all().size()));

        // Let debugger screens know what happened
        NostalgicTweaks.LOGGER.info(String.format("Registered %d customized swing speeds", cache.custom.size()));
    }
}
