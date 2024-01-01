package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.network.packet.tweak.ClientboundTweakText;
import mod.adrenix.nostalgic.network.packet.tweak.ServerboundTweakText;
import mod.adrenix.nostalgic.network.packet.tweak.TweakPacket;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.container.Container;
import org.jetbrains.annotations.Nullable;

public class TweakText extends TweakValue<String>
{
    /* Factories */

    /**
     * Build a new {@link TweakText} instance that is only available for the client. Reference the {@code see also} link
     * for more information about client tweaks.
     *
     * @param defaultText The tweak's default value.
     * @param container   The tweak's {@link Container}, either a {@code category} or {@code group}.
     * @return A new {@link TweakText.Builder} instance.
     * @see TweakEnv#CLIENT
     */
    public static TweakText.Builder client(String defaultText, Container container)
    {
        return new Builder(defaultText, TweakEnv.CLIENT, container);
    }

    /**
     * Build a new {@link TweakText} instance that is available for both the client and server. Reference the
     * {@code see also} link for more information about server tweaks.
     *
     * @param defaultText The tweak's default value.
     * @param container   The tweak's {@link Container}, either a {@code category} or {@code group}.
     * @return A new {@link TweakText.Builder} instance.
     * @see TweakEnv#SERVER
     */
    public static TweakText.Builder server(String defaultText, Container container)
    {
        return new Builder(defaultText, TweakEnv.SERVER, container);
    }

    /**
     * Build a new {@link TweakText} instance that is dynamic. Reference the {@code see also} link for more information
     * about dynamic tweaks.
     *
     * @param defaultText The tweak's default value.
     * @param container   The tweak's {@link Container}, either a {@code category} or {@code group}.
     * @return A new {@link TweakText.Builder} instance.
     * @see TweakEnv#DYNAMIC
     */
    public static TweakText.Builder dynamic(String defaultText, Container container)
    {
        return new Builder(defaultText, TweakEnv.DYNAMIC, container);
    }

    /* Constructor */

    TweakText(TweakText.Builder builder)
    {
        super(builder);
    }

    /* Methods */

    @Override
    public @Nullable TweakPacket getClientboundPacket()
    {
        return new ClientboundTweakText(this);
    }

    @Override
    public @Nullable TweakPacket getServerboundPacket()
    {
        return new ServerboundTweakText(this);
    }

    /* Builder */

    public static class Builder extends TweakValue.Builder<String, Builder>
    {
        /* Constructor */

        Builder(String defaultValue, TweakEnv env, Container container)
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
         * @return A new {@link TweakText} instance.
         */
        public TweakText build()
        {
            return new TweakText(this);
        }
    }
}
