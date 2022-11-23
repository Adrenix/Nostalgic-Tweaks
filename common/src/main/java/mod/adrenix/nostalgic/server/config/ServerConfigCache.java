package mod.adrenix.nostalgic.server.config;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.auto.AutoConfig;
import mod.adrenix.nostalgic.common.config.auto.serializer.GsonConfigSerializer;
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
            return ServerConfigCache.CLIENT_CACHE;

        if (!ServerConfigCache.initialized)
            ServerConfigCache.preload();

        return ServerConfigCache.cache;
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
    private static InteractionResult reload()
    {
        // Retrieve new config
        ServerConfigCache.cache = AutoConfig.getConfigHolder(ServerConfig.class).getConfig();

        // Let consoles know what happened
        NostalgicTweaks.LOGGER.info("Server config was reloaded");

        return InteractionResult.SUCCESS;
    }

    /**
     * Loads the server configuration cache prematurely when the server starts mixin patching.
     */
    public static void preload()
    {
        NostalgicTweaks.LOGGER.info("Initializing server config prematurely for mixin compatibility");
        ServerConfigCache.initialize();
    }

    /**
     * Initializes the server configuration cache when the server starts.
     * This only happens once and this method will return early if the cache has already been initialized.
     */
    public static void initialize()
    {
        // Do not initialize again this method was already run
        if (!ServerConfigCache.initialized)
            ServerConfigCache.initialized = true;
        else
            return;

        // Register and cache config
        AutoConfig.register(ServerConfig.class, GsonConfigSerializer::new);
        AutoConfig.getConfigHolder(ServerConfig.class).registerLoadListener((manager, update) -> reload());
        AutoConfig.getConfigHolder(ServerConfig.class).registerSaveListener((manager, data) -> reload());
        ServerConfigCache.reload();

        // Inform console
        NostalgicTweaks.LOGGER.info("Loaded %d server controlled tweaks", TweakServerCache.all().size());
    }
}
