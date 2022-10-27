package mod.adrenix.nostalgic.common.config.reflect;

/**
 * Provides helpers for both the client and server tweak caches.
 * The server utilizes this class so do not include client code.
 */

public abstract class TweakCommonCache
{
    public static String generateKey(GroupType group, String key) { return group.toString() + "@" + key; }
}
