package mod.adrenix.nostalgic.client.gui.toast;

import mod.adrenix.nostalgic.tweak.config.ModTweak;

import java.util.function.Supplier;

/**
 * Each toast will have its own identifier defined here.
 */
public enum ToastId
{
    WELCOME(ModTweak.SHOW_WELCOME_TOAST::get),
    HANDSHAKE(ModTweak.SHOW_HANDSHAKE_TOAST::get),
    SERVERBOUND_TWEAK(ModTweak.SHOW_SERVERBOUND_TOAST::get),
    CLIENTBOUND_TWEAK(ModTweak.SHOW_CLIENTBOUND_TOAST::get);

    /* Fields */

    private final Supplier<Boolean> isActive;

    /* Constructor */

    ToastId(Supplier<Boolean> supplier)
    {
        this.isActive = supplier;
    }

    /* Methods */

    /**
     * @return Whether this toast is active and can be made visible to the user.
     */
    public boolean isActive()
    {
        return this.isActive.get();
    }
}
