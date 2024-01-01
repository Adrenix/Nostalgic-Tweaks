package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.network.packet.tweak.ClientboundTweakItemMap;
import mod.adrenix.nostalgic.network.packet.tweak.ServerboundTweakItemMap;
import mod.adrenix.nostalgic.network.packet.tweak.TweakPacket;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.listing.ItemMap;
import org.jetbrains.annotations.Nullable;

public class TweakItemMap<T> extends TweakMap<T, ItemMap<T>>
{
    /* Factories */

    /**
     * Build a new {@link TweakItemMap} instance that is only available to the client. Reference the {@code see also}
     * link for more information about client tweaks.
     *
     * @param defaultMap The default {@link ItemMap}.
     * @param container  The tweak's {@link Container}, either {@code category} or {@code group}.
     * @param <V>        The class type of the values stored in the map.
     * @return A new {@link TweakItemMap.Builder} instance.
     * @see TweakEnv#CLIENT
     */
    public static <V> TweakItemMap.Builder<V> client(ItemMap<V> defaultMap, Container container)
    {
        return new Builder<>(defaultMap, TweakEnv.CLIENT, container);
    }

    /**
     * Build a new {@link TweakItemMap} instance that is available for both the client and server. Reference the
     * {@code see also} link for more information about server tweaks.
     *
     * @param defaultMap The default {@link ItemMap}.
     * @param container  The tweak's {@link Container}, either {@code category} or {@code group}.
     * @param <V>        The class type of the values stored in the map.
     * @return A new {@link TweakItemMap.Builder} instance.
     * @see TweakEnv#SERVER
     */
    public static <V> TweakItemMap.Builder<V> server(ItemMap<V> defaultMap, Container container)
    {
        return new Builder<>(defaultMap, TweakEnv.SERVER, container);
    }

    /**
     * Build a new {@link TweakItemMap} instance that is dynamic. Reference the {@code see also} link for more
     * information about dynamic tweaks.
     *
     * @param defaultMap The default {@link ItemMap}.
     * @param container  The tweak's {@link Container}, either {@code category} or {@code group}.
     * @param <V>        The class type of the values stored in the map.
     * @return A new {@link TweakItemMap.Builder} instance.
     * @see TweakEnv#DYNAMIC
     */
    public static <V> TweakItemMap.Builder<V> dynamic(ItemMap<V> defaultMap, Container container)
    {
        return new Builder<>(defaultMap, TweakEnv.DYNAMIC, container);
    }

    /* Constructor */

    TweakItemMap(TweakMap.Builder<?, T, ItemMap<T>> builder)
    {
        super(builder);
    }

    /* Methods */

    @Override
    public @Nullable TweakPacket getClientboundPacket()
    {
        return new ClientboundTweakItemMap(this);
    }

    @Override
    public @Nullable TweakPacket getServerboundPacket()
    {
        return new ServerboundTweakItemMap(this);
    }

    /* Builder */

    public static class Builder<V> extends TweakMap.Builder<Builder<V>, V, ItemMap<V>>
    {
        /* Constructor */

        Builder(ItemMap<V> defaultList, TweakEnv env, Container container)
        {
            super(defaultList, env, container);
        }

        /* Methods */

        @Override
        Builder<V> self()
        {
            return this;
        }

        /**
         * Finalize the building process.
         *
         * @return A new {@link TweakItemMap} instance.
         */
        public TweakItemMap<V> build()
        {
            return new TweakItemMap<>(this);
        }
    }
}
