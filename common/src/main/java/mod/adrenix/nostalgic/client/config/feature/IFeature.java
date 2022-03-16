package mod.adrenix.nostalgic.client.config.feature;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.EntryCache;
import mod.adrenix.nostalgic.client.config.reflect.GroupType;
import mod.adrenix.nostalgic.client.config.reflect.StatusType;

/**
 * The feature package assists the configuration menu by indicating to the user that something might be wrong if a
 * mod feature's mixin code has not yet been executed.
 *
 * This method of feature tracking is not always accurate. Most mod features do not execute until the action occurs
 * within game. For example, 2D items will not be considered "loaded" until an item entity enters the world.
 */

public interface IFeature
{
    String getKey();
    GroupType getGroup();

    void setKey(String key);
    void setLoaded(boolean state);
    boolean isLoaded();

    default void setEnabled()
    {
        if (this.isLoaded())
            return;

        EntryCache<Object> entryCache = EntryCache.get(this.getGroup(), this.getKey());
        if (entryCache != null)
            entryCache.setStatus(StatusType.OKAY);
        else
        {
            NostalgicTweaks.LOGGER.warn(String.format("Unable to set status of feature '%s' in feature group '%s'", this.getKey(), this.getGroup()));
            NostalgicTweaks.LOGGER.warn("This is a fault of the mod dev. Please report this key mismatch!");
        }

        this.setLoaded(true);
    }
}
