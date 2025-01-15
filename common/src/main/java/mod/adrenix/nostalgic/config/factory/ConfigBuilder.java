package mod.adrenix.nostalgic.config.factory;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.io.PathUtil;

import java.nio.file.Path;

public abstract class ConfigBuilder
{
    /* Temporary Loading */

    /**
     * Get a temporary optional custom config handler for a config class.
     *
     * @param config The config structure class.
     * @param path   The path that points to where the config file is located.
     * @param <T>    The config structure class type.
     * @return A {@link ConfigHandler} instance.
     */
    @PublicAPI
    public static <T extends ConfigMeta> ConfigHandler<T> temp(Class<T> config, Path path)
    {
        return new ConfigHandler<>(config, path, ConfigPermissions.READ_ONLY, () -> { }, () -> { });
    }

    /**
     * Get a temporary optional custom config handler for a config class.
     *
     * @param config The config structure class.
     * @param path   The path that points to where the config file is located.
     * @param onLoad Define instructions to perform after what is currently saved on disk is loaded into memory.
     * @param <T>    The config structure class type.
     * @return A {@link ConfigHandler} instance.
     */
    @PublicAPI
    public static <T extends ConfigMeta> ConfigHandler<T> temp(Class<T> config, Path path, Runnable onLoad)
    {
        return new ConfigHandler<>(config, path, ConfigPermissions.READ_ONLY, onLoad, () -> { });
    }

    /* Fields */

    private static ConfigHandler<?> instance = null;

    /* Methods */

    /**
     * Register a new config class to the config loader's registry.
     *
     * @param config The config structure class.
     * @param onLoad Define instructions to perform after a config is loaded.
     * @param <T>    The config structure class type.
     * @throws RuntimeException When the config is already registered or the config class does not have a {@code Config}
     *                          annotation.
     */
    @PublicAPI
    public static <T extends ConfigMeta> void create(Class<T> config, Runnable onLoad)
    {
        create(config, onLoad, () -> { });
    }

    /**
     * Register a new config class to the config loader's registry.
     *
     * @param config The config structure class.
     * @param onLoad Define instructions to perform after what is currently saved on disk is loaded into memory.
     * @param onSave Define instructions to perform after what is currently loaded is saved to disk.
     * @param <T>    The config structure class type.
     * @throws RuntimeException When the config is already registered or the config class does not have a {@code Config}
     *                          annotation.
     */
    @PublicAPI
    public static <T extends ConfigMeta> void create(Class<T> config, Runnable onLoad, Runnable onSave)
    {
        String name = config.getSimpleName();

        if (ConfigBuilder.instance != null)
            throw new RuntimeException(String.format("[Config Factory] %s is already built", name));

        Config structure = config.getAnnotation(Config.class);

        if (structure == null)
            throw new RuntimeException(String.format("[Config Factory] No @Config annotation is attached to %s", name));

        Path path = PathUtil.getConfigPath().resolve(structure.filename() + ".json");
        ConfigBuilder.instance = new ConfigHandler<>(config, path, ConfigPermissions.READ_WRITE, onLoad, onSave);
        ConfigBuilder.instance.init();

        NostalgicTweaks.LOGGER.info(String.format("[Config Factory] Successfully built %s", name));
    }

    /**
     * Get the {@link ConfigHandler} instance that is currently loaded in memory.
     *
     * @param <T> The config class type.
     * @return A {@link ConfigHandler} that is associated with the loaded config.
     * @throws RuntimeException If the config class has not yet been built.
     */
    @SuppressWarnings("unchecked") // The config class type is guaranteed since only one config can be loaded
    public static <T extends ConfigMeta> ConfigHandler<T> getHandler()
    {
        if (ConfigBuilder.instance == null)
            throw new RuntimeException("[Config Factory] A config has not yet been built");

        return (ConfigHandler<T>) ConfigBuilder.instance;
    }

    /**
     * Get the config instance from the given class type that is currently loaded in memory.
     *
     * @param <T> The config class type.
     * @return A config instance that is associated with the given config class.
     * @throws RuntimeException If the config class has not yet been built.
     */
    @SuppressWarnings("unchecked") // The config class type is guaranteed since only one config can be loaded
    public static <T extends ConfigMeta> T getConfig(Class<T> config)
    {
        if (ConfigBuilder.instance == null)
            throw new RuntimeException("[Config Factory] A config has not yet been built");
        else if (ClassUtil.isNotInstanceOf(ConfigBuilder.instance.getLoaded(), config))
            throw new RuntimeException("[Config Factory] Loaded config is not an instance of the given class type");
        else
            return (T) ConfigBuilder.instance.getLoaded();
    }
}
