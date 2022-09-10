package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;

public enum GuiTweak implements ITweak
{
    DEFAULT_SCREEN,
    DISPLAY_NEW_TAGS,
    DISPLAY_SIDED_TAGS,
    DISPLAY_TAG_TOOLTIPS,
    DISPLAY_FEATURE_STATUS,
    DISPLAY_CATEGORY_TREE,
    CATEGORY_TREE_COLOR,
    DISPLAY_ROW_HIGHLIGHT,
    ROW_HIGHLIGHT_FADE,
    ROW_HIGHLIGHT_COLOR;

    /* Implementation */

    private String key;
    private NostalgicTweaks.Side side = null;
    private TweakClientCache<?> clientCache;
    private TweakServerCache<?> serverCache;
    private boolean loaded = false;

    @Override public String getKey() { return this.key; }
    @Override public GroupType getGroup() { return GroupType.GUI; }

    @Override public void setSide(NostalgicTweaks.Side side) { this.side = side; }
    @Override public NostalgicTweaks.Side getSide() { return this.side; }

    @Override public void setClientCache(TweakClientCache<?> cache) { this.clientCache = cache; }
    @Override public void setServerCache(TweakServerCache<?> cache) { this.serverCache = cache; }
    @Override public TweakServerCache<?> getServerCache() { return this.serverCache; }
    @Override public TweakClientCache<?> getClientCache() { return this.clientCache; }

    @Override public boolean isLoaded() { return this.loaded; }
    @Override public void setLoaded(boolean state) { this.loaded = state; }
    @Override public void setKey(String key) { this.key = key; }
}
