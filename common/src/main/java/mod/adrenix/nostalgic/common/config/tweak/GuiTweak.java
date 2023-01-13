package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import net.fabricmc.api.EnvType;

/**
 * Although user interface tweaks don't go through the main mod config pipeline, it is still necessary for tweaks to be
 * loaded into the tweak client cache so that options can be loaded by the config row list manager.
 */

public enum GuiTweak implements Tweak
{
    // Graphical User Interface

    DEFAULT_SCREEN,
    DISPLAY_NEW_TAGS,
    DISPLAY_SIDED_TAGS,
    DISPLAY_TAG_TOOLTIPS,
    DISPLAY_FEATURE_STATUS,
    DISPLAY_CATEGORY_TREE,
    CATEGORY_TREE_COLOR,
    DISPLAY_ROW_HIGHLIGHT,
    ROW_HIGHLIGHT_FADE,
    ROW_HIGHLIGHT_COLOR,
    MAXIMUM_BACKUPS;

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

    @Override public TweakGroup getGroup() { return TweakGroup.GUI; }

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
