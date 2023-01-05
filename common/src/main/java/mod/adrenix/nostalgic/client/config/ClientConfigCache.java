package mod.adrenix.nostalgic.client.config;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.auto.AutoConfig;
import mod.adrenix.nostalgic.common.config.auto.serializer.GsonConfigSerializer;
import net.minecraft.world.InteractionResult;

/**
 * This class is used exclusively by the client. It caches the current values stored on disk.
 *
 * The {@link TweakClientCache} caches these values and lets the user change those values without interfering with the
 * values saved on disk.
 */

public abstract class ClientConfigCache
{
    /* Cache Fields */

    private static boolean initialized = false;
    private static final ClientConfig SERVER_CACHE = new ClientConfig();
    private static ClientConfig cache;

    /**
     * If the caller is from a server, then return a dummy cache since the client cache is never used by the server.
     * Otherwise, preload configuration data if this cache has not already been initialized.
     *
     * @return The client config cache if the caller was the client, or a dummy cache if the caller was the server.
     */
    private static ClientConfig getCache()
    {
        // This cache is only used by the ModConfig class and is not used for logic
        if (NostalgicTweaks.isServer())
            return ClientConfigCache.SERVER_CACHE;

        if (!ClientConfigCache.initialized)
            ClientConfigCache.preload();

        return ClientConfigCache.cache;
    }

    /* Quick Group Cache Access */

    public static ClientConfig getRoot() { return getCache(); }
    public static ClientConfig.Sound getSound() { return getCache().sound; }
    public static ClientConfig.EyeCandy getCandy() { return getCache().eyeCandy; }
    public static ClientConfig.Gameplay getGameplay() { return getCache().gameplay; }
    public static ClientConfig.Animation getAnimation() { return getCache().animation; }
    public static ClientConfig.Swing getSwing() { return getCache().swing; }
    public static ClientConfig.Gui getGui() { return getCache().gui; }

    /* Cache Methods */

    /**
     * Reloads and validates the config file saved on disk.
     * @return Always returns a success result regardless of config file validity.
     */
    private static InteractionResult reload()
    {
        // Retrieve new config and validate its data
        ClientConfigCache.cache = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();

        // Let consoles know what happened and what was loaded
        NostalgicTweaks.LOGGER.info("Config was reloaded");

        return InteractionResult.SUCCESS;
    }

    /**
     * Loads the client configuration cache prematurely when the game starts mixin patching.
     * @throws AssertionError When this method is called by the server.
     */
    public static void preload()
    {
        if (NostalgicTweaks.isServer())
        {
            String fail = String.format("[%s] Cannot initialize client config for server.", NostalgicTweaks.MOD_NAME);
            throw new AssertionError(fail);
        }

        NostalgicTweaks.LOGGER.info("Initializing client config prematurely for mixin compatibility");
        ClientConfigCache.initialize();
    }

    /**
     * Initializes the client configuration cache when the game starts.
     * This only happens once and this method will return early if the cache has already been initialized.
     */
    public static void initialize()
    {
        // Do not initialize again if this method was run prematurely
        if (!ClientConfigCache.initialized)
            ClientConfigCache.initialized = true;
        else
            return;

        // Register and cache config
        AutoConfig.register(ClientConfig.class, GsonConfigSerializer::new);
        AutoConfig.getConfigHolder(ClientConfig.class).registerLoadListener((manager, update) -> reload());
        AutoConfig.getConfigHolder(ClientConfig.class).registerSaveListener((manager, data) -> reload());
        ClientConfigCache.reload();

        // List loaded tweaks
        NostalgicTweaks.LOGGER.info("Loaded %d tweaks", TweakClientCache.all().size());
    }

    /**
     * Saves the current state of the client config to disk.
     */
    public static void save() { AutoConfig.getConfigHolder(ClientConfig.class).save(); }
}