package mod.adrenix.nostalgic.config.cache;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.config.ClientConfig;
import mod.adrenix.nostalgic.config.ServerConfig;
import mod.adrenix.nostalgic.config.factory.ConfigBuilder;
import mod.adrenix.nostalgic.tweak.TweakValidator;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;

import java.nio.file.Path;

/**
 * This class is responsible for getting the saved config file on disk and caching the saved values. This must not be
 * used for runtime value retrieval since the purpose of caching is for runtime reflection.
 */
public abstract class ConfigCache
{
    /* Fields */

    private static boolean clientInitialized = false;
    private static boolean serverInitialized = false;
    private static ClientConfig clientCache;
    private static ServerConfig serverCache;

    /* Methods */

    /**
     * Preloads the client config cache if config value retrieval is performed too early.
     * <br><br>
     * This should <b color=red>not</b> be used to get the value from a tweak. Use the tweak class built-in value getter
     * methods which automatically return values based on logical side and mod status.
     *
     * @return A client config cache.
     */
    public static ClientConfig client()
    {
        if (!clientInitialized)
            preloadClient();

        return clientCache;
    }

    /**
     * Preloads the server config cache if config value retrieval is performed too early.
     * <br><br>
     * This should <b color=red>not</b> be used to get the value from a tweak. Use the tweak class built-in value getter
     * methods which automatically return values based on logical side and mod status.
     *
     * @return A server config cache.
     */
    public static ServerConfig server()
    {
        if (!serverInitialized)
            preloadServer();

        return serverCache;
    }

    /**
     * Validates the client config file loaded into memory and then assigns it to the loaded client config cache.
     */
    private static void reloadClient()
    {
        clientCache = ConfigBuilder.getConfig(ClientConfig.class);

        TweakPool.values().forEach(ConfigReflect::syncTweak);
        TweakPool.values().forEach(Tweak::sync);
        TweakValidator.check(ClientConfig.class);
    }

    /**
     * Validates the server config file loaded into memory and then assigns it to the loaded server config cache.
     */
    private static void reloadServer()
    {
        serverCache = ConfigBuilder.getConfig(ServerConfig.class);

        TweakPool.filter(Tweak::isMultiplayerLike).forEach(ConfigReflect::syncTweak);
        TweakValidator.check(ServerConfig.class);
    }

    /**
     * Loads the client configuration cache prematurely when the game starts mixin patching.
     *
     * @throws AssertionError When the server calls this method.
     */
    private static void preloadClient()
    {
        if (NostalgicTweaks.isServer())
            throw new AssertionError(String.format("[%s] Cannot initialize client config for server.", NostalgicTweaks.MOD_NAME));

        NostalgicTweaks.LOGGER.info("[Config Cache] Initializing client config prematurely for mixin compatibility");

        initClient();
    }

    /**
     * Loads the server configuration cache prematurely when the game starts mixin patching.
     */
    private static void preloadServer()
    {
        NostalgicTweaks.LOGGER.info("[Config Cache] Initializing server config prematurely for mixin compatibility");

        initServer();
    }

    /**
     * Initializes the client configuration cache when the game starts. This only happens once, and this method will
     * return early if the cache has already been initialized.
     */
    public static void initClient()
    {
        // Do not initialize again if this method was run prematurely
        if (!clientInitialized)
            clientInitialized = true;
        else
            return;

        // Register and cache config
        ConfigBuilder.create(ClientConfig.class, ConfigCache::reloadClient);

        // Scan for container issues
        Container.scanForIssues();

        // List loaded tweaks
        NostalgicTweaks.LOGGER.info("[Config Cache] Loaded %d client tweaks", TweakPool.values().size());
    }

    /**
     * Initializes the server configuration cache when the game starts. This only happens once, and this method will
     * return early if the cache has already been initialized.
     */
    public static void initServer()
    {
        // Do not initialize again if this method was run prematurely
        if (!serverInitialized)
            serverInitialized = true;
        else
            return;

        // Register and cache config
        ConfigBuilder.create(ServerConfig.class, ConfigCache::reloadServer);

        // Scan for container issues
        Container.scanForIssues();

        // List loaded tweaks
        String message = "[Config Cache] Loaded %d server tweaks";
        NostalgicTweaks.LOGGER.info(message, TweakPool.filter(Tweak::isMultiplayerLike).toList().size());
    }

    /**
     * The config file will be kept in the default config folder used by mod loaders.
     *
     * @return A path within a mod loader config folder with the filename appended with a {@code .json} extension.
     */
    public static Path path()
    {
        return ConfigBuilder.getHandler().getPath();
    }

    /**
     * Saves the client config if the mod is running on the client, otherwise it saves the server config.
     */
    public static void save()
    {
        ConfigBuilder.getHandler().save();
    }
}
