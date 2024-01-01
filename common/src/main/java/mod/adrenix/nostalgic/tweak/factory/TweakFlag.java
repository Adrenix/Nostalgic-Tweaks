package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.network.packet.tweak.ClientboundTweakFlag;
import mod.adrenix.nostalgic.network.packet.tweak.ServerboundTweakFlag;
import mod.adrenix.nostalgic.network.packet.tweak.TweakPacket;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.container.Container;
import org.jetbrains.annotations.Nullable;

public class TweakFlag extends TweakValue<Boolean>
{
    /* Factories */

    /**
     * Build a new {@link TweakFlag} instance that is only available for the client. Reference the {@code see also} link
     * for more information about client tweaks.
     *
     * @param defaultFlag The tweak's default value.
     * @param container   The tweak's {@link Container}, either a {@code category} or {@code group}.
     * @return A new {@link TweakFlag.Builder} instance.
     * @see TweakEnv#CLIENT
     */
    public static TweakFlag.Builder client(boolean defaultFlag, Container container)
    {
        return new Builder(defaultFlag, TweakEnv.CLIENT, container);
    }

    /**
     * Build a new {@link TweakFlag} instance that is available for both the client and server. Reference the
     * {@code see also} link for more information about server tweaks.
     *
     * @param defaultFlag The tweak's default value.
     * @param container   The tweak's {@link Container}, either a {@code category} or {@code group}.
     * @return A new {@link TweakFlag.Builder} instance.
     * @see TweakEnv#SERVER
     */
    public static TweakFlag.Builder server(boolean defaultFlag, Container container)
    {
        return new Builder(defaultFlag, TweakEnv.SERVER, container);
    }

    /**
     * Build a new {@link TweakFlag} instance that is dynamic. Reference the {@code see also} link for more information
     * about dynamic tweaks.
     *
     * @param defaultFlag The tweak's default value.
     * @param container   The tweak's {@link Container}, either a {@code category} or {@code group}.
     * @return A new {@link TweakFlag.Builder} instance.
     * @see TweakEnv#DYNAMIC
     */
    public static TweakFlag.Builder dynamic(boolean defaultFlag, Container container)
    {
        return new Builder(defaultFlag, TweakEnv.DYNAMIC, container);
    }

    /* Constructor */

    TweakFlag(TweakFlag.Builder builder)
    {
        super(builder);
    }

    /* Methods */

    @Override
    public @Nullable TweakPacket getClientboundPacket()
    {
        return new ClientboundTweakFlag(this);
    }

    @Override
    public @Nullable TweakPacket getServerboundPacket()
    {
        return new ServerboundTweakFlag(this);
    }

    /* Builder */

    public static class Builder extends TweakValue.Builder<Boolean, Builder>
    {
        /* Constructor */

        Builder(Boolean defaultValue, TweakEnv env, Container container)
        {
            super(defaultValue, env, container);
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
         * @return A new {@link TweakFlag} instance.
         */
        public TweakFlag build()
        {
            return new TweakFlag(this);
        }
    }
}
