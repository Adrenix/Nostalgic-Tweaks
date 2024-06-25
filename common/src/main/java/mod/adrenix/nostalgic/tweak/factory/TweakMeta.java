package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.config.cache.CacheHolder;
import mod.adrenix.nostalgic.config.cache.CacheMode;
import mod.adrenix.nostalgic.config.cache.ConfigCache;
import mod.adrenix.nostalgic.network.packet.tweak.TweakPacket;
import mod.adrenix.nostalgic.tweak.TweakValidator;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * This interface provides implementation details for a tweak's stored metadata. This interface provides the required
 * methods for manipulating the metadata, saving the metadata, and sending/receiving metadata.
 *
 * @param <T> The class type of the data stored in the tweak.
 */
public interface TweakMeta<T>
{
    /**
     * Register a tweak by defining its config json identifier. This method will add the tweak into the mod's
     * {@link TweakPool}. Additionally, this must be used in the config structure classes so that changes in the tweak's
     * json identifier are reflected onto the {@link TweakPool}.
     *
     * @param jsonId The config json key that points to this tweak.
     * @return The tweak's default value.
     */
    T register(String jsonId);

    /**
     * The value that is returned will be a value that relates to the current game context. For example, if this is a
     * server side tweak, then this method will return its disabled value if the player is not connected to a server
     * with Nostalgic Tweaks installed.
     * <p>
     * This should be used <b color=red>at all times</b> when getting a tweak value within mod logic. Avoid using other
     * tweak value getters unless you understand what is being returned by those methods.
     *
     * @return A tweak value based on the current game state.
     * @see #fromDisk() Get what is currently saved on disk
     * @see #fromCache() Get what is currently saved based on cache mode
     * @see #fromServer() Get the value sent by a server
     */
    T get();

    /**
     * Get the value saved on disk. This will return the default value if the value for this tweak has not yet been
     * set.
     *
     * @return The value saved on disk.
     */
    T fromDisk();

    /**
     * Get the value received from a server with Nostalgic Tweaks installed.
     *
     * @return A value sent from a server with the mod installed.
     */
    T fromServer();

    /**
     * Get the temporary cache value stored in this tweak based on the current cache mode.
     *
     * @return A config cache value based on the logical side chosen within the tweak's menu row.
     */
    default T fromCache()
    {
        return this.getCacheHolder().get();
    }

    /**
     * Get a saved value based on the current cache mode. If in local mode, the disk value is returned. If in network
     * mode, the network value is returned.
     *
     * @return A saved value based on the logical side chose within the tweak's menu row.
     */
    default T fromMode()
    {
        return this.isLocalMode() ? this.fromDisk() : this.fromServer();
    }

    /**
     * Get the cache value stored locally.
     *
     * @return A config cache value for the client.
     */
    default T fromLocal()
    {
        return this.getCacheHolder().getLocal();
    }

    /**
     * Set the cache value stored locally.
     *
     * @param value The value to put into the local cache.
     */
    default void setLocal(T value)
    {
        this.getCacheHolder().setLocal(value);
    }

    /**
     * Get the cache value stored for the network.
     *
     * @return A config cache value to be sent to the server.
     */
    default T fromNetwork()
    {
        return this.getCacheHolder().getNetwork();
    }

    /**
     * Set the cache value stored for the network.
     *
     * @param value The value to put into the network cache that will be sent to the server.
     */
    default void setNetwork(T value)
    {
        this.getCacheHolder().setNetwork(value);
    }

    /**
     * Only server-side and dynamic tweaks will make use of {@link CacheMode}. The cache mode determines which sided
     * cache value is read/written to. The cache value returned by {@link TweakMeta#fromCache()} and the cache value set
     * by {@link TweakMeta#setCacheValue(Object)} changes based on the current cache mode.
     *
     * @return A {@link CacheMode} enumeration value.
     */
    CacheMode getCacheMode();

