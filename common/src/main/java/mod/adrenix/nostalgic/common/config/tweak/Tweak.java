package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.TweakGroup;
import mod.adrenix.nostalgic.common.config.reflect.TweakStatus;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;
import net.fabricmc.api.EnvType;

/**
 * The Tweak interface has two important services. First, each tweak will have an enumeration associated with its
 * client configuration cache. These enumerations will make accessing tweaks easier and will significantly improve
 * performance when the value of a tweak is queried.
 *
 * Second, this interface assists the configuration menu by indicating to the user that something might be wrong if a
 * tweak's enumeration has not yet been queried by the mod.
 *
 * This method of tweak tracking is not always accurate. Most tweaks do not execute code until the action occurs
 * within game. For example, 2D items will not be considered "loaded" until an item entity enters the world.
 */

public interface Tweak
{
    /**
     * Each tweak must be associated with a group type.
     * @return A group type enumeration value.
     */
    TweakGroup getGroup();

    /**
     * Each tweak must have a configuration key associated with it.
     * @param key A config file JSON key.
     */
    void setKey(String key);

    /**
     * Get a tweak's raw JSON configuration key.
     * @return A config file JSON key.
     */
    String getKey();

    /**
     * Each tweak must have an environment associated with it.
     * @param side An environment enumeration value (client/server).
     */
    void setEnv(EnvType side);

    /**
     * Get a tweak's environment. This will either be client or server.
     * @return An environment type enumeration value.
     */
    EnvType getEnv();

    /**
     * Set the client cache value associated with this tweak.
     * Doing this will significantly improve performance when this tweak is queried.
     *
     * @param cache A tweak client cache instance.
     */
    void setClientCache(TweakClientCache<?> cache);

    /**
     * Get the tweak client cache instance associated with this tweak.
     * @return A tweak client cache instance.
     */
    TweakClientCache<?> getClientCache();

    /**
     * Set the server cache value associated with this tweak.
     * Doing this will significantly improve performance when this tweak is queried.
     *
     * @param cache A tweak server cache instance.
     */
    void setServerCache(TweakServerCache<?> cache);

    /**
     * Get the tweak server cache instance associated with this tweak.
     * @return A tweak server cache instance.
     */
    TweakServerCache<?> getServerCache();

    /**
     * This will either be loaded or unloaded. A tweak is considered loaded when mod code queries a value for this
     * tweak instance.
     *
     * @param state A load state boolean.
     */
    void setLoaded(boolean state);

    /**
     * Get the load state for this tweak.
     * @return Whether this tweak is loaded.
     */
    boolean isLoaded();

    /**
     * Invoke this default interface method when a tweak is being loaded.
     * Tweak JSON key checks are performed here and will crash the game if there is an issue.
     *
     * These issues will only happen because of a developer and should quickly be found during game startup.
     */
    default void setEnabled()
    {
        if (this.isLoaded())
            return;

        TweakClientCache<Object> clientCache = TweakClientCache.get(this);
        TweakServerCache<Object> serverCache = TweakServerCache.get(this);

        this.setEnv(clientCache != null && serverCache == null ? EnvType.CLIENT : EnvType.SERVER);

        if (clientCache != null)
            clientCache.setTweak(this);

        if (NostalgicTweaks.isClient())
        {
            if (clientCache != null)
                clientCache.setStatus(TweakStatus.LOADED);
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
                serverCache.setStatus(TweakStatus.LOADED);
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
