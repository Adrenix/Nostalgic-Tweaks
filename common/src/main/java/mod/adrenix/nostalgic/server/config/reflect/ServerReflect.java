package mod.adrenix.nostalgic.server.config.reflect;

import com.mojang.datafixers.util.Pair;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
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
     * @param group Group associated with server tweak.
     * @param key Key that links tweak to the server config.
     * @param value The value to save in the server config cache.
     */
    public static void setConfig(GroupType group, String key, Object value)
    {
        setField(group, ServerConfigCache.getRoot(), key, value);
    }

    /**
     * Get all server tweaks saved in a configuration group.
     * @param group Group associated with server tweak.
     * @return A map of server tweaks associated with the given group type.
     */
    public static HashMap<String, Object> getGroup(GroupType group)
    {
        return fetchFields(group, ServerConfigCache.getRoot());
    }

    /**
     * Private Helpers
     */

    private static Pair<Class<?>, Object> getServerGroupClass(GroupType group, ServerConfig config)
    {
        switch (group)
        {
            // For group types not used by the server, just return the root of the config
            case ROOT, SWING, GUI, SOUND, ANIMATION -> { return new Pair<>(ServerConfig.class, config); }
            case GAMEPLAY -> { return new Pair<>(ServerConfig.Gameplay.class, config.gameplay); }
            case CANDY -> { return new Pair<>(ServerConfig.EyeCandy.class, config.eyeCandy); }
        }

        return new Pair<>(ServerConfig.class, config);
    }

    private static void setField(GroupType group, ServerConfig config, String key, Object value)
    {
        CommonReflect.setFieldHelper(getServerGroupClass(group, config), key, value);
    }

    private static HashMap<String, Object> fetchFields(GroupType group, ServerConfig config)
    {
        return CommonReflect.fetchFieldsHelper(getServerGroupClass(group, config));
    }
}
