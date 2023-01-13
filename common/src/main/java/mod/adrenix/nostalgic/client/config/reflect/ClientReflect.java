package mod.adrenix.nostalgic.client.config.reflect;

import mod.adrenix.nostalgic.client.config.ClientConfigCache;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;

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
     * Updates the config cache. To save to disk use {@link mod.adrenix.nostalgic.common.config.auto.ConfigHolder#save()}.
     * @param group The group type that is associated with a tweak.
     * @param key The key that links the tweak to the config.
     * @param value The value to save in the config cache.
     */
    public static void setConfig(TweakGroup group, String key, Object value)
    {
        setField(group, ClientConfigCache.getRoot(), key, value);
    }

    /**
     * Get all tweaks saved in a configuration group.
     * @param group The group to get tweaks from.
     * @return A map of tweaks associated with given group type.
     */
    public static HashMap<String, Object> getGroup(TweakGroup group)
    {
        return fetchFields(group, ClientConfigCache.getRoot());
    }

    /**
     * Get the default value of a tweak.
     * @param group The group type that is associated with the key.
     * @param key The key that links the tweak to the config.
     * @param <T> Class associated with tweak.
     * @return The default value associated with the given tweak identifiers.
     */
    public static <T> T getDefault(TweakGroup group, String key)
    {
        return getFieldValue(group, key, CommonReflect.DEFAULT_CONFIG);
    }

    /**
     * Get the current tweak value that is saved on disk.
     * @param group The group type that is associated with the key.
     * @param key The key that links the tweak to the config.
     * @param <T> Class associated with tweak.
     * @return The current value saved on disk.
     */
    public static <T> T getCurrent(TweakGroup group, String key)
    {
        return getFieldValue(group, key, ClientConfigCache.getRoot());
    }

    /*
       Private Helpers
     */

    /**
     * Find a field value from the config.
     * @param group The group type that is associated with the key.
     * @param key The key that links the tweak to the config.
     * @param config A client config instance.
     * @param <T> The expected type associated with the field.
     * @return A value retrieved from a field.
     */
    private static <T> T getFieldValue(TweakGroup group, String key, ClientConfig config)
    {
        return CommonReflect.getFieldHelper(CommonReflect.getGroupClass(group, config), key);
    }

    /**
     * Set a field in the client config.
     * @param group The group type that is associated with the key.
     * @param config A client config instance.
     * @param key The key that links the tweak to the config.
     * @param value The value to put in the field.
     */
    private static void setField(TweakGroup group, ClientConfig config, String key, Object value)
    {
        CommonReflect.setFieldHelper(CommonReflect.getGroupClass(group, config), key, value);
    }

    /**
     * Get a hash map of fields associated with a group type and the client config.
     * @param group A group type.
     * @param config A client config instance.
     * @return A hash map of fields from the given group type from the client config.
     */
    private static HashMap<String, Object> fetchFields(TweakGroup group, ClientConfig config)
    {
        return CommonReflect.fetchFieldsHelper(CommonReflect.getGroupClass(group, config));
    }
}
