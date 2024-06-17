package mod.adrenix.nostalgic.config.cache;

import mod.adrenix.nostalgic.util.common.data.Holder;

import java.util.function.Supplier;

/**
 * A cache holder has a {@code local} value and a {@code network} value. The {@code local} value is used by both the
 * client and server. Only the client manages and sends a {@code network} value since this is used by the config
 * graphical user interface.
 *
 * @param <T> The class type of the value stored.
 */
public class CacheHolder<T>
{
    /* Fields */

    private final Holder<T> local;
    private final Holder<T> network;
    private final Supplier<CacheMode> mode;

    /* Constructor */

    private CacheHolder(T local, T network, Supplier<CacheMode> mode)
    {
        this.local = Holder.create(local);
        this.network = Holder.create(network);
        this.mode = mode;
    }

    /* Static */

    /**
     * Start a new cache value from the given value. Both the {@code local} and {@code network} fields will have the
     * same starting value.
     *
     * @param value    A value to start from.
     * @param supplier A {@link Supplier} that provides the current {@link CacheMode} for this value.
     * @param <V>      The class type of the value.
     * @return A new network value instance.
     * @see #with(Object, Object, Supplier)
     */
    public static <V> CacheHolder<V> from(V value, Supplier<CacheMode> supplier)
    {
        return new CacheHolder<>(value, value, supplier);
    }

    /**
     * Start a new cache value with the given {@code local} and {@code network} values.
     *
     * @param local    The local value to start with.
     * @param network  The network value to start with.
     * @param supplier A {@link Supplier} that provides the current {@link CacheMode} for this value.
     * @param <V>      The class type of the value.
     * @return A new cache value instance.
     * @see #from(Object, Supplier)
     */
    public static <V> CacheHolder<V> with(V local, V network, Supplier<CacheMode> supplier)
    {
        return new CacheHolder<>(local, network, supplier);
    }

    /* Methods */

    /**
     * @return A value based on the current {@link CacheMode}.
     * @see #getLocal()
     * @see #getNetwork()
     */
    public T get()
    {
        return this.isLocalMode() ? this.getLocal() : this.getNetwork();
    }

    /**
     * Change the value stored based on the current {@link CacheMode}.
     *
     * @param value The new value to store.
     * @see #setLocal(Object)
     * @see #setNetwork(Object)
     */
    public void set(T value)
    {
        if (this.isNetworkMode())
            this.setNetwork(value);
        else
            this.setLocal(value);
    }

    /**
     * @return Whether the current {@link CacheMode} for this value is {@code LOCAL}.
     * @see #isNetworkMode()
     */
    public boolean isLocalMode()
    {
        return this.mode.get() == CacheMode.LOCAL;
    }

    /**
     * @return Whether the current {@link CacheMode} for this value is {@code NETWORK}.
     * @see #isLocalMode()
     */
    public boolean isNetworkMode()
    {
        return this.mode.get() == CacheMode.NETWORK;
    }

    /**
     * @return The {@link Holder} instance for the {@code local} field.
     * @see #getLocal()
     * @see #setLocal(Object)
     * @see #setLocal(Number)
     */
    public Holder<T> local()
    {
        return this.local;
    }

    /**
     * This value is read by both the client and server.
     *
     * @return The value currently stored {@code locally}.
     * @see #setLocal(Object)
     * @see #setLocal(Number)
     * @see #local()
     */
    public T getLocal()
    {
        return this.local().get();
    }

    /**
     * Change the value stored {@code locally}. This is used by both the client and server.
     *
     * @param value The new value to store.
     * @see #setLocal(Number)
     * @see #getLocal()
     * @see #local()
     */
    public void setLocal(T value)
    {
        this.local().set(value);
    }

    /**
     * Overload method of {@link #setLocal(Object)} for number cases.
     *
     * @param number The new number value to store.
     * @see #setLocal(Object)
     * @see #getLocal()
     * @see #local()
     */
    @SuppressWarnings("unchecked") // Value is class checked before setting
    public void setLocal(Number number)
    {
        if (this.local.get().getClass().isAssignableFrom(number.getClass()))
            this.local.set((T) number);
    }

    /**
     * @return The {@link Holder} instance for the {@code network} field.
     * @see #getNetwork()
     * @see #setNetwork(Object)
     * @see #setNetwork(Number)
     */
    public Holder<T> network()
    {
        return this.network;
    }

    /**
     * This value is only used by the client user interface and is the value that is eventually sent to the server.
     *
     * @return The value currently stored in the {@code network} field.
     * @see #setNetwork(Object)
     * @see #setNetwork(Number)
     * @see #network()
     */
    public T getNetwork()
    {
        return this.network.get();
    }

    /**
     * Change the value stored in the {@code network} field. This is only used by the client user interface and changes
     * what is sent to the server.
     *
     * @param value The new value to store.
     * @see #setNetwork(Number)
     * @see #getNetwork()
     * @see #network()
     */
    public void setNetwork(T value)
    {
        this.network().set(value);
    }

    /**
     * Overload method of {@link #setNetwork(Object)} for number cases.
     *
     * @param number The new number value to store.
     * @see #setNetwork(Object)
     * @see #getNetwork()
     * @see #network()
     */
    @SuppressWarnings("unchecked") // Value is class checked before setting
    public void setNetwork(Number number)
    {
        if (this.network.get().getClass().isAssignableFrom(number.getClass()))
            this.network.set((T) number);
    }
}
