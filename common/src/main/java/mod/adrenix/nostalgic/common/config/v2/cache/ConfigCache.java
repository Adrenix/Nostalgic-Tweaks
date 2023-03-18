package mod.adrenix.nostalgic.common.config.v2.cache;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.auto.AutoConfig;
import mod.adrenix.nostalgic.common.config.auto.serializer.GsonConfigSerializer;
import mod.adrenix.nostalgic.common.config.v2.client.ClientConfig;
import mod.adrenix.nostalgic.common.config.v2.server.ServerConfig;
import net.minecraft.world.InteractionResult;

/**
 * This class is responsible for getting the saved config file on disk and caching the saved values and is not
 * used for runtime value retrieval.
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
     * @return A client config cache.
     */
    public static ClientConfig client()
    {
        if (!ConfigCache.clientInitialized)
            ConfigCache.preloadClient();

        return clientCache;
    }

    /**
     * Preloads the server config cache if config value retrieval is performed too early.
     * @return A server config cache.
     */
    public static ServerConfig server()
    {
        if (!ConfigCache.serverInitialized)
            ConfigCache.preloadServer();

        return serverCache;
    }

    /**
     * Reloads and validates the client config file saved on disk.
     * @return Always returns a success result regardless of client config file validity.
     */
    private static InteractionResult reloadClient()
    {
        // Retrieve new config and validate its data
        ConfigCache.clientCache = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();

        // Let consoles know what happened and what was loaded
        NostalgicTweaks.LOGGER.info("Client config was reloaded");

        return InteractionResult.SUCCESS;
    }

    /**
     * Reloads and validates the server config file saved on disk.
     * @return Always returns a success result regardless of server config file validity.
     */
    private static InteractionResult reloadServer()
    {
        // Retrieve new config and validate its data
        ConfigCache.serverCache = AutoConfig.getConfigHolder(ServerConfig.class).getConfig();

        // Let consoles know what happened and what was loaded
        NostalgicTweaks.LOGGER.info("Server config was reloaded");

        return InteractionResult.SUCCESS;
    }

    /**
     * Loads the client configuration cache prematurely when the game starts mixin patching.
     * @throws AssertionError When this method is called by the server.
     */
    private static void preloadClient()
    {
        if (NostalgicTweaks.isServer())
        {
            String fail = String.format("[%s] Cannot initialize client config for server.", NostalgicTweaks.MOD_NAME);
            throw new AssertionError(fail);
        }

        NostalgicTweaks.LOGGER.info("Initializing client config prematurely for mixin compatibility");
        ConfigCache.initClient();
    }

    /**
     * Loads the server configuration cache prematurely when the game starts mixin patching.
     */
    private static void preloadServer()
    {
        NostalgicTweaks.LOGGER.info("Initializing server config prematurely for mixin compatibility");
        ConfigCache.initServer();
    }

    /**
     * Initializes the client configuration cache when the game starts.
     * This only happens once and this method will return early if the cache has already been initialized.
     */
    public static void initClient()
    {
        // Do not initialize again if this method was run prematurely
        if (!ConfigCache.clientInitialized)
            ConfigCache.clientInitialized = true;
        else
            return;

        // Register and cache config
        AutoConfig.register(ClientConfig.class, GsonConfigSerializer::new);
        AutoConfig.getConfigHolder(ClientConfig.class).registerLoadListener((manager, update) -> reloadClient());
        AutoConfig.getConfigHolder(ClientConfig.class).registerSaveListener((manager, data) -> reloadClient());
    }

    /**
     * Initializes the server configuration cache when the game starts.
     * This only happens once and this method will return early if the cache has already been initialized.
     */
    public static void initServer()
    {
        // Do not initialize again if this method was run prematurely
        if (!ConfigCache.serverInitialized)
            ConfigCache.serverInitialized = true;
        else
            return;

        // Register and cache config
        AutoConfig.register(ServerConfig.class, GsonConfigSerializer::new);
        AutoConfig.getConfigHolder(ServerConfig.class).registerLoadListener((manager, update) -> reloadServer());
        AutoConfig.getConfigHolder(ServerConfig.class).registerSaveListener((manager, data) -> reloadServer());
    }

    /**
     * Saves the current state of the client config to disk.
     */
    public static void saveClient()
    {
        AutoConfig.getConfigHolder(ClientConfig.class).save();
    }

    /**
     * Saves the current state of the server config to disk.
     */
    public static void saveServer()
    {
        AutoConfig.getConfigHolder(ServerConfig.class).save();
    }
}
