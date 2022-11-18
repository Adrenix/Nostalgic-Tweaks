package mod.adrenix.nostalgic.server.config.reflect;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.annotation.TweakSide;
import mod.adrenix.nostalgic.common.config.reflect.CommonReflect;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.common.config.reflect.TweakCommonCache;
import mod.adrenix.nostalgic.common.config.tweak.Tweak;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Both the client and server use this class. Do not use client code here.
 * The server uses this to compare received values and retrieve needed config data so tweaks can be saved to disk.
 *
 * Client uses this cache as a way to keep in sync with the server. This prevents any client related states being
 * overridden from values received from the server.
 */

public class TweakServerCache<T> extends TweakCommonCache
{
    /**
     * This cache keeps a record of server only tweaks reducing the amount of tweaks to cycle through when the server
     * sends current config values to the client. Dynamic tweaks will also be kept in this cache.
     *
     * It will reference the default client config to see which tweaks are currently marked as server controlled.
     */

    private static final HashMap<String, TweakServerCache<?>> CACHE = new HashMap<>();

    static
    {
        if (NostalgicTweaks.isClient())
        {
            TweakClientCache.all().forEach((id, tweak) ->
            {
                if (!tweak.isClient() || tweak.isDynamic())
                    CACHE.put(id, new TweakServerCache<>(tweak.getGroup(), tweak.getKey(), tweak.getSavedValue()));
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
                            CACHE.put(generateKey(group, key), new TweakServerCache<>(group, key, value));
                    }
                })
            );
        }
    }

    /**
     * Get a hash map of all server-only tweaks.
     * @return A map of tweak keys to their server cached value.
     */
    public static HashMap<String, TweakServerCache<?>> all() { return CACHE; }

    /**
     * Get a server-side tweak. This should <b>only</b> be used if a tweak enumeration is not available.
     * For the best performance, use {@link TweakServerCache#get(Tweak)} since it retrieves cached hashmap pointers.
     * @param group The group a tweak is associated with.
     * @param key The key used to identify the tweak.
     * @return The current tweak value kept in the cache.
     * @param <T> The type associated with the tweak.
     */
    @SuppressWarnings("unchecked") // Since groups and keys are unique to tweaks and asserted, their returned type is assured.
    public static <T> TweakServerCache<T> get(GroupType group, String key)
    {
        return (TweakServerCache<T>) CACHE.get(generateKey(group, key));
    }

    /**
     * An overload method for {@link TweakServerCache#get(GroupType, String)}. This should be the primary way of
     * retrieving cached tweak values. When each tweak loads, a pointer is cached in the tweak's enumeration instance.
     * This method will use that pointer instead of looping through the hashmap to get a tweak's value.
     * @param tweak The tweak to fetch from cache.
     * @return The current value kept in the server tweak cache.
     * @param <T> The type associated with the tweak.
     */
    @SuppressWarnings("unchecked") // Since groups and keys are unique to tweaks, their returned type is assured.
    public static <T> TweakServerCache<T> get(Tweak tweak)
    {
        if (tweak.getSide() == NostalgicTweaks.Side.CLIENT)
            return null;

        if (tweak.getServerCache() == null)
            tweak.setServerCache(get(tweak.getGroup(), tweak.getKey()));

        return (TweakServerCache<T>) tweak.getServerCache();
    }

    /* Fields */

    /**
     * Caches the dynamic annotation status of each tweak. Since this metadata never changes, it is best to cache the
     * known values than constantly using reflection to find metadata.
     */
    private final boolean isAnnotatedDynamic;

    /**
     * This field is used by both the client and server.
     *
     * The client can change this field within the config menu.
     * Therefore, this field will not always be in sync with the server.
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

    /* Constructor */

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
        super(group, key);

        this.value = value;
        this.server = value;
        this.isAnnotatedDynamic = this.isMetadataPresent(TweakSide.Dynamic.class);
    }

    /* Methods */

    /**
     * Get the value used by both the client and server. The client can change this field within the config menu.
     * Therefore, this field will not always be in sync with the server.
     * @return The current (possibly not synced) value for this server tweak.
     */
    public T getValue() { return this.value; }

    /**
     * Get the current state last received by the server. This should be used by the client when it seeks to find the
     * current state of a tweak set by the server.
     * @return A value only used by the client.
     */
    public T getServerCache() { return this.server; }

    /**
     * Some server tweaks may be marked as dynamic. Ultimately, the server will decide what the state of these tweaks
     * should be. If a client is connected to a server without N.T., then the client will take over the state.
     * @return Whether the tweak is annotated with a dynamic side.
     */
    public boolean isDynamic() { return this.isAnnotatedDynamic; }

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
