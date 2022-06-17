package mod.adrenix.nostalgic.client.config.tweak;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.TweakCache;
import mod.adrenix.nostalgic.client.config.reflect.GroupType;
import mod.adrenix.nostalgic.client.config.reflect.StatusType;

/**
 * The tweak package assists the configuration menu by indicating to the user that something might be wrong if a
 * tweak's mixin code has not yet been executed.
 *
 * This method of tweak tracking is not always accurate. Most tweaks do not execute code until the action occurs
 * within game. For example, 2D items will not be considered "loaded" until an item entity enters the world.
 */

public interface ITweak
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

        TweakCache<Object> tweakCache = TweakCache.get(this.getGroup(), this.getKey());
        if (tweakCache != null)
            tweakCache.setStatus(StatusType.LOADED);
        else
        {
            NostalgicTweaks.LOGGER.warn(String.format("Unable to set status of tweak '%s' in tweak group '%s'", this.getKey(), this.getGroup()));
            NostalgicTweaks.LOGGER.warn("This is a fault of the mod dev. Please report this key mismatch!");
        }

        this.setLoaded(true);
    }
}
