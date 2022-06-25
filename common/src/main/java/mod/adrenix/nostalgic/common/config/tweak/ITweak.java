package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;

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

        TweakClientCache<Object> tweakCache = TweakClientCache.get(this.getGroup(), this.getKey());
        if (tweakCache != null)
            tweakCache.setStatus(StatusType.LOADED);
        else
        {
            String fail = String.format(
                "Unable to set status of tweak '%s' in tweak group '%s'.\nThis is a fault of the mod dev. Please report this key mismatch!",
                this.getKey(),
                this.getGroup()
            );

            // Each config key needs to match the tweak cache. Failing this requirement results in a thrown error.
            throw new AssertionError(fail);
        }

        this.setLoaded(true);
    }
}