    /**
     * Manually change the cache mode.
     *
     * @param cacheMode A {@link CacheMode} enumeration value.
     */
    void setCacheMode(CacheMode cacheMode);

    /**
     * @return Whether the tweak's {@link CacheMode} is in {@code LOCAL} mode.
     */
    default boolean isLocalMode()
    {
        return this.getCacheMode() == CacheMode.LOCAL;
    }

    /**
     * @return Whether the tweak's {@link CacheMode} is in {@code NETWORK} mode.
     */
    default boolean isNetworkMode()
    {
        return this.getCacheMode() == CacheMode.NETWORK;
    }

    /**
     * Functional shortcut for toggling the cache mode. So if the mode is to {@code LOCAL}, then it will change to
     * {@code NETWORK} and vice versa.
     */
    default void toggleCacheMode()
    {
        if (this.isLocalMode())
            this.setCacheMode(CacheMode.NETWORK);
        else
            this.setCacheMode(CacheMode.LOCAL);
    }

    /**
     * A {@link CacheHolder} is used by the client-side config user interface so that changes can be made to both a
     * client-side and server-side cache value. When changes are saved, the client values are saved to disk and any
     * changes to server values are sent to the server.
     *
     * @return A {@link CacheHolder} that stores the config user interface cached value changes for client/server.
     */
    CacheHolder<T> getCacheHolder();

    /**
     * Change the temporary cache value stored in this tweak. If the {@link CacheMode} is set to {@code LOCAL} then this
     * will update the value stored in {@link #fromCache()}. Otherwise, if the {@link CacheMode} is set to
     * {@code NETWORK} then this will update the value stored in {@link #fromServer()}.
     *
     * @param value A new cache value to store.
     */
    void setCacheValue(T value);

    /**
     * Sets the cache value to the tweak's default value.
     */
    void setCacheToDefault();

    /**
     * @return Whether the cache value matches the tweak's default value.
     */
    boolean isCacheDefault();

    /**
     * Functional shortcut for checking if the config cache is not at its default state.
     *
     * @return Whether the cache value does not match the tweak's default value.
     */
    default boolean isCacheNotDefault()
    {
        return !this.isCacheDefault();
    }

    /**
     * @return Whether the cache value matches the disabled value.
     */
    default boolean isCacheDisabled()
    {
        return this.fromCache().equals(this.getDisabled());
    }

    /**
     * Set the current cache value to its disabled value.
     */
    default void setCacheDisabled()
    {
        this.setCacheValue(this.getDisabled());
    }

    /**
     * Check if the given values can be saved to disk or sent to a server.
     *
     * @param cacheValue The cache value to check.
     * @return Whether the given cache value is savable.
     */
    default boolean isCacheSavable(T value, T cacheValue)
    {
        if (value instanceof Byte && cacheValue instanceof Byte)
            return ((Byte) value).compareTo((Byte) cacheValue) != 0;

        if (value instanceof Short && cacheValue instanceof Short)
            return ((Short) value).compareTo((Short) cacheValue) != 0;

        if (value instanceof Integer && cacheValue instanceof Integer)
            return ((Integer) value).compareTo((Integer) cacheValue) != 0;

        if (value instanceof Long && cacheValue instanceof Long)
            return ((Long) value).compareTo((Long) cacheValue) != 0;

        if (value instanceof Float && cacheValue instanceof Float)
            return ((Float) value).compareTo((Float) cacheValue) != 0;

        if (value instanceof Double && cacheValue instanceof Double)
            return ((Double) value).compareTo((Double) cacheValue) != 0;

        return !value.equals(cacheValue);
    }

    /**
     * Check if a tweak's local cache value has changed. If a value can be saved to disk, then this will return
     * {@code true}.
     *
     * @return Whether the local cache has changed.
     * @see #isNetworkSavable()
     * @see #isAnyCacheSavable()
     */
    default boolean isLocalSavable()
    {
        return this.isCacheSavable(this.fromDisk(), this.fromLocal());
    }

