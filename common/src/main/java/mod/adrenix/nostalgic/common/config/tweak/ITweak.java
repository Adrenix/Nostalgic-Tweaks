package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;

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

    NostalgicTweaks.Side getSide();
    TweakClientCache<?> getClientCache();
    TweakServerCache<?> getServerCache();
    void setServerCache(TweakServerCache<?> cache);
    void setClientCache(TweakClientCache<?> cache);
    void setSide(NostalgicTweaks.Side side);

    void setKey(String key);
    void setLoaded(boolean state);
    boolean isLoaded();

    default void setEnabled()
    {
        if (this.isLoaded())
            return;

        TweakClientCache<Object> clientCache = TweakClientCache.get(this);
        TweakServerCache<Object> serverCache = TweakServerCache.get(this);

        this.setSide(clientCache != null && serverCache == null ? NostalgicTweaks.Side.CLIENT : NostalgicTweaks.Side.SERVER);

        if (clientCache != null)
            clientCache.setTweak(this);

        if (NostalgicTweaks.isClient())
        {
            if (clientCache != null)
                clientCache.setStatus(StatusType.LOADED);
            else
            {
                String fail = String.format
                (
                    "[%s] Unable to set status of client tweak '%s' in tweak group '%s'.\nThis is a fault of the mod dev. Please report this key mismatch!",
                    NostalgicTweaks.MOD_NAME,
                    this.getKey(),
                    this.getGroup()
                );

                // Each config key needs to match the tweak cache. Failing this requirement results in a thrown error.
                throw new AssertionError(fail);
            }
        }
        else
        {
            if (serverCache != null)
                serverCache.setStatus(StatusType.LOADED);
            else if (clientCache == null)
            {
                String fail = String.format
                (
                    "[%s] Unable to set status of server tweak '%s' in tweak group '%s'.\nThis is a fault of the mod dev. Please report this key mismatch!",
                    NostalgicTweaks.MOD_NAME,
                    this.getKey(),
                    this.getGroup()
                );

                // Each config key needs to match the tweak cache. Failing this requirement results in a thrown error.
                throw new AssertionError(fail);
            }
        }

        this.setLoaded(true);
    }
}
