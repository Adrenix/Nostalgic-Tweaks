package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.network.packet.tweak.TweakPacket;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import org.jetbrains.annotations.Nullable;

public class TweakColor extends TweakValue<String>
{
    /* Factories */

    /**
     * Build a new {@link TweakColor} instance that is only available for the client. Reference the {@code see also}
     * link for more information about client tweaks.
     *
     * <p><br>
     * The {@code defaultHex} <i>must</i> be of the format {@code #A-F_0-9}. A tweak will be considered {@code opaque}
     * if the {@code defaultHex} only has six characters (excluding the #). If the tweak supports alpha-transparency,
     * then the {@code defaultHex} must be eight characters (excluding the #).
     *
     * @param defaultHex The tweak's default hex color.
     * @param container  The tweak's {@link Container}, either a {@code category} or {@code group}.
     * @return A new {@link TweakColor.Builder} instance.
     * @see TweakEnv#CLIENT
     */
    public static TweakColor.Builder client(String defaultHex, Container container)
    {
        return new Builder(defaultHex, TweakEnv.CLIENT, container);
    }

    /* Fields */

    private final TweakColor.Builder builder;

    /* Constructor */

    TweakColor(TweakColor.Builder builder)
    {
        super(builder);

        this.builder = builder;
    }

    /* Methods */

    /**
     * @return Will be {@code true} if the default hex string does not have an alpha component.
     */
    public boolean isOpaque()
    {
        return !this.builder.hasTransparency;
    }

    @Override
    public @Nullable TweakPacket getClientboundPacket()
    {
        return null;
    }

    @Override
    public @Nullable TweakPacket getServerboundPacket()
    {
        return null;
    }

    /* Builder */

    public static class Builder extends TweakValue.Builder<String, Builder>
    {
        /* Fields */

        final boolean hasTransparency;

        /* Constructor */

        Builder(String defaultValue, TweakEnv env, Container container)
        {
            super(defaultValue, env, container);

            if (!defaultValue.startsWith("#"))
                throw new RuntimeException("Default color hex string did not start with #");

            if (!HexUtil.isValid(defaultValue))
                throw new RuntimeException("Invalid default hex string: [A-F][0-9] or is not 6/8 chars in length");

            this.hasTransparency = defaultValue.replace("#", "").length() > 6;

            this.load();
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
         * @return A new {@link TweakColor} instance.
         */
        public TweakColor build()
        {
            return new TweakColor(this);
        }
    }
}
