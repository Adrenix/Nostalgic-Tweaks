package mod.adrenix.nostalgic.common.config.v2.cache;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.v2.client.ClientConfig;
import mod.adrenix.nostalgic.common.config.v2.server.ServerConfig;
import mod.adrenix.nostalgic.common.config.v2.tweak.Tweak;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public abstract class ConfigReflect
{
    /**
     * Get a field matching a tweak's config key within the given class type.
     * @param classType A class type to search its fields.
     * @param tweak The tweak to get config key data from.
     * @return An optional field instance with the first field name matching the tweak's config key.
     */
    private static Optional<Field> getField(Class<?> classType, Tweak<?> tweak)
    {
        return Arrays.stream(classType.getFields())
            .filter(field -> field.getName().toLowerCase().contains(tweak.getConfigKey()))
            .findFirst()
        ;
    }

    /**
     * Prints out an error message in the console if a field could not be found.
     * @param name The field name that was being searched.
     * @param value The value that was to be applied to the field.
     */
    private static void cannotFindFieldInConfig(String name, Object value)
    {
        NostalgicTweaks.LOGGER.error(String.format("Could not reflect (%s) to apply value (%s)", name, value));
    }

    /**
     * Set a new value to a field and cache the field pointer in the tweak.
     * @param field A field instance to set a new value.
     * @param configInstance A config instance for field setting.
     * @param value The new value to apply to the field.
     * @param tweak The tweak to cache the field pointer in.
     * @throws IllegalAccessException When the field cannot be properly set with the new value.
     */
    private static void setAndCache(Field field, Object configInstance, Object value, Tweak<?> tweak) throws IllegalAccessException
    {
        tweak.setConfigField(field);
        field.set(configInstance, value);
    }

    /**
     * Set a field in the given config instance.
     * @param tweak The tweak to get reflection data from.
     * @param classType The class type to examine for field data.
     * @param configInstance A config class instance to apply the new value to.
     * @param value The new value to apply to the tweak's config field via reflection.
     */
    private static void setField(Tweak<?> tweak, Class<?> classType, Object configInstance, Object value)
    {
        try
        {
            if (tweak.configField().isPresent())
            {
                tweak.configField().get().set(configInstance, value);
                return;
            }

            if (tweak.getContainer().isRoot())
            {
                Optional<Field> rootField = getField(classType, tweak);

                if (rootField.isPresent())
                    setAndCache(rootField.get(), configInstance, value, tweak);
                else
                    cannotFindFieldInConfig(tweak.getCacheKey(), value);

                return;
            }

            Optional<Field> categoryField = Arrays.stream(classType.getFields())
                .filter(field -> field.getType().toString().contains("class"))
                .filter(field -> field.getName().toLowerCase().contains(tweak.getContainer().getCategory().getConfigKey()))
                .findFirst()
            ;

            if (categoryField.isPresent())
            {
                Optional<Field> groupField = getField(categoryField.get().getClass(), tweak);

                if (groupField.isPresent())
                    setAndCache(groupField.get(), configInstance, value, tweak);
                else
                    cannotFindFieldInConfig(tweak.getConfigKey(), value);
            }
            else
                NostalgicTweaks.LOGGER.error(String.format("Could not find category class field (%s)", tweak.getContainer().getCategory().getConfigKey()));
        }
        catch (IllegalArgumentException | IllegalAccessException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Change a client config field value via reflection. This needs to be performed before a config is saved to disk.
     * @param tweak The tweak to get config field data from.
     * @param value The new value to apply.
     */
    public static void setClientField(Tweak<?> tweak, Object value)
    {
        setField(tweak, ClientConfig.class, ConfigCache.client(), value);
    }

    /**
     * Change a server config field value via reflection. This needs to be performed before a config is saved to disk.
     * @param tweak The tweak to get config field data from.
     * @param value The new value to apply.
     */
    public static void setServerField(Tweak<?> tweak, Object value)
    {
        setField(tweak, ServerConfig.class, ConfigCache.server(), value);
    }
}
