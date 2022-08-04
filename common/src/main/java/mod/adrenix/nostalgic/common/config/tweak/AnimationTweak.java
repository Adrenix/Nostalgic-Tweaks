package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;

public enum AnimationTweak implements ITweak
{
    // Arm Animations
    ARM_SWAY,
    ARM_SWAY_MIRROR,
    ARM_SWAY_INTENSITY,
    ITEM_SWING,
    SWING_DROP,

    // Item Animations
    COOLDOWN,
    REEQUIP,
    TOOL_EXPLODE,

    // Mob Animations
    ZOMBIE_ARMS,
    SKELETON_ARMS,
    GHAST_CHARGING,

    // Player Animations
    DEATH,
    BACKWARD_WALK,
    COLLIDE_BOB,
    BOB_VERTICAL,
    CREATIVE_CROUCH,
    SNEAK_SMOOTH;

    /* Implementation */

    private String key;
    private TweakClientCache<?> clientCache;
    private TweakServerCache<?> serverCache;
    private boolean loaded = false;

    @Override public String getKey() { return this.key; }
    @Override public GroupType getGroup() { return GroupType.ANIMATION; }

    @Override public void setClientCache(TweakClientCache<?> cache) { this.clientCache = cache; }
    @Override public void setServerCache(TweakServerCache<?> cache) { this.serverCache = cache; }
    @Override public TweakServerCache<?> getServerCache() { return this.serverCache; }
    @Override public TweakClientCache<?> getClientCache() { return this.clientCache; }

    @Override public boolean isLoaded() { return this.loaded; }
    @Override public void setLoaded(boolean state) { this.loaded = state; }
    @Override public void setKey(String key) { this.key = key; }
}