    /**
     * Check if a tweak's network cache value has changed. If a value can be sent to a server running the mod, then this
     * will return {@code true}. Otherwise, the returned value is {@code false}.
     *
     * @return Whether the network cache has changed.
     * @see #isLocalSavable()
     * @see #isAnyCacheSavable()
     */
    boolean isNetworkSavable();

    /**
     * Check if a tweak's cache value has changed. If a value can be saved to disk, then this will return {@code true}.
     * If a value can be sent to a server running the mod, then this will return {@code true}. Otherwise, the returned
     * value is {@code false}. If cache mode is relevant to examination, then use {@link #isCacheUndoable()}.
     *
     * @return Whether a client cache value or server cache value is savable.
     * @see #isLocalSavable()
     * @see #isNetworkSavable()
     */
    default boolean isAnyCacheSavable()
    {
        return this.isLocalSavable() || this.isNetworkSavable();
    }

    /**
     * Check if a tweak's current cache mode value has changed.
     *
     * @return Whether the current cache mode is savable.
     * @see #isLocalSavable()
     * @see #isNetworkSavable()
     */
    boolean isCurrentCacheSavable();

    /**
     * Check if a tweak's cache value has changed based on the selected cache mode. If the cache mode is irrelevant to
     * examining if the cache has changed, then use {@link #isAnyCacheSavable()}.
     *
     * @return Whether an undo action is available based on the tweak's selected cache mode.
     */
    boolean isCacheUndoable();

    /**
     * Functional shortcut for {@link #isCacheUndoable()}.
     *
     * @return Whether an undo action is not available based on the tweak's current cache mode.
     */
    default boolean isCacheNotUndoable()
    {
        return !this.isCacheUndoable();
    }

    /**
     * Undoes any changes made to the cache based on the current {@link CacheMode}.
     */
    void undoCache();

    /**
     * Applies the cache based on the current {@link CacheMode}. If the tweak is in {@code NETWORK} mode, then the
     * applied changes will also be sent to the server if the player is connected to a server with the mod installed and
     * the player is an operator.
     * <p>
     * This does <b color=red>not</b> save changes to disk. To save to disk, use {@link ConfigCache#save()}.
     *
     * @throws RuntimeException If the server tried to invoke this method.
     */
    void applyCurrentCache();

    /**
     * Puts the local cache value into {@link #setDisk(Object)}. If the tweak is controlled by the server, and the
     * client is connected to a server with the mod (and is an operator), then the changes are also sent to the server.
     * The server does not use this, only the client will invoke this method.
     * <p>
     * This does <b color=red>not</b> save changes to disk. To save to disk, use {@link ConfigCache#save()}.
     *
     * @throws RuntimeException If the server tried to invoke this method.
     */
    void applyCacheAndSend();

    /**
     * Applies the value to the mod's config (either client if client or server if server) via reflection. This must be
     * done before the config file is saved, or else nothing will be saved since the cache was never applied to the
     * runtime config instance.
     *
     * @param value The value to apply via reflection.
     */
    void applyReflection(T value);

    /**
     * @return A {@link TweakPacket} instance that will be sent to clients running the mod.
     */
    @Nullable
    TweakPacket getClientboundPacket();

    /**
     * @return A {@link TweakPacket} instance that will be sent to a server running the mod.
     */
    @Nullable
    TweakPacket getServerboundPacket();

    /**
     * Sends an update packet to the server. The server will handle these packets, so they are also responsible for
     * receiving data from the client, processing that data, saving new changes to I/O, and responding back to clients
     * with any updated data.
     */
    default void sendToServer()
    {
        Optional.ofNullable(this.getServerboundPacket()).ifPresent(PacketUtil::sendToServer);
    }

