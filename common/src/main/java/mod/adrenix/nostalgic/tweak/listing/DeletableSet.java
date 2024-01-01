package mod.adrenix.nostalgic.tweak.listing;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface DeletableSet<E, L extends Listing<E, L>> extends ListingSet<E, L>
{
    /**
     * @return The {@link Set} of deleted elements within the set listing.
     */
    Set<E> getDeleted();

    @Override
    default void acceptSafely(Set<?> set)
    {
        set.forEach(e -> this.applySafely(e, this::add));
    }

    /**
     * @return A {@link Stream} of all the set listing's elements and deleted elements.
     */
    @Override
    default Stream<E> stream()
    {
        LinkedHashSet<E> elements = new LinkedHashSet<>();

        elements.addAll(this.getSet());
        elements.addAll(this.getDeleted());

        return elements.stream();
    }

    /**
     * Adds the given element to this set if it is not already present. If the given element is within the listing's
     * deleted elements set, then it is removed from the deleted elements set.
     *
     * @param element A set element.
     * @return Yields {@code true} if this set did not already contain the specified element.
     * @see Set#add(Object)
     */
    default boolean add(E element)
    {
        this.getDeleted().remove(element);
        return this.getSet().add(element);
    }

    /**
     * Copies all the elements from the given collection to this listing's set. The effect of this call is equivalent to
     * that of calling {@link #add(Object)} on this listing's set once for each element in the given collection. Any
     * elements within the given collection found in the listing's deleted elements set will be removed from the deleted
     * elements set.
     *
     * @param collection A {@link Collection} of elements to add to this listing's set.
     * @see Set#addAll(Collection)
     */
    default void addAll(Collection<? extends E> collection)
    {
        collection.forEach(this.getDeleted()::remove);
        this.getSet().addAll(collection);
    }

    /**
     * Copies all the elements from the given {@link DeletableSet} to this listing's set. The effect of this call is
     * equivalent to that of calling {@link #addAll(Collection)} with {@link #getSet()}. Any elements within the given
     * {@link DeletableSet} found the listing's deleted elements set will be removed from the deleted elements set.
     *
     * @param deletable A {@link DeletableSet} instance.
     * @see #addAll(Collection)
     */
    default void addAll(DeletableSet<E, L> deletable)
    {
        this.addAll(deletable.getSet());
    }

    /**
     * Check if the given element is in the listing's deleted set.
     *
     * @param element An element.
     * @return Whether the given element was found in the listing's deleted set.
     */
    default boolean isDeleted(E element)
    {
        return this.getDeleted().contains(element);
    }

    /**
     * Checks if the given element is in this listing's set. This does not check the deleted elements set.
     *
     * @param element An element.
     * @return Whether the listing's set contains the given element.
     * @see Set#contains(Object)
     */
    default boolean contains(E element)
    {
        return this.getSet().contains(element);
    }

    /**
     * Delete an element from this listing's set. It will be added to the listing's deleted elements set, so this
     * operation can be undone using {@link #undo(Object)}.
     *
     * @param element A set element.
     */
    default void delete(E element)
    {
        this.getDeleted().add(element);
        this.getSet().remove(element);
    }

    /**
     * Remove an element from the listing's set and deleted set.
     *
     * @param element The element to remove.
     */
    default void remove(E element)
    {
        this.getSet().remove(element);
        this.getDeleted().remove(element);
    }

    /**
     * Undo a deleted element from this listing's deleted elements set. The removed element will be put back into the
     * listing's set.
     *
     * @param element A set element.
     */
    default void undo(E element)
    {
        this.add(element);

    }

    /**
     * Clears this listing's deleted elements set.
     */
    default void resetDeletedElements()
    {
        this.getDeleted().clear();
    }

    /**
     * Removes entries from this set that were found in the deleted elements set. The deleted elements set is cleared
     * after this operation is completed.
     */
    default void applyDeletedElements()
    {
        this.getDeleted().forEach(this::remove);
        this.resetDeletedElements();
    }

    @Override
    default void applyCache()
    {
        this.applyDeletedElements();
    }

    /**
     * Safely apply an element to the given consumer if the given element's class is assignable from the listing's
     * generic type.
     *
     * @param element  A set element.
     * @param consumer A {@link Consumer} that accepts the element.
     * @param <T>      The element's class type.
     */
    @SuppressWarnings("unchecked") // Checks if element's class is assignable from the listing's generic type
    default <T> void applySafely(T element, Consumer<E> consumer)
    {
        if (element.getClass().isAssignableFrom(this.genericType()))
            consumer.accept((E) element);
    }
}
