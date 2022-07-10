package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;

public enum GameplayTweak implements ITweak
{
    // Combat System
    INSTANT_BOW,
    INVINCIBLE_BOW,
    ARROW_SPEED,
    DISABLE_COOLDOWN,
    DISABLE_SWEEP,

    // Experience System
    ALT_EXPERIENCE_BAR,
    DISABLE_EXP_BAR,
    ORB_SPAWN,
    ORB_RENDERING,
    ANVIL,
    ENCHANT_TABLE,

    // Hunger System
    ALT_HUNGER_BAR,
    DISABLE_HUNGER_BAR,
    HUNGER,
    INSTANT_EAT,
    FOOD_STACKING,

    // Game Mechanics
    SPRINT,
    SWIM,
    FIRE_SPREAD,
    INSTANT_AIR,
    INFINITE_BURN;

    /* Implementation */

    private String key;
    private TweakClientCache<?> clientCache;
    private TweakServerCache<?> serverCache;
    private boolean loaded = false;

    @Override public String getKey() { return this.key; }
    @Override public GroupType getGroup() { return GroupType.GAMEPLAY; }

    @Override public void setClientCache(TweakClientCache<?> cache) { this.clientCache = cache; }
    @Override public void setServerCache(TweakServerCache<?> cache) { this.serverCache = cache; }
    @Override public TweakServerCache<?> getServerCache() { return this.serverCache; }
    @Override public TweakClientCache<?> getClientCache() { return this.clientCache; }

    @Override public boolean isLoaded() { return this.loaded; }
    @Override public void setLoaded(boolean state) { this.loaded = state; }
    @Override public void setKey(String key) { this.key = key; }
}
