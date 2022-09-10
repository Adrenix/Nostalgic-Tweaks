package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;

public enum SwingTweak implements ITweak
{
    OVERRIDE_SPEEDS;

    /* Implementation */

    private String key;
    private NostalgicTweaks.Side side = null;
    private TweakClientCache<?> clientCache;
    private TweakServerCache<?> serverCache;
    private boolean loaded = false;

    @Override public String getKey() { return this.key; }
    @Override public GroupType getGroup() { return GroupType.SWING; }

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
