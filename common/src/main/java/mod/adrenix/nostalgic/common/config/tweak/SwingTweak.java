package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import net.fabricmc.api.EnvType;

/**
 * Many of the tweaks defined in the client config swing subclass do not have an enumeration attached to them. This is
 * because those tweaks are only used by the client and are not impacted by a disabled mod state. Other tweaks that do
 * change when the mod state is off and/or are impacted by the server, need to be processed by the mod config pipeline
 * procedure. That pipeline prevents server controlled tweaks from running without being connected to a world with the
 * mod installed and prevents tweaks from running when the mod is disabled.
 */

public enum SwingTweak implements Tweak
{
    // Swing

    OVERRIDE_SPEEDS,
    LEFT_CLICK_SPEEDS,
    RIGHT_CLICK_SPEEDS;

    /* Fields */

    /**
     * This field must be defined in the client config within a static block below an entry definition.
     * There are safeguard checks in place to prevent missing, mistyped, or invalid key entries.
     */
    private String key;

    /**
     * Keeps track of whether this tweak is client or server controller.
     */
    private EnvType env = null;

    /**
     * Keeps track of whether this tweak has had its enumeration queried.
     */
    private boolean loaded = false;

    /* Caching */

    private TweakClientCache<?> clientCache;
    private TweakServerCache<?> serverCache;

    /* Tweak Implementation */

    @Override public TweakGroup getGroup() { return TweakGroup.SWING; }

    @Override public void setKey(String key) { this.key = key; }
    @Override public String getKey() { return this.key; }

    @Override public void setEnv(EnvType env) { this.env = env; }
    @Override public EnvType getEnv() { return this.env; }

    @Override public void setClientCache(TweakClientCache<?> cache) { this.clientCache = cache; }
    @Override public TweakClientCache<?> getClientCache() { return this.clientCache; }

    @Override public void setServerCache(TweakServerCache<?> cache) { this.serverCache = cache; }
    @Override public TweakServerCache<?> getServerCache() { return this.serverCache; }

    @Override public void setLoaded(boolean state) { this.loaded = state; }
    @Override public boolean isLoaded() { return this.loaded; }
}
