package mod.adrenix.nostalgic.common.config.reflect;

import com.mojang.datafixers.util.Pair;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.common.config.annotation.TweakData;
import org.jetbrains.annotations.CheckReturnValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * This reflection helper class is used by both the server and client.
 * Do not use any vanilla client code. Accessing the client config from here is safe.
 *
 * @see mod.adrenix.nostalgic.client.config.reflect.ClientReflect
 * @see mod.adrenix.nostalgic.server.config.reflect.ServerReflect
 */

public abstract class CommonReflect
{
    /**
     * Although this config is not used by the server, it used to determine which tweaks are controlled by the server.
     * The only manual requirement is adding the server tweaks to the server config itself.
     */
    public static final ClientConfig DEFAULT_CONFIG = new ClientConfig();

    /**
     * Get a class/subclass reference from the client config.
     * @param group A group type.
     * @param config A client config instance.
     * @return A data pair with a class/subclass type on the left and a client config instance on the right.
     */
    public static Pair<Class<?>, Object> getGroupClass(TweakGroup group, ClientConfig config)
    {
        switch (group)
        {
            case ROOT -> { return new Pair<>(ClientConfig.class, config); }
            case SOUND -> { return new Pair<>(ClientConfig.Sound.class, config.sound); }
            case CANDY -> { return new Pair<>(ClientConfig.EyeCandy.class, config.eyeCandy); }
            case GAMEPLAY -> { return new Pair<>(ClientConfig.Gameplay.class, config.gameplay); }
            case ANIMATION -> { return new Pair<>(ClientConfig.Animation.class, config.animation); }
            case SWING -> { return new Pair<>(ClientConfig.Swing.class, config.swing); }
            case GUI -> { return new Pair<>(ClientConfig.Gui.class, config.gui); }
        }

        return new Pair<>(ClientConfig.class, config);
    }

    /**
     * Helper method for setting a field in a given class instance.
     * @param groupClass A data pair with a class type on the left and a class instance on the right.
     * @param key A config key that is available in the given class instance.
     * @param value The value to store in the config class instance.
     */
    public static void setFieldHelper(Pair<Class<?>, Object> groupClass, String key, Object value)
    {
        Class<?> reference = groupClass.getFirst();
        Object instance = groupClass.getSecond();

        for (Field field : reference.getFields())
        {
            try
            {
                if (field.getName().equals(key))
                {
                    field.set(instance, value);
                    break;
                }
            }
            catch (IllegalArgumentException | IllegalAccessException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Helper method for getting a field within a given class instance.
     * @param groupClass A data pair with a class type on the left and a class instance on the right.
     * @param key A config key that is available in the given class instance.
     * @return The value stored in the class instance at the given key.
     */
    @SuppressWarnings("unchecked") // Keys are guaranteed to find a config value
    public static <T> T getFieldHelper(Pair<Class<?>, Object> groupClass, String key)
    {
        Class<?> reference = groupClass.getFirst();
        Object instance = groupClass.getSecond();

        for (Field field : reference.getFields())
        {
            try
            {
                if (key.equals(field.getName()))
                    return (T) field.get(instance);
            }
            catch (IllegalArgumentException | IllegalAccessException exception)
            {
                exception.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Get an annotation attached to a config tweak.
     * @param group A group type.
     * @param key Key that matches both tweak and config class field.
     * @param annotation The annotation class to look for.
     * @param <T> A class that extends the Annotation interface.
     * @return The annotation if it was found.
     */
    @CheckReturnValue
    public static <T extends Annotation> T getAnnotation(TweakGroup group, String key, Class<T> annotation)
    {
        return getFieldAnnotation(group, key, annotation);
    }

    /**
     * Overload method for {@link CommonReflect#getAnnotation(TweakGroup, String, Class)}.
     * @param cache A cached entry from {@link TweakCommonCache}.
     * @param annotation An annotation class to check for.
     * @param <T> The class type of annotation.
     * @return An annotation instance if it was found.
     */
    @CheckReturnValue
    public static <T extends Annotation> T getAnnotation(TweakCommonCache cache, Class<T> annotation)
    {
        return getAnnotation(cache.getGroup(), cache.getKey(), annotation);
    }

    /**
     * Helper for public annotation retriever method.
     * @param group A group type.
     * @param key Key that matches both tweak and config class field.
     * @param annotation The annotation class to look for.
     * @param <T> A class that extends the Annotation interface.
     * @return The annotation if it was found.
     */
    @CheckReturnValue
    private static <T extends Annotation> T getFieldAnnotation(TweakGroup group, String key, Class<T> annotation)
    {
        Pair<Class<?>, Object> groupClass = getGroupClass(group, DEFAULT_CONFIG);
        Class<?> reference = groupClass.getFirst();

        for (Field field : reference.getFields())
        {
            if (field.getName().equals(key))
                return field.getAnnotation(annotation);
        }

        return null;
    }

    /**
     * Helper method for getting fields in a class instance.
     * @param groupClass A data pair with a class type on the left and a class instance on the right.
     * @return A map of tweaks that was found in the class/subclass.
     */
    public static HashMap<String, Object> fetchFieldsHelper(Pair<Class<?>, Object> groupClass)
    {
        Class<?> reference = groupClass.getFirst();
        Object instance = groupClass.getSecond();
        HashMap<String, Object> fields = new HashMap<>();

        for (Field field : reference.getFields())
        {
            // Don't include fields that should be ignored
            if (field.getAnnotation(TweakData.Ignore.class) != null)
                continue;

            try
            {
                fields.put(field.getName(), field.get(instance));
            }
            catch (IllegalArgumentException | IllegalAccessException exception)
            {
                exception.printStackTrace();
            }
        }

        return fields;
    }
}
