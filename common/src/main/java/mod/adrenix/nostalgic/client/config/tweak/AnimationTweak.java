package mod.adrenix.nostalgic.client.config.tweak;

import mod.adrenix.nostalgic.client.config.reflect.GroupType;

public enum AnimationTweak implements ITweak
{
    ITEM_SWING,
    COOLDOWN,
    REEQUIP,
    ARM_SWAY,
    ARM_SWAY_MIRROR,
    ARM_SWAY_INTENSITY,
    COLLIDE_BOB,
    BOB_VERTICAL,
    SNEAK_SMOOTH,
    SWING_DROP,
    SWING_INTERRUPT,
    ZOMBIE_ARMS,
    SKELETON_ARMS,
    GHAST_CHARGING,
    TOOL_EXPLODE;

    /* Implementation */

    private String key;
    private boolean loaded = false;

    @Override public String getKey() { return this.key; }
    @Override public GroupType getGroup() { return GroupType.ANIMATION; }

    @Override public boolean isLoaded() { return this.loaded; }
    @Override public void setLoaded(boolean state) { this.loaded = state; }
    @Override public void setKey(String key) { this.key = key; }
}
