package mod.adrenix.nostalgic.server.config.reflect;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.annotation.TweakSide;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.common.config.reflect.TweakCommonCache;
import mod.adrenix.nostalgic.common.config.tweak.ITweak;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Both the client and server use this class. Do not use client code here.
 * The server uses this to compare received values and retrieve needed config data so tweaks can be saved to disk.
 *
 * Client uses this cache as a way to keep in sync with the server. This prevents any client related states being
 * overridden from values received from the server.
 */

public class TweakServerCache<T>
{
    /**
     * This cache keeps a record of server only tweaks reducing the amount of tweaks to cycle through when the server
     * sends current config values to the client.
     *
     * It will reference the default client config to see which tweaks are currently marked as server controlled.
     */

    private static final HashMap<String, TweakServerCache<?>> cache = new HashMap<>();

    private static String generateKey(GroupType group, String key) { return TweakCommonCache.generateKey(group, key); }
    static
    {
        if (NostalgicTweaks.isClient())
        {
            TweakClientCache.all().forEach((id, tweak) -> {
                if (!tweak.isClientSide() || tweak.isDynamic())
                    cache.put(id, new TweakServerCache<>(tweak.getGroup(), tweak.getKey(), tweak.getSavedValue()));
            });
        }
        else
        {
            Arrays.stream(GroupType.values()).forEach((group) ->
                ServerReflect.getGroup(group).forEach((key, value) ->
                {
                    if (CommonReflect.getAnnotation(group, key, TweakSide.Ignore.class) == null)
                    {
                        TweakSide.Server server = CommonReflect.getAnnotation(group, key, TweakSide.Server.class);
                        TweakSide.Dynamic dynamic = CommonReflect.getAnnotation(group, key, TweakSide.Dynamic.class);

                        if (server != null || dynamic != null)
                            cache.put(generateKey(group, key), new TweakServerCache<>(group, key, value));
                    }
                })
            );
        }
    }

    public static HashMap<String, TweakServerCache<?>> all() { return cache; }

    @SuppressWarnings("unchecked") // Since groups and keys are unique to tweaks, their returned type is assured.
    public static <T> TweakServerCache<T> get(GroupType group, String key)
    {
        return (TweakServerCache<T>) cache.get(generateKey(group, key));
    }

    @SuppressWarnings("unchecked") // Since groups and keys are unique to tweaks, their returned type is assured.
    public static <T> TweakServerCache<T> get(ITweak tweak)
    {
        if (tweak.getServerCache() == null)
            tweak.setServerCache(get(tweak.getGroup(), tweak.getKey()));
        return (TweakServerCache<T>) tweak.getServerCache();
    }

    /**
     * All tweak caches have a group and a unique key within that group.
     * @see mod.adrenix.nostalgic.client.config.ClientConfig
     */

    private final String key;
    private final GroupType group;
    private StatusType status;

    /**
     * This field is used by both the client and server.
     *
     * The client changes this field at will by the user in the config menu.
     * It will not always be in sync with the server.
     *
     * The server uses this field to ensure values received from clients matches this cache.
     */
    private T value;

    /**
     * This field is only used by the client.
     *
     * It saves the current state last received by the server. This should be used by the client when it seeks to find
     * the current state of a tweak set by the server.
     */
    private T server;

    /**
     * Server tweaks are created once and saved in the cache.
     * Use the cache to retrieve tweaks by their group and key identification.
     *
     * @param group Group associated with this tweak (e.g. CandyTweak).
     * @param key The unique key of a tweak. Must match what is saved on disk.
     * @param value The value of a tweak. Can be an Enum, boolean, String, int, etc.
     */
    private TweakServerCache(GroupType group, String key, T value)
    {
        this.group = group;
        this.key = key;
        this.value = value;
        this.server = value;
        this.status = StatusType.FAIL;

        TweakSide.EntryStatus status = CommonReflect.getAnnotation(group, key, TweakSide.EntryStatus.class);
        if (status != null)
            this.status = status.status();
    }

    public GroupType getGroup() { return this.group; }
    public String getKey() { return this.key; }
    public T getValue() { return this.value; }
    public T getServerCache() { return this.server; }

    /**
     * Some server tweaks may be marked as dynamic. Ultimately, the server will decide what the state of these tweaks
     * should be. If a client is connected to a server without N.T., then the client will take over the state.
     * @return Whether the tweak is annotated with a dynamic side.
     */
    public boolean isDynamic()
    {
        return CommonReflect.getAnnotation(this, TweakSide.Dynamic.class) != null;
    }

    /**
     * The status of a tweak is updated when its code is executed.
     * @see mod.adrenix.nostalgic.common.config.reflect.StatusType
     * @return Whether a tweak has failed to load, has not attempted to load, or is loaded.
     */
    public StatusType getStatus() { return this.status; }

    /**
     * Can be set anywhere and updated at anytime.
     * @see mod.adrenix.nostalgic.common.config.reflect.StatusType
     * @param status The current status of a tweak.
     */
    public void setStatus(StatusType status) { this.status = status; }

    /**
     * Sets the value of the server cache. Only the client should be using this method.
     * @param value Any object - will be class checked.
     */
    @SuppressWarnings("unchecked") // Check if value received from server matches client cached value
    public void setValue(Object value)
    {
        if (value.getClass().equals(this.value.getClass()))
            this.value = (T) value;
        else
        {
            String info = String.format
            (
                "Unable to update value for %s since the received value was (%s). Expected (%s)",
                generateKey(this.group, this.key),
                value,
                this.value
            );

            NostalgicTweaks.LOGGER.warn(info);
        }
    }

    /**
     * Sets the current state last received by the server. Only the client should be using this method.
     * @param value Any object - will be class checked.
     */
    @SuppressWarnings("unchecked") // Check if value received from server matches client cached value
    public void setServerCache(Object value)
    {
        if (value.getClass().equals(this.server.getClass()))
            this.server = (T) value;
        else
        {
            String info = String.format
            (
                "Unable to update server cache for %s since the received value was (%s). Expected (%s)",
                generateKey(this.group, this.key),
                value,
                this.value
            );

            NostalgicTweaks.LOGGER.warn(info);
        }
    }
}
