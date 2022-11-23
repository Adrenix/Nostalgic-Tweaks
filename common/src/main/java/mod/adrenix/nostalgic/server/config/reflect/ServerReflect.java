package mod.adrenix.nostalgic.server.config.reflect;

import com.mojang.datafixers.util.Pair;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;
import mod.adrenix.nostalgic.server.config.ServerConfig;
import mod.adrenix.nostalgic.server.config.ServerConfigCache;

import java.util.HashMap;

/**
 * This is the interface that lets the server tweak cache values be applied to mixins and be saved on disk.
 * Not all groups will be available to the server config, and client only groups should never be referenced.
 * Since some groups will have both client and server only tweaks, it is necessary that both sides use the same group types.
 *
 * @see mod.adrenix.nostalgic.common.config.reflect.CommonReflect
 * @see mod.adrenix.nostalgic.client.config.reflect.ClientReflect
 */

public abstract class ServerReflect
{
    /**
     * Updates the server config cache. To save to disk use {@link me.shedaniel.autoconfig.ConfigHolder#save()}.
     * @param group A group type associated with the server tweak.
     * @param key Key that links tweak to the server config.
     * @param value The value to save in the server config cache.
     */
    public static void setConfig(TweakGroup group, String key, Object value)
    {
        setField(group, ServerConfigCache.getRoot(), key, value);
    }

    /**
     * Get all server tweaks saved in a configuration group.
     * @param group A group type.
     * @return A map of server tweaks associated with the given group type.
     */
    public static HashMap<String, Object> getGroup(TweakGroup group)
    {
        return fetchFields(group, ServerConfigCache.getRoot());
    }

    /*
       Private Helpers
     */

    /**
     * Get a data pair that holds a server config subclass type and a server config subclass instance.
     * @param group A group type.
     * @param config A server config instance.
     * @return A data pair with a class type and its associated instance.
     */
    private static Pair<Class<?>, Object> getServerGroupClass(TweakGroup group, ServerConfig config)
    {
        switch (group)
        {
            // For group types not used by the server, just return the root of the config
            case ROOT, SWING, GUI, SOUND -> { return new Pair<>(ServerConfig.class, config); }
            case ANIMATION -> { return new Pair<>(ServerConfig.Animation.class, config.animation); }
            case GAMEPLAY -> { return new Pair<>(ServerConfig.Gameplay.class, config.gameplay); }
            case CANDY -> { return new Pair<>(ServerConfig.EyeCandy.class, config.eyeCandy); }
        }

        return new Pair<>(ServerConfig.class, config);
    }

    /**
     * Set a field in the server config.
     * @param group The group type that is associated with the key.
     * @param config A server config instance.
     * @param key The key that links the tweak to the config.
     * @param value The value to put in the field.
     */
    private static void setField(TweakGroup group, ServerConfig config, String key, Object value)
    {
        CommonReflect.setFieldHelper(getServerGroupClass(group, config), key, value);
    }

    /**
     * Get a hash map of fields associated with a group type and the server config.
     * @param group A group type.
     * @param config A server config instance.
     * @return A hash map of fields from the given group type from the server config.
     */
    private static HashMap<String, Object> fetchFields(TweakGroup group, ServerConfig config)
    {
        return CommonReflect.fetchFieldsHelper(getServerGroupClass(group, config));
    }
}
