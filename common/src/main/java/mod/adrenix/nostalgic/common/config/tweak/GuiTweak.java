package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.common.config.reflect.GroupType;

public enum GuiTweak implements ITweak
{
    DEFAULT_SCREEN,
    DISPLAY_NEW_TAGS,
    DISPLAY_SIDED_TAGS,
    DISPLAY_TAG_TOOLTIPS,
    DISPLAY_FEATURE_STATUS;

    /* Implementation */

    private String key;
    private boolean loaded = false;

    @Override public String getKey() { return this.key; }
    @Override public GroupType getGroup() { return GroupType.GUI; }

    @Override public boolean isLoaded() { return this.loaded; }
    @Override public void setLoaded(boolean state) { this.loaded = state; }
    @Override public void setKey(String key) { this.key = key; }
}
