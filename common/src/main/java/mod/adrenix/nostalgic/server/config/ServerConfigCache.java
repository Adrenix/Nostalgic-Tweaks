package mod.adrenix.nostalgic.server.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import net.minecraft.world.InteractionResult;

/**
 * This class is used exclusively by the server. It caches the current values stored on disk.
 *
 * The {@link TweakServerCache} caches these values and provides the server with an interface to send updated tweaks
 * to connected players without interfering with the values saved on disk.
 */

public abstract class ServerConfigCache
{
    /* Configuration Caching */

    private static boolean isInitialized = false;
    private static final ServerConfig CLIENT_CACHE = new ServerConfig();
    private static ServerConfig cache = new ServerConfig();
    public static ServerConfig getRoot() { return cache; }
    public static ServerConfig.EyeCandy getCandy() { return NostalgicTweaks.isServer() ? cache.eyeCandy : CLIENT_CACHE.eyeCandy; }

    private static InteractionResult reloadConfiguration()
    {
        // Retrieve new config
        cache = AutoConfig.getConfigHolder(ServerConfig.class).getConfig();

        // Let consoles know what happened
        NostalgicTweaks.LOGGER.info("Server config was reloaded");

        return InteractionResult.SUCCESS;
    }

    public static void initializeConfiguration()
    {
        // Do not initialize again this method was already run
        if (!isInitialized)
            isInitialized = true;
        else
            return;

        // Register and cache config
        AutoConfig.register(ServerConfig.class, GsonConfigSerializer::new);
        AutoConfig.getConfigHolder(ServerConfig.class).registerLoadListener((manager, update) -> reloadConfiguration());
        AutoConfig.getConfigHolder(ServerConfig.class).registerSaveListener((manager, data) -> reloadConfiguration());
        reloadConfiguration();

        // Inform console
        NostalgicTweaks.LOGGER.info(String.format("Loaded %d server controlled tweaks", TweakServerCache.all().size()));
    }
}
