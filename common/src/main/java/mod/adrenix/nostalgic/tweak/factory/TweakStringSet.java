package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.network.packet.tweak.ClientboundTweakStringSet;
import mod.adrenix.nostalgic.network.packet.tweak.ServerboundTweakStringSet;
import mod.adrenix.nostalgic.network.packet.tweak.TweakPacket;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.listing.StringSet;
import org.jetbrains.annotations.Nullable;

public class TweakStringSet extends TweakListing<String, StringSet>
{
    /* Factories */

    /**
     * Build a new {@link TweakStringSet} instance that is only available to the client. Reference the {@code see also}
     * link for more information about client tweaks.
     *
     * @param defaultSet The default {@link StringSet}.
     * @param container  The tweak's {@link Container}, either {@code category} or {@code group}.
     * @return A new {@link TweakStringSet.Builder} instance.
     * @see TweakEnv#CLIENT
     */
    public static TweakStringSet.Builder client(StringSet defaultSet, Container container)
    {
        return new Builder(defaultSet, TweakEnv.CLIENT, container);
    }

    /**
     * Build a new {@link TweakStringSet} instance that is available for both the client and server. Reference the
     * {@code see also} link for more information about server tweaks.
     *
     * @param defaultSet The default {@link StringSet}.
     * @param container  The tweak's {@link Container}, either {@code category} or {@code group}.
     * @return A new {@link TweakStringSet.Builder} instance.
     * @see TweakEnv#SERVER
     */
    public static TweakStringSet.Builder server(StringSet defaultSet, Container container)
    {
        return new Builder(defaultSet, TweakEnv.SERVER, container);
    }

    /**
     * Build a new {@link TweakStringSet} instance that is dynamic. Reference the {@code see also} link for more
     * information about dynamic tweaks.
     *
     * @param defaultSet The default {@link StringSet}.
     * @param container  The tweak's {@link Container}, either {@code category} or {@code group}.
     * @return A new {@link TweakStringSet.Builder} instance.
     * @see TweakEnv#DYNAMIC
     */
    public static TweakStringSet.Builder dynamic(StringSet defaultSet, Container container)
    {
        return new Builder(defaultSet, TweakEnv.DYNAMIC, container);
    }

    /* Constructor */

    TweakStringSet(TweakStringSet.Builder builder)
    {
        super(builder);
    }

    /* Methods */

    @Override
    public @Nullable TweakPacket getClientboundPacket()
    {
        return new ClientboundTweakStringSet(this);
    }

    @Override
    public @Nullable TweakPacket getServerboundPacket()
    {
        return new ServerboundTweakStringSet(this);
    }

    /* Builder */

    public static class Builder extends TweakListing.Builder<Builder, String, StringSet>
    {
        /* Constructor */

        Builder(StringSet defaultList, TweakEnv env, Container container)
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
         * @return A new {@link TweakStringSet} instance.
         */
        public TweakStringSet build()
        {
            return new TweakStringSet(this);
        }
    }
}
