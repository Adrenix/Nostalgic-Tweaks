package mod.adrenix.nostalgic.tweak.listing;

import java.util.Set;
import java.util.stream.Stream;

public interface ListingSet<E, L extends Listing<E, L>> extends Listing<E, L>
{
    /**
     * @return The {@link Set} that is within the set listing.
     */
    Set<E> getSet();

    /**
     * Each implementation of this interface must provide a stream of the values the set holds. While this may not be
     * useful for some implementations, it is useful in situations where data stored in the main set is not the only
     * data that should be included in the stream. For example, deleted elements may be included in the resulting
     * stream.
     *
     * @return A {@link Stream} of values held by the listing.
     */
    Stream<E> stream();

    /**
     * Safely accept a set of values to be added to this listing.
     *
     * @param set A {@link Set} of elements.
     */
    void acceptSafely(Set<?> set);
}