    /**
     * Sends an update packet to all players connected on the server. Clients will handle these packets, so they are
     * responsible for receiving data from the server and updating the network cache for tweaks as needed.
     */
    default void sendToAll()
    {
        Optional.ofNullable(this.getClientboundPacket()).ifPresent(PacketUtil::sendToAll);
    }

    /**
     * Sends an update packet to a specific player connected to the server. The player's client will handle the packet,
     * so the client will be responsible for receiving this data from the server.
     *
     * @param player A {@link ServerPlayer} instance.
     */
    default void sendToPlayer(ServerPlayer player)
    {
        Optional.ofNullable(this.getClientboundPacket()).ifPresent(packet -> PacketUtil.sendToPlayer(player, packet));
    }

    /**
     * Sync local cache values with what is saved on disk and sync multiplayer cache values with the value sent by a
     * server.
     */
    void sync();

    /**
     * Change the value that is to be saved on disk via reflection. Note, this does not perform any I/O saving
     * operations. Saving to disk must be done with {@link ConfigCache#save()} or {@link #setDiskAndSave(Object)}.
     *
     * @param value The new value that will be reflected onto the config file.
     * @see #setDiskAndSave(Object)
     */
    void setDisk(T value);

    /**
     * Change the value saved on disk via reflection and save those changes with an I/O operation. If saving is not
     * desired then use {@link #setDisk(Object)}.
     *
     * @param value The new value that will be reflected and saved into the config file.
     */
    default void setDiskAndSave(T value)
    {
        this.setDisk(value);
        ConfigCache.save();
    }

    /**
     * Change the value stored in the cache and change the value stored on the disk. Note, this does not perform any I/O
     * saving operations. To save to disk: {@link ConfigCache#save()} or {@link #setCacheAndDiskThenSave(Object)}.
     *
     * @param value The new value that will be set to the tweak's cache and the disk.
     * @see #setCacheAndDiskThenSave(Object)
     */
    default void setCacheAndDisk(T value)
    {
        this.setCacheValue(value);
        this.setDisk(value);
    }

    /**
     * Change the value stored in the cache and change the value stored on the disk and then save changes to the disk.
     * If saving to disk is not desired then use {@link #setCacheAndDisk(Object)}.
     *
     * @param value The new value that will be set to the tweak's cache and the disk.
     * @see #setCacheAndDisk(Object)
     */
    default void setCacheAndDiskThenSave(T value)
    {
        this.setCacheAndDisk(value);
        ConfigCache.save();
    }

    /**
     * Change the value received from a server with Nostalgic Tweaks installed. This is used by network packets.
     *
     * @param value The value that was set by the server over the network.
     */
    void setReceived(T value);

    /**
     * Get the default value assigned to this tweak when it was built. This can't be changed after a tweak is built, so
     * there is not a setter method for the field.
     *
     * @return The default value that is associated with this tweak.
     */
    T getDefault();

    /**
     * Get the "disabled" value associated with this tweak. When the mod is globally disabled, or when the game context
     * puts a tweak into a disabled state, then each tweak's "disabled" value is used. For example, when the player is
     * connected to a server without Nostalgic Tweaks installed, server side tweaks will return their disabled states
     * since server tweaks only work when connected to a server with the mod installed.
     *
     * @return A "disabled" value for when the tweak is in a disabled state.
     */
    T getDisabled();

    /**
     * The "disabled" value associated with a tweak can be changed during runtime. This is useful in situations where a
     * tweak builder did not provide a disabled value.
     *
     * @param value A "disabled" value to use when the tweak is put into a disabled state.
     */
    void setDisabled(T value);

    /**
     * This method is used by the network to check if a tweak's value has changed internally. Each tweak implementation
     * will have a different approach to examine changes, so this must be implemented on an as-needed basis.
     *
     * @return Whether the tweak's value has changed.
     */
    boolean hasChanged(T receivedValue);

