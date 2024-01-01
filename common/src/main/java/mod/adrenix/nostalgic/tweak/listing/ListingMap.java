package mod.adrenix.nostalgic.tweak.listing;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ListingMap<V, L extends Listing<V, L>> extends Listing<V, L>
{
    /**
     * @return The {@link Map} that is within the map listing.
     */
    Map<String, V> getMap();

    /**
     * Each implementation of this interface must provide a {@link Set} of {@link Map.Entry} entries the listing holds.
     * While this may not be useful for some implementations, it is useful where the data stored in the main map is not
     * the only data that should be included in the set. For example, deleted entries may be included in the returned
     * set.
     *
     * @return A {@link Set} of all the map listing's entries and deleted entries.
     */
    Set<Map.Entry<String, V>> entrySet();

    /**
     * Each implementation of this interface must provide a {@link Collection} of the values the listing holds. While
     * this may not be useful for some implementations, it is useful in situations where data stored in the main map is
     * not the only data that should be included in the collection.
     *
     * @return A {@link Collection} of values held by the listing.
     */
    Collection<V> values();

    /**
     * Safely accept a map of string/value entries to be added to this listing.
     *
     * @param map A {@link Map} of string/value entries.
     */
    void acceptSafely(Map<String, ?> map);

    /**
     * Any map-like listing that implements this interface must provide a default value when a new entry is added. This
     * is required in situations where an entry needs reset, or a new entry is added without a known value.
     *
     * @return The default map value.
     */
    V getDefaultValue();
}
