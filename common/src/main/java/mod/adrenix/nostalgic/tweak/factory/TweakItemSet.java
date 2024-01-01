package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.network.packet.tweak.ClientboundTweakItemSet;
import mod.adrenix.nostalgic.network.packet.tweak.ServerboundTweakItemSet;
import mod.adrenix.nostalgic.network.packet.tweak.TweakPacket;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.listing.ItemSet;
import org.jetbrains.annotations.Nullable;

public class TweakItemSet extends TweakListing<String, ItemSet>
{
    /* Factories */

    /**
     * Build a new {@link TweakItemSet} instance that is only available to the client. Reference the {@code see also}
     * link for more information about client tweaks.
     *
     * @param defaultSet The default {@link ItemSet}.
     * @param container  The tweak's {@link Container}, either {@code category} or {@code group}.
     * @return A new {@link TweakItemSet.Builder} instance.
     * @see TweakEnv#CLIENT
     */
    public static TweakItemSet.Builder client(ItemSet defaultSet, Container container)
    {
        return new Builder(defaultSet, TweakEnv.CLIENT, container);
    }

    /**
     * Build a new {@link TweakItemSet} instance that is available for both the client and server. Reference the
     * {@code see also} link for more information about server tweaks.
     *
     * @param defaultSet The default {@link ItemSet}.
     * @param container  The tweak's {@link Container}, either {@code category} or {@code group}.
     * @return A new {@link TweakItemSet.Builder} instance.
     * @see TweakEnv#SERVER
     */
    public static TweakItemSet.Builder server(ItemSet defaultSet, Container container)
    {
        return new Builder(defaultSet, TweakEnv.SERVER, container);
    }

    /**
     * Build a new {@link TweakItemSet} instance that is dynamic. Reference the {@code see also} link for more
     * information about dynamic tweaks.
     *
     * @param defaultSet The default {@link ItemSet}.
     * @param container  The tweak's {@link Container}, either {@code category} or {@code group}.
     * @return A new {@link TweakItemSet.Builder} instance.
     * @see TweakEnv#DYNAMIC
     */
    public static TweakItemSet.Builder dynamic(ItemSet defaultSet, Container container)
    {
        return new Builder(defaultSet, TweakEnv.DYNAMIC, container);
    }

    /* Constructor */

    TweakItemSet(TweakItemSet.Builder builder)
    {
        super(builder);
    }

    /* Methods */

    @Override
    public @Nullable TweakPacket getClientboundPacket()
    {
        return new ClientboundTweakItemSet(this);
    }

    @Override
    public @Nullable TweakPacket getServerboundPacket()
    {
        return new ServerboundTweakItemSet(this);
    }

    /* Builder */

    public static class Builder extends TweakListing.Builder<Builder, String, ItemSet>
    {
        /* Constructor */

        Builder(ItemSet defaultList, TweakEnv env, Container container)
        {
            super(defaultList, env, container);
        }

        /* Methods */

        @Override
        Builder self()
        {
            return this;
        }

        /**
         * Finalize the building process.
         *
         * @return A new {@link TweakItemSet} instance.
         */
        public TweakItemSet build()
        {
            return new TweakItemSet(this);
        }
    }
}
