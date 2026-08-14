package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.network.packet.tweak.ClientboundTweakMobMap;
import mod.adrenix.nostalgic.network.packet.tweak.ServerboundTweakMobMap;
import mod.adrenix.nostalgic.network.packet.tweak.TweakPacket;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.listing.MobMap;
import org.jetbrains.annotations.Nullable;

public class TweakMobMap<T> extends TweakMap<T, MobMap<T>>
{
    /* Factories */

    /**
     * Build a new {@link TweakMobMap} instance that is only available to the client. Reference the {@code see also}
     * link for more information about client tweaks.
     *
     * @param defaultMap The default {@link MobMap}.
     * @param container  The tweak's {@link Container}, either {@code category} or {@code group}.
     * @param <V>        The class type of the values stored in the map.
     * @return A new {@link TweakMobMap.Builder} instance.
     * @see TweakEnv#CLIENT
     */
    public static <V> TweakMobMap.Builder<V> client(MobMap<V> defaultMap, Container container)
    {
        return new TweakMobMap.Builder<>(defaultMap, TweakEnv.CLIENT, container);
    }

    /**
     * Build a new {@link TweakMobMap} instance that is available for both the client and server. Reference the
     * {@code see also} link for more information about server tweaks.
     *
     * @param defaultMap The default {@link MobMap}.
     * @param container  The tweak's {@link Container}, either {@code category} or {@code group}.
     * @param <V>        The class type of the values stored in the map.
     * @return A new {@link TweakMobMap.Builder} instance.
     * @see TweakEnv#SERVER
     */
    public static <V> TweakMobMap.Builder<V> server(MobMap<V> defaultMap, Container container)
    {
        return new TweakMobMap.Builder<>(defaultMap, TweakEnv.SERVER, container);
    }

    /**
     * Build a new {@link TweakMobMap} instance that is dynamic. Reference the {@code see also} link for more
     * information about dynamic tweaks.
     *
     * @param defaultMap The default {@link MobMap}.
     * @param container  The tweak's {@link Container}, either {@code category} or {@code group}.
     * @param <V>        The class type of the values stored in the map.
     * @return A new {@link TweakMobMap.Builder} instance.
     * @see TweakEnv#DYNAMIC
     */
    public static <V> TweakMobMap.Builder<V> dynamic(MobMap<V> defaultMap, Container container)
    {
        return new TweakMobMap.Builder<>(defaultMap, TweakEnv.DYNAMIC, container);
    }

    /* Constructor */

    TweakMobMap(TweakMap.Builder<?, T, MobMap<T>> builder)
    {
        super(builder);
    }

    @Override
    public @Nullable TweakPacket getClientboundPacket()
    {
        return new ClientboundTweakMobMap(this);
    }

    @Override
    public @Nullable TweakPacket getServerboundPacket()
    {
        return new ServerboundTweakMobMap(this);
    }

    /* Builder */

    public static class Builder<V> extends TweakMap.Builder<Builder<V>, V, MobMap<V>>
    {
        Builder(MobMap<V> defaultList, TweakEnv env, Container container)
        {
            super(defaultList, env, container);
        }

        @Override
        Builder<V> self()
        {
            return this;
        }

        /**
         * Finalize the building process.
         *
         * @return A new {@link TweakMobMap} instance.
         */
        public TweakMobMap<V> build()
        {
            return new TweakMobMap<>(this);
        }
    }
}
