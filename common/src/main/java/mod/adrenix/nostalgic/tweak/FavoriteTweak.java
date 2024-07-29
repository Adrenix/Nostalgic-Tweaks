package mod.adrenix.nostalgic.tweak;

import mod.adrenix.nostalgic.config.cache.ConfigCache;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.factory.Tweak;

/**
 * Simple utility class for managing the mod's favorite tweak list within the user's config. The tweak used is hidden
 * from the config user interface. Instead, a "star" button is on the left of tweak value controller widgets. When
 * clicked, the tweak will be taken in/out of the favorite list.
 */
public abstract class FavoriteTweak
{
    /**
     * Add a tweak to the config's favorite tweak list.
     *
     * @param tweak A {@link Tweak} instance.
     */
    public static void add(Tweak<?> tweak)
    {
        ModTweak.FAVORITE_TWEAKS.fromCache().add(tweak.getJsonPathId());
        ModTweak.FAVORITE_TWEAKS.applyCacheAndSend();

        ConfigCache.save();
    }

    /**
     * Remove a tweak from the config's favorite tweak list.
     *
     * @param tweak A {@link Tweak} instance.
     */
    public static void remove(Tweak<?> tweak)
    {
        ModTweak.FAVORITE_TWEAKS.fromCache().remove(tweak.getJsonPathId());
        ModTweak.FAVORITE_TWEAKS.applyCacheAndSend();

        ConfigCache.save();
    }

    /**
     * A toggle helper method that will remove the tweak if is in the favorite list or add the tweak if it's not in the
     * favorite list.
     *
     * @param tweak A {@link Tweak} instance.
     */
    public static void toggle(Tweak<?> tweak)
    {
        if (isAbsent(tweak))
            add(tweak);
        else if (isPresent(tweak))
            remove(tweak);
    }

    /**
     * Check if the given tweak is within the config's favorite tweak list.
     *
     * @param tweak A {@link Tweak} instance.
     * @return Whether the tweak has been starred.
     */
    public static boolean isPresent(Tweak<?> tweak)
    {
        return ModTweak.FAVORITE_TWEAKS.fromCache().contains(tweak.getJsonPathId());
    }

    /**
     * Check if the given tweak is not within the config's favorite tweak list.
     *
     * @param tweak A {@link Tweak} instance.
     * @return Whether the tweak has not been starred.
     */
    public static boolean isAbsent(Tweak<?> tweak)
    {
        return !isPresent(tweak);
    }
}
