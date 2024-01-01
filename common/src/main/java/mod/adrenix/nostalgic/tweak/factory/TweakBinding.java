package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.network.packet.tweak.TweakPacket;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.gui.KeybindingId;
import org.jetbrains.annotations.Nullable;

public class TweakBinding extends TweakValue<Integer>
{
    /* Factories */

    /**
     * Build a new {@link TweakBinding} instance that is only available for the client. Reference the {@code see also}
     * link for more information about client tweaks.
     *
     * <br><br>
     * The {@code defaultBinding} <i>must</i> use an {@code int} and <b color=red>not</b> a {@code GLFW} reference. The
     * server will not have access to {@code GLFW} when it class-loads a tweak binding instance.
     *
     * @param defaultBinding The tweak's default keybinding.
     * @param container      The tweak's {@link Container}, either a {@code category} or {@code group}.
     * @param keybindingId   The {@link KeybindingId} associated with this tweak.
     * @return A new {@link TweakBinding.Builder} instance.
     * @see TweakEnv#CLIENT
     */
    public static TweakBinding.Builder client(Integer defaultBinding, Container container, KeybindingId keybindingId)
    {
        return new Builder(defaultBinding, TweakEnv.CLIENT, container, keybindingId);
    }

    /* Fields */

    private final TweakBinding.Builder builder;

    /* Constructor */

    TweakBinding(TweakBinding.Builder builder)
    {
        super(builder);

        this.builder = builder;
    }

    /* Methods */

    /**
     * If this tweak is not a key binding, then a type of {@code NONE} will be returned.
     *
     * @return The {@link KeybindingId} of this tweak.
     */
    public KeybindingId getKeybindingId()
    {
        return this.builder.keybindingId;
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

    public static class Builder extends TweakValue.Builder<Integer, Builder>
    {
        /* Fields */

        final KeybindingId keybindingId;

        /* Constructor */

        Builder(Integer defaultValue, TweakEnv env, Container container, KeybindingId keybindingId)
        {
            super(defaultValue, env, container);

            this.keybindingId = keybindingId;
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
         * @return A new {@link TweakBinding} instance.
         */
        public TweakBinding build()
        {
            return new TweakBinding(this);
        }
    }
}
