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
 * to connected players without referencing the values saved on disk.
 */

public abstract class ServerConfigCache
{
    /* Cache Fields */

    private static boolean initialized = false;
    private static final ServerConfig CLIENT_CACHE = new ServerConfig();
    private static ServerConfig cache;

    /**
     * If the caller is from a client, then return a dummy cache since the server cache is never used by the client.
     * Otherwise, preload the configuration data if this cache has not already been initialized.
     *
     * @return The server config cache if the caller was the server, or a dummy cache if the caller was the client.
     */
    private static ServerConfig getCache()
    {
        // This cache is only used by the ModConfig class and is not used for logic
        if (NostalgicTweaks.isClient())
            return CLIENT_CACHE;

        if (!initialized)
            preloadConfiguration();

        return cache;
    }

    /* Quick Group Cache Access */

    public static ServerConfig getRoot() { return getCache(); }
    public static ServerConfig.EyeCandy getCandy() { return getCache().eyeCandy; }
    public static ServerConfig.Gameplay getGameplay() { return getCache().gameplay; }
    public static ServerConfig.Animation getAnimation() { return getCache().animation; }

    /* Cache Methods */

    /**
     * Reloads and validates the config file saved on disk.
     * @return Always returns a success result regardless of config file validity.
     */
    private static InteractionResult reloadConfiguration()
    {
        // Retrieve new config
        cache = AutoConfig.getConfigHolder(ServerConfig.class).getConfig();

        // Let consoles know what happened
        NostalgicTweaks.LOGGER.info("Server config was reloaded");

        return InteractionResult.SUCCESS;
    }

    /**
     * Loads the server configuration cache prematurely when the server starts mixin patching.
     */
    public static void preloadConfiguration()
    {
        NostalgicTweaks.LOGGER.info("Initializing server config prematurely for mixin compatibility");
        initializeConfiguration();
    }

    /**
     * Initializes the server configuration cache when the server starts.
     * This only happens once and this method will return early if the cache has already been initialized.
     */
    public static void initializeConfiguration()
    {
        // Do not initialize again this method was already run
        if (!initialized)
            initialized = true;
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
