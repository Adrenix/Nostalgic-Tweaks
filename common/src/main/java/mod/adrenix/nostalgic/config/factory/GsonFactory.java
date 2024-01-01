package mod.adrenix.nostalgic.config.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class GsonFactory
{
    /**
     * This Gson builder defines how the config is serialized/deserialized. Map-based tweaks extend linked hash maps,
     * and set-based tweaks extend linked hash sets.
     */
    static final Gson BUILDER = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Create a Gson config serializer that handles custom mod data.
     *
     * @param config A config class instance.
     * @param <T>    The class type of the config.
     * @return A new {@link GsonSerializer} instance.
     */
    public static <T extends ConfigMeta> GsonSerializer<T> create(Class<T> config)
    {
        return new GsonSerializer<>(config);
    }
}
