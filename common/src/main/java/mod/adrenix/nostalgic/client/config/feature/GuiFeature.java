package mod.adrenix.nostalgic.client.config.feature;

import mod.adrenix.nostalgic.client.config.reflect.GroupType;

public enum GuiFeature implements IFeature
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
