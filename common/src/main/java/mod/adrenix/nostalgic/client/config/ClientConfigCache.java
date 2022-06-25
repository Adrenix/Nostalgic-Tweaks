package mod.adrenix.nostalgic.client.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import net.minecraft.world.InteractionResult;

/**
 * This class is used exclusively by the client. It caches the current values stored on disk.
 *
 * The {@link TweakClientCache} caches these values and lets the user change those values without interfering with the
 * values saved on disk.
 */

public abstract class ClientConfigCache
{
    /* Configuration Caching */

    private static ClientConfig cache = new ClientConfig();
    public static ClientConfig getRoot() { return cache; }
    public static ClientConfig.Sound getSound() { return cache.sound; }
    public static ClientConfig.EyeCandy getCandy() { return cache.eyeCandy; }
    public static ClientConfig.Animation getAnimation() { return cache.animation; }
    public static ClientConfig.Swing getSwing() { return cache.swing; }
    public static ClientConfig.Gui getGui() { return cache.gui; }
    private static boolean isInitialized = false;

    private static InteractionResult reloadConfiguration()
    {
        // Retrieve new config and validate its data
        ClientConfigCache.cache = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();
        CustomSwings.validate();

        // Let consoles know what happened and what was loaded
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
        // Do not initialize again if this method was run prematurely
        if (!isInitialized)
            isInitialized = true;
        else
            return;

        // Register and cache config
        AutoConfig.register(ClientConfig.class, GsonConfigSerializer::new);
        AutoConfig.getConfigHolder(ClientConfig.class).registerLoadListener((manager, update) -> reloadConfiguration());
        AutoConfig.getConfigHolder(ClientConfig.class).registerSaveListener((manager, data) -> reloadConfiguration());
        reloadConfiguration();

        // List loaded tweaks
        NostalgicTweaks.LOGGER.info(String.format("Loaded %d tweaks", TweakClientCache.all().size()));

        // Let consoles know what happened
        NostalgicTweaks.LOGGER.info(String.format("Registered %d customized swing speeds", cache.custom.size()));
    }
}
