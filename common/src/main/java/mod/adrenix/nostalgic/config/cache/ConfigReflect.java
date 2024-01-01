package mod.adrenix.nostalgic.config.cache;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.config.ClientConfig;
import mod.adrenix.nostalgic.config.ServerConfig;
import mod.adrenix.nostalgic.config.factory.ConfigMeta;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.util.common.data.Pair;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public abstract class ConfigReflect
{
    /**
     * Change a client config field value via reflection. This needs to be performed before a config is saved to disk.
     *
     * @param tweak The tweak to get config field data from.
     * @param value The new value to apply.
     */
    public static void setClientField(Tweak<?> tweak, Object value)
    {
        setField(tweak, ClientConfig.class, ConfigCache.client(), value);
    }

    /**
     * Change a server config field value via reflection. This needs to be performed before a config is saved to disk.
     *
     * @param tweak The tweak to get config field data from.
     * @param value The new value to apply.
     */
    public static void setServerField(Tweak<?> tweak, Object value)
    {
        setField(tweak, ServerConfig.class, ConfigCache.server(), value);
    }

    /**
     * Change a config field value via reflection using a custom {@link ConfigMeta} instance. Reflection needs to be
     * performed before a config instance is saved to disk.
     *
     * @param classType      The class type of the config instance.
     * @param configInstance The custom config to reflect onto.
     * @param tweak          The tweak to get config field data from.
     * @param value          The new value to apply.
     * @param <M>            The class type must extend {@link ConfigMeta}.
     */
    public static <M extends ConfigMeta> void setManualField(Class<M> classType, M configInstance, Tweak<?> tweak, Object value)
    {
        setField(tweak, classType, configInstance, value);
    }

    /**
     * Set a tweak's value using the sided {@link ConfigMeta} stored in runtime memory. Any changes made to the sided
     * config instance must be done before performing reflection operations.
     *
     * @param tweak The tweak to sync with config data.
     */
    public static void syncTweak(Tweak<?> tweak)
    {
        if (NostalgicTweaks.isClient())
            setTweak(tweak, ClientConfig.class, ConfigCache.client());
        else
            setTweak(tweak, ServerConfig.class, ConfigCache.server());
    }

    /**
     * Get a field matching a tweak's config key within the given class type.
     *
     * @param classType A class type to search its fields.
     * @param tweak     The tweak to get config key data from.
     * @return An optional field instance with the first field name matching the tweak's config key.
     */
    private static Optional<Field> getFieldFromClass(Class<?> classType, Tweak<?> tweak)
    {
        return Arrays.stream(classType.getFields())
            .filter(field -> field.getName().contains(tweak.getJsonId()))
            .findFirst();
    }

    /**
     * Get a subclass field using the given parent class.
     *
     * @param classType The class type to examine for field data.
     * @param tweak     The tweak to get reflection data from.
     * @return An optional field instance.
     */
    private static Optional<Field> getFieldFromSubclass(Class<?> classType, Tweak<?> tweak)
    {
        String categoryKey = tweak.getCategory().getJsonId().toLowerCase(Locale.ROOT);

        return Arrays.stream(classType.getFields())
            .filter(field -> field.getType().toString().contains("class"))
            .filter(field -> field.getName().toLowerCase(Locale.ROOT).contains(categoryKey))
            .findFirst();
    }

    /**
     * Prints out an error message in the console if a field could not be found.
     *
     * @param name  The field name that was being searched.
     * @param value The value that was to be applied to the field.
     */
    private static void cannotFindFieldInConfig(String name, Object value)
    {
        if (value != null)
            NostalgicTweaks.LOGGER.error("Could not find (%s) to apply value (%s)", name, value);
        else
            NostalgicTweaks.LOGGER.error("Could not find (%s)", name);
    }

    /**
     * Find a field and its parent class based on the given tweak and {@link ConfigMeta} class.
     *
     * @param tweak          The tweak to get reflection data from.
     * @param classType      The class type of the config instance.
     * @param configInstance A config class instance.
     * @param <M>            The class type must extend {@link ConfigMeta}.
     * @return A {@link Pair} with an {@link Optional} {@link Field} on the left and the field's {@link Object class}.
     */
    public static <M extends ConfigMeta> Pair<Optional<Field>, Object> findField(Tweak<?> tweak, Class<M> classType, Object configInstance)
    {
        if (tweak.getContainer().isRoot())
            return new Pair<>(getFieldFromClass(classType, tweak), configInstance);

        Optional<Field> fromCategory = getFieldFromSubclass(classType, tweak);

        if (fromCategory.isPresent())
        {
            Field field = fromCategory.get();

            try
            {
                return new Pair<>(getFieldFromClass(field.getType(), tweak), field.get(configInstance));
            }
            catch (IllegalAccessException exception)
            {
                NostalgicTweaks.LOGGER.error("Could not access subclass field (%s) with parent class (%s)", field, configInstance);
            }
        }

        return new Pair<>(Optional.empty(), configInstance);
    }

    /**
     * Get the value stored in a field.
     *
     * @param tweak          The tweak to get reflection data from.
     * @param classType      The class type of the config instance.
     * @param configInstance A config class instance.
     * @param <M>            The class type must extend {@link ConfigMeta}.
     * @return A {@code nullable} {@link Object} found in the field.
     */
    public static <M extends ConfigMeta> @Nullable Object getFieldValue(Tweak<?> tweak, Class<M> classType, Object configInstance)
    {
        Pair<Optional<Field>, Object> pair = findField(tweak, classType, configInstance);
        Optional<Field> found = pair.left();
        Object parentClass = pair.right();

        if (found.isEmpty())
        {
            cannotFindFieldInConfig(tweak.getJsonId(), null);
            return null;
        }

        Field field = found.get();

        try
        {
            return field.get(parentClass);
        }
        catch (IllegalAccessException exception)
        {
            NostalgicTweaks.LOGGER.error("Could not access field (%s)\n%s", field, exception);
            return null;
        }
    }

    /**
     * Set a field in the given config instance.
     *
     * @param tweak          The tweak to get reflection data from.
     * @param classType      The class type of the config instance.
     * @param configInstance A config class instance to apply the new value to.
     * @param value          The new value to apply to the tweak's config field via reflection.
     * @param <M>            The class type must extend {@link ConfigMeta}.
     */
    private static <M extends ConfigMeta> void setField(Tweak<?> tweak, Class<M> classType, Object configInstance, Object value)
    {
        Pair<Optional<Field>, Object> pair = findField(tweak, classType, configInstance);
        Optional<Field> found = pair.left();
        Object parentClass = pair.right();

        if (found.isEmpty())
        {
            cannotFindFieldInConfig(tweak.getJsonId(), value);
            return;
        }

        Field field = found.get();

        try
        {
            field.set(parentClass, value);
        }
        catch (IllegalAccessException exception)
        {
            NostalgicTweaks.LOGGER.error("Could not access field (%s)\n%s", field, exception);
        }
    }

    /**
     * Set the value of a tweak using the given config instance.
     *
     * @param tweak          The tweak to get reflection data from.
     * @param classType      The class type of the config instance.
     * @param configInstance A config class instance to apply the new value to.
     * @param <M>            The class type must extend {@link ConfigMeta}.
     * @param <T>            The class type of the tweak's metadata.
     */
    private static <T, M extends ConfigMeta> void setTweak(Tweak<T> tweak, Class<M> classType, Object configInstance)
    {
        Pair<Optional<Field>, Object> pair = findField(tweak, classType, configInstance);
        Optional<Field> found = pair.left();
        Object parentClass = pair.right();

        if (found.isEmpty())
        {
            cannotFindFieldInConfig(tweak.getJsonId(), null);
            return;
        }

        Field field = found.get();

        try
        {
            // noinspection unchecked
            tweak.setDisk((T) field.get(parentClass));
        }
        catch (IllegalAccessException exception)
        {
            NostalgicTweaks.LOGGER.error("Could not access field (%s)\n%s", field, exception);
        }
    }
}