    /**
     * Perform validation checks on tweak data.
     *
     * @param validator A {@link TweakValidator} instance.
     * @return Whether validation was successful.
     */
    boolean validate(TweakValidator validator);

    /**
     * By default, this method gets the class from the default value used to build this tweak. If this behavior is not
     * desired, then this must be overridden by the implementation.
     *
     * @return The class type of the generic assigned to this tweak.
     */
    @SuppressWarnings("unchecked") // The default value class will be a class type of <T>
    default Class<T> getGenericType()
    {
        return (Class<T>) this.getDefault().getClass();
    }

    /**
     * Cast a tweak as an {@link Object}. Useful in situations where wildcards need handled in {@link Object} form.
     *
     * @param meta The wildcard {@link Tweak}.
     * @return A {@link Tweak} instance with a cast to {@link Object}.
     */
    @SuppressWarnings("unchecked") // All generic class types are formed from a Java Object
    static Tweak<Object> wildcard(Tweak<?> meta)
    {
        return (Tweak<Object>) meta;
    }

    /**
     * Checks if the given value's class type matches this tweak's metadata class type. If so, then the consumer will
     * receive the given value.
     *
     * @param value    An {@link Object} instance.
     * @param consumer A {@link Consumer} that accepts the value.
     * @return Whether the application was applied.
     */
    default boolean applySafely(Object value, Consumer<Object> consumer)
    {
        Optional<? extends Tweak<?>> cast = this.generic(value.getClass());
        cast.ifPresent(meta -> consumer.accept(value));

        return cast.isPresent();
    }

    /**
     * Get an optional cast of a tweak to the given value class type. The optional will be empty if the tweak's value is
     * not assignable to the given class type.
     *
     * @param tweak       A tweak instance.
     * @param genericType The class type of the generic assigned to this tweak.
     * @param <U>         The generic type of the tweak's metadata.
     * @return An {@link Optional} with a {@link Tweak} cast to the given class type.
     */
    @SuppressWarnings("unchecked") // The metadata instance is class-type checked
    static <U> Optional<Tweak<U>> generic(TweakMeta<?> tweak, Class<? super U> genericType)
    {
        if (genericType.isAssignableFrom(tweak.getGenericType()))
            return Optional.of((Tweak<U>) tweak);

        return Optional.empty();
    }

    /**
     * Get an optional cast of a tweak to the given class type. The optional will be empty if the class value is not
     * assignable to the given class type.
     *
     * @param genericType The class type of the generic assigned to this tweak.
     * @param <U>         The generic type of the tweak's metadata.
     * @return An {@link Optional} with a {@link Tweak} cast to the given class type.
     */
    default <U> Optional<Tweak<U>> generic(Class<? super U> genericType)
    {
        return generic(this, genericType);
    }

    /**
     * Widen {@link TweakMeta} to a specific {@link Tweak} class. If the given class type is assignable from the given
     * metadata, then widening will occur.
     *
     * @param tweak     The {@link TweakMeta} instance to try and widen.
     * @param widenType The class type to widen to.
     * @param <U>       The class type of the tweak to widen to.
     * @return An {@link Optional} with a widened {@link Tweak}.
     */
    @SuppressWarnings("unchecked") // The metadata instance is class-type checked
    static <U> Optional<U> cast(TweakMeta<?> tweak, Class<? super U> widenType)
    {
        if (widenType.isAssignableFrom(tweak.getClass()))
            return Optional.of((U) tweak);

        return Optional.empty();
    }

    /**
     * Widen {@link TweakMeta} to a specific {@link Tweak} class. If the given class type is assignable from the given
     * metadata, then widening will occur.
     *
     * @param widenType The class type to widen to.
     * @param <U>       The class type of the tweak to widen to.
     * @return An {@link Optional} with a widened {@link Tweak}.
     */
    default <U> Optional<U> cast(Class<? super U> widenType)
    {
        return cast(this, widenType);
    }
}
