package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.common.config.reflect.GroupType;

public enum SoundTweak implements ITweak
{
    OLD_ATTACK,
    OLD_HURT,
    OLD_FALL,
    OLD_STEP,
    OLD_DOOR,
    OLD_BED,
    OLD_XP;

    /* Implementation */

    private String key;
    private boolean loaded = false;

    @Override public String getKey() { return this.key; }
    @Override public GroupType getGroup() { return GroupType.SOUND; }

    @Override public boolean isLoaded() { return this.loaded; }
    @Override public void setLoaded(boolean state) { this.loaded = state; }
    @Override public void setKey(String key) { this.key = key; }
}
