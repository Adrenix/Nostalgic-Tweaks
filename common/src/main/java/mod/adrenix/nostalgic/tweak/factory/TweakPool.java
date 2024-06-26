package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.config.cache.CacheMode;
import mod.adrenix.nostalgic.tweak.TweakStatus;
import mod.adrenix.nostalgic.util.common.CollectionUtil;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class TweakPool
{
    /* Cache Map */

    /**
     * This map contains a list of all tweaks that were made from the {@link Tweak#register(String)} method. The purpose
     * of this map is to get tweaks using a config json path identifier. This should only be used in situations where
     * tweaks aren't available. For example, the packet system must use this pool to determine what tweak to change by
     * using a config json path. Both the client and server have access to this map, but the server will only have
     * access to server side tweaks since it only registers server side tweaks.
     *
     * <p><br>
     * To get a tweak's config json path, use {@link Tweak#getJsonPathId()}.
     */
    static final LinkedHashMap<String, Tweak<?>> TWEAK_MAP = new LinkedHashMap<>();

    /**
     * Get an optional tweak from the tweak pool map using the provided pool identifier. Tweak pool map keys follow the
     * mod's JSON config tree. These keys will be in the format of {@code categoryId.tweakId}. For example,
     * {@code gameplay.oldFire}).
     *
     * @param poolId A config json path identifier that points to a tweak.
     * @return An {@link Optional} {@link Tweak}.
     * @see Tweak#getJsonPathId()
     */
    public static Optional<Tweak<?>> find(String poolId)
    {
        return Optional.ofNullable(TWEAK_MAP.get(poolId));
    }

    /**
     * @return A {@link Collection} of all {@link Tweak} instances.
     */
    public static Collection<Tweak<?>> values()
    {
        return TWEAK_MAP.values();
    }

    /**
     * @return A {@link Stream} of all {@link Tweak} instances.
     */
    public static Stream<Tweak<?>> stream()
    {
        return values().stream();
    }

    /**
     * Get a filtered {@link Stream} of {@link Tweak} instances based on the provided predicate(s).
     *
     * @param filters A varargs list of {@link Predicate}.
     * @return A filtered {@link Stream} of {@link Tweak} instances.
     */
    @SafeVarargs
    public static Stream<Tweak<?>> filter(Predicate<Tweak<?>>... filters)
    {
        return CollectionUtil.filterAll(stream(), filters);
    }

    /**
     * Get a filtered {@link Stream} of {@link Tweak} instances based on the provided collection of predicates.
     *
     * @param filters A {@link Collection} of {@link Predicate}.
     * @return A filtered {@link Stream} of {@link Tweak} instances.
     */
    public static Stream<Tweak<?>> filter(Collection<Predicate<Tweak<?>>> filters)
    {
        return CollectionUtil.filterAll(stream(), filters);
    }

    /* Cache Status */

    /**
     * This flag determines if the {@link #setAllFail()} method has been called. If it has, then the method will return
     * early since the mod has already performed the update for all tweaks.
     */
    private static boolean isCacheStatusUpdated = false;

    /**
     * Update all tweaks in the global cache to the <code color=red>FAIL</code> state if they're currently in the
     * {@code WAIT} state. This method will only run <i>once</i> per game session and should only be called <i>after</i>
     * the player joins a world.
     */
    public static void setAllFail()
    {
        if (isCacheStatusUpdated)
            return;

        TWEAK_MAP.forEach((key, tweak) -> {
            if (tweak.getEnvStatus() == TweakStatus.WAIT)
                tweak.setEnvStatus(TweakStatus.FAIL);
        });

        isCacheStatusUpdated = true;
    }

    /**
     * Update all tweaks in the global cache to the correct-sided cache mode based on the current game state. This
     * should be invoked when the config user interface screen is opened.
     */
    public static void setAllCacheModes()
    {
        TWEAK_MAP.forEach((key, tweak) -> {
            if (NostalgicTweaks.isServer())
                return;

            if (tweak.isNetworkAvailable())
                tweak.setCacheMode(CacheMode.NETWORK);
            else
                tweak.setCacheMode(CacheMode.LOCAL);
        });
    }
}