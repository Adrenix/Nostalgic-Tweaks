package mod.adrenix.nostalgic.client.config.reflect;

import com.mojang.datafixers.util.Pair;
import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * This is the interface that lets the client tweak cache values be applied to mixins and be saved on disk.
 * All configuration values (server included) are accessible here since each tweak is to work in all play environments.
 *
 * @see mod.adrenix.nostalgic.common.config.reflect.CommonReflect
 * @see mod.adrenix.nostalgic.server.config.reflect.ServerReflect
 */

public abstract class ClientReflect
{
    /**
     * Updates the config cache. To save to disk use {@link me.shedaniel.autoconfig.ConfigHolder#save()}.
     * @param group Group associated with tweak.
     * @param key The key that links the tweak to the config.
     * @param value The value to save in the config cache.
     */
    public static void setConfig(GroupType group, String key, Object value)
    {
        setField(group, ClientConfigCache.getRoot(), key, value);
    }

    /**
     * Get all tweaks saved in a configuration group.
     * @param group The group to get tweaks from.
     * @return A map of tweaks associated with given group type.
     */
    public static HashMap<String, Object> getGroup(GroupType group)
    {
        return fetchFields(group, ClientConfigCache.getRoot());
    }

    /**
     * Get the default value of a tweak.
     * @param group Group associated with tweak.
     * @param key The key that links the tweak to the config.
     * @param <T> Class associated with tweak.
     * @return The default value associated with the given tweak identifiers.
     */
    public static <T> T getDefault(GroupType group, String key)
    {
        return getFieldValue(group, key, CommonReflect.DEFAULT_CONFIG);
    }

    /**
     * Get the current tweak value that is saved on disk.
     * @param group Group associated with tweak.
     * @param key The key that links the tweak to the config.
     * @param <T> Class associated with tweak.
     * @return The current value saved on disk.
     */
    public static <T> T getCurrent(GroupType group, String key)
    {
        return getFieldValue(group, key, ClientConfigCache.getRoot());
    }

    /**
     * Private Helpers
     */

    private static <T> T getFieldValue(GroupType group, String key, ClientConfig config)
    {
        return findField(group, config, key);
    }

    @SuppressWarnings("unchecked") // Keys are guaranteed to find a config value
    private static <T> T findField(GroupType group, ClientConfig config, String key)
    {
        Pair<Class<?>, Object> groupClass = CommonReflect.getGroupClass(group, config);
        Class<?> reference = groupClass.getFirst();
        Object instance = groupClass.getSecond();

        for (Field field : reference.getFields())
        {
            try
            {
                if (key.equals(field.getName()))
                    return (T) field.get(instance);
            }
            catch (IllegalArgumentException | IllegalAccessException e) { e.printStackTrace(); }
        }

        return null;
    }

    private static void setField(GroupType group, ClientConfig config, String key, Object value)
    {
        CommonReflect.setFieldHelper(CommonReflect.getGroupClass(group, config), key, value);
    }

    private static HashMap<String, Object> fetchFields(GroupType group, ClientConfig config)
    {
        return CommonReflect.fetchFieldsHelper(CommonReflect.getGroupClass(group, config));
    }
}
