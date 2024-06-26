package mod.adrenix.nostalgic.config.factory;

import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;

public class GsonSerializer<T extends ConfigMeta>
{
    /* Fields */

    private final Class<T> config;

    /* Constructor */

    public GsonSerializer(Class<T> config)
    {
        this.config = config;
    }

    /* Methods */

    /**
     * Writes the current config cache to disk at the given path.
     *
     * @param config A {@link Config} instance that extends {@link ConfigMeta}.
     * @param path   A {@link Path} instance to write to.
     * @throws LoaderException When there is an IO exception.
     */
    public void write(T config, Path path) throws LoaderException
    {
        try
        {
            Files.createDirectories(path.getParent());

            BufferedWriter writer = Files.newBufferedWriter(path);
            GsonFactory.BUILDER.toJson(config, writer);

            writer.close();
        }
        catch (IOException exception)
        {
            throw new LoaderException(exception);
        }
    }

    /**
     * Reads a config file from disk.
     *
     * @param path A {@link Path} instance to read from.
     * @return A new config instance from the values that were stored on disk.
     * @throws LoaderException When there is an IO exception or JSON parse exception.
     */
    public T read(Path path) throws LoaderException
    {
        if (Files.exists(path))
        {
            try
            {
                BufferedReader reader = Files.newBufferedReader(path);
                T config = GsonFactory.BUILDER.fromJson(reader, this.config);

                reader.close();

                return config;
            }
            catch (IOException | JsonParseException exception)
            {
                throw new LoaderException(exception);
            }
        }
        else
            return this.getDefault();
    }

    /**
     * @return Creates a new default config class instance.
     */
    public T getDefault()
    {
        try
        {
            Constructor<T> constructor = this.config.getDeclaredConstructor();
            constructor.setAccessible(true);

            return constructor.newInstance();
        }
        catch (ReflectiveOperationException exception)
        {
            throw new RuntimeException(exception);
        }
    }
}
