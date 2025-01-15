package mod.adrenix.nostalgic.config.factory;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.io.BackupFile;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

public class ConfigHandler<T extends ConfigMeta>
{
    /* Fields */

    private final Path path;
    private final Class<T> config;
    private final GsonSerializer<T> serializer;
    private final ConfigPermissions permissions;
    private final Runnable onLoad;
    private final Runnable onSave;

    @Nullable private T loaded;

    /* Constructor */

    public ConfigHandler(Class<T> config, Path path, ConfigPermissions permissions, Runnable onLoad, Runnable onSave)
    {
        this.path = path;
        this.config = config;
        this.onLoad = onLoad;
        this.onSave = onSave;
        this.permissions = permissions;
        this.serializer = new GsonSerializer<>(config);
    }

    /* Methods */

    /**
     * Initialize this config handler. A config startup backup will be created before loading and saving the config on
     * disk.
     */
    void init()
    {
        this.startup();

        if (this.load())
            this.save();
    }

    /**
     * Change what is currently loaded into memory. This will override what was loaded from the disk, if anything was
     * loaded at all. This should only be used when using a temporary instance of a config that is not saved anywhere on
     * the disk. If this handler manages a config file saved on the disk, then use {@link #load()} instead.
     *
     * @param configInstance An instance of the config class type this handler manages.
     */
    public void setLoaded(@Nullable T configInstance)
    {
        this.loaded = configInstance;

        if (this.loaded == null)
        {
            NostalgicTweaks.LOGGER.warn("[Config Handler] Aborted runtime loading of %s since it was null", this.config.getSimpleName());
            return;
        }

        try
        {
            this.loaded.validate();
            this.onLoad.run();

            if (this.permissions == ConfigPermissions.READ_WRITE)
                NostalgicTweaks.LOGGER.info("[Config Handler] Successfully loaded runtime %s", this.config.getSimpleName());
            else
                NostalgicTweaks.LOGGER.info("[Config Handler] Successfully loaded temporary %s", this.config.getSimpleName());
        }
        catch (LoaderException exception)
        {
            NostalgicTweaks.LOGGER.error("[Config Handler] Failed runtime loading of %s\n%s", this.config.getSimpleName(), exception);
        }
    }

    /**
     * @return The current config that is loaded in memory held by this manager.
     */
    @Nullable
    public T getLoaded()
    {
        return this.loaded;
    }

    /**
     * @return A new default config instance.
     */
    public T getDefault()
    {
        return this.serializer.getDefault();
    }

    /**
     * The config file will be kept in the default config folder used by mod loaders.
     *
     * @return A path within a mod loader config folder with the filename appended with a {@code .json} extension.
     */
    public Path getPath()
    {
        return this.path;
    }

    /**
     * Exports the given config file in string format.
     *
     * @param path A {@link Path} that the config is to be written to.
     */
    public void export(Path path)
    {
        try
        {
            this.serializer.write(this.loaded, path);
        }
        catch (LoaderException exception)
        {
            NostalgicTweaks.LOGGER.error("[Config Handler] Could not write config to the given path\n%s", exception);
        }
    }

    /**
     * Creates a startup backup file for this manager in the mod's backup directory. Instead of being labeled as a
     * {@code backup} file, it will instead be labeled as a {@code startup_backup} file. Every time the mod starts, a
     * startup backup file will be created in case something horrible happens to the config file during the loading
     * process.
     */
    private void startup()
    {
        try
        {
            if (this.permissions == ConfigPermissions.READ_WRITE)
                BackupFile.startup(this.path);
        }
        catch (IOException exception)
        {
            NostalgicTweaks.LOGGER.error("[Config Handler] Could not create a startup backup config file\n%s", exception.toString());
        }
    }

    /**
     * Creates a backup file of the current config saved on disk.
     *
     * @return Whether the backup procedure was successful.
     */
    public boolean backup()
    {
        try
        {
            if (this.permissions == ConfigPermissions.READ_WRITE)
                BackupFile.save(this.path);

            return true;
        }
        catch (IOException exception)
        {
            NostalgicTweaks.LOGGER.error("[Config Handler] Could not create a backup config file\n%s", exception);
            return false;
        }
    }

    /**
     * Saves the config currently loaded to disk.
     */
    public void save()
    {
        try
        {
            if (this.permissions == ConfigPermissions.READ_ONLY)
                return;

            this.serializer.write(this.loaded, this.path);
            this.onSave.run();

            NostalgicTweaks.LOGGER.info("[Config Handler] Successfully saved %s", this.config.getSimpleName());
        }
        catch (LoaderException exception)
        {
            NostalgicTweaks.LOGGER.error("[Config Handler] Failed to save config %s\n%s", this.config.getSimpleName(), exception);
        }
    }

    /**
     * Deserializes the saved config file and runs any post-load instructions. Any deserialization errors, when caught,
     * will force the config file to reset back a default state and an error is printed to the logging console.
     *
     * @return Yields {@code true} if the deserialized config passed validation and successfully loaded. Otherwise, the
     * config is reset back to a default state and yields {@code false}.
     */
    public boolean load()
    {
        try
        {
            this.loaded = this.serializer.read(this.path);

            if (this.loaded == null)
                throw new LoaderException(new Throwable(String.format("[Config Handler] Could not properly read config file (%s)", this.path)));

            this.loaded.validate();
            this.onLoad.run();

            NostalgicTweaks.LOGGER.info("[Config Handler] Successfully loaded %s", this.config.getSimpleName());

            return true;
        }
        catch (LoaderException exception)
        {
            NostalgicTweaks.LOGGER.error("[Config Handler] Failed to load %s\n%s", this.config.getSimpleName(), exception);

            if (this.permissions == ConfigPermissions.READ_WRITE)
            {
                this.reset();
                this.onLoad.run();

                NostalgicTweaks.LOGGER.error("[Config Handler] Config file was reset - see backup file to retrieve deleted data");
            }

            return false;
        }
    }

    /**
     * Resets the current config file to its default state. A backup of the current data that is about to be reset will
     * be created. The reset file will be saved to disk.
     */
    public void reset()
    {
        this.backup();

        this.loaded = this.serializer.getDefault();

        try
        {
            this.loaded.validate();
        }
        catch (LoaderException exception)
        {
            throw new RuntimeException("[Config Handler] Could not create a default config file", exception);
        }

        this.save();
    }
}
