package mod.adrenix.nostalgic.tweak.listing;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public interface DeletableMap<V, L extends Listing<V, L>> extends ListingMap<V, L>
{
    /**
     * @return The {@link Map} of deleted entries within the map listing.
     */
    Map<String, V> getDeleted();

    @Override
    default void acceptSafely(Map<String, ?> map)
    {
        map.forEach((key, value) -> this.applySafely(key, value, this::put));
    }

    /**
     * @return A {@link Collection} of all the map listing's values and deleted values.
     */
    @Override
    default Collection<V> values()
    {
        LinkedHashSet<V> values = new LinkedHashSet<>();

        values.addAll(this.getMap().values());
        values.addAll(this.getDeleted().values());

        return values;
    }

    /**
     * @return A {@link Set} of all the map listing's entries and deleted entries.
     */
    @Override
    default Set<Map.Entry<String, V>> entrySet()
    {
        LinkedHashSet<Map.Entry<String, V>> entries = new LinkedHashSet<>();

        entries.addAll(this.getMap().entrySet());
        entries.addAll(this.getDeleted().entrySet());

        return entries;
    }

    /**
     * Put a new entry in the listing's map using the given key and the default reset value of the listing.
     *
     * @param key The new map key.
     * @see #put(String, Object)
     */
    default void put(String key)
    {
        this.put(key, this.getDefaultValue());
    }

    /**
     * Put a new key/value pair into the listing's map. If the listing's map previously contained a mapping for the key,
     * the old value is replaced by the specified value. If the deleted entries map also contains the given key/value
     * pair, then that pair will be removed from the deleted entries map.
     *
     * @param key   The map key with which the given value is to be associated.
     * @param value The value to be associated with the given key.
     * @return The previous value associated with a key, or null if there was no mapping for key. A {@code null} return
     * can also indicate that the map previously associated {@code null} with a key if the implementation supports null
     * values.
     * @see Map#put(Object, Object)
     */
    default V put(String key, V value)
    {
        this.getDeleted().remove(key, value);
        return this.getMap().put(key, value);
    }

    /**
     * Copies all the mappings from the given map to this listing's map. The effect of this call is equivalent to that
     * of calling {@link #put(String, Object)} on this listing's map once for each mapping from key {@code k} to value
     * {@code v} in the given map. Any entries within the given map found in the listing's deleted map will be removed
     * from the deleted map.
     *
     * @param map Mappings to be stored in this listing's map.
     * @see Map#putAll(Map)
     */
    default void putAll(Map<? extends String, ? extends V> map)
    {
        map.forEach(this.getDeleted()::remove);
        this.getMap().putAll(map);
    }

    /**
     * Copies all the mappings from the given {@link DeletableMap} to this listing's map. The effect of this call is
     * equivalent to that of calling {@link #putAll(Map)} with {@link #getMap()}. Any entries within the given map found
     * in the listing's deleted map will be removed from the deleted map.
     *
     * @param deletable A {@link DeletableMap} instance.
     * @see #putAll(Map)
     */
    @PublicAPI
    default void putAll(DeletableMap<V, L> deletable)
    {
        this.putAll(deletable.getMap());
    }

    /**
     * Get a value from the listing map or the deleted entries using the given key.
     *
     * @param key A map key.
     * @return A listing map value or deleted value if it exists.
     */
    default @Nullable V getOrDeleted(String key)
    {
        if (this.getMap().containsKey(key))
            return this.getMap().get(key);

        if (this.getDeleted().containsKey(key))
            return this.getDeleted().get(key);

        return null;
    }

    /**
     * Check if the given listing map key is in the listing's deleted map.
     *
     * @param key A map key.
     * @return Whether the given key was found in the listing's deleted map.
     */
    default boolean isDeleted(String key)
    {
        return this.getDeleted().containsKey(key);
    }

    /**
     * Delete a key/value pair from this listing's map. It will be put into the listing's deleted map entries, so this
     * operation can be undone using {@link #undo(String, Object)}.
     *
     * @param key   A map key.
     * @param value A map value.
     */
    default void delete(String key, V value)
    {
        this.getDeleted().put(key, value);
        this.getMap().remove(key, value);
    }

    /**
     * Remove the key, value pair from the listing's map and deleted map.
     *
     * @param key   A map key.
     * @param value A map value.
     */
    default void remove(String key, V value)
    {
        this.getMap().remove(key, value);
        this.getDeleted().remove(key, value);
    }

    /**
     * Undo a deleted key/value pair from this listing's map. It will be removed from the deleted cache map and put back
     * into the listing's map.
     *
     * @param key   A map key.
     * @param value A map value.
     */
    default void undo(String key, V value)
    {
        this.getDeleted().remove(key);
        this.getMap().put(key, value);
    }

    /**
     * Adds a new entry to the listing's map with the given key and the listing map's default value, if and only if, an
     * entry with that key wasn't already present in the listing's map.
     *
     * @param key A map key.
     */
    default void putIfAbsent(String key)
    {
        if (this.getDeleted().containsKey(key))
            this.undo(key, this.getDeleted().get(key));
        else
            this.getMap().putIfAbsent(key, this.getDefaultValue());
    }

    /**
     * Clear this map's deleted entries cache.
     */
    default void resetDeletedEntries()
    {
        this.getDeleted().clear();
    }

    /**
     * Removes entries from this map that were found in the deleted entries map. The deleted entries map is cleared
     * after this operation is completed.
     */
    default void applyDeletedEntries()
    {
        this.getDeleted().forEach(this.getMap()::remove);
        this.resetDeletedEntries();
    }

    @Override
    default void applyCache()
    {
        this.applyDeletedEntries();
    }

    /**
     * Safely apply a key/value pair to the given consumer if the given value's class is assignable from the listing's
     * generic type.
     *
     * @param key      A map key.
     * @param value    A map value.
     * @param consumer A {@link BiConsumer} that accepts the key/value pair.
     * @param <T>      The value's class type.
     */
    @SuppressWarnings("unchecked") // Checks if value's class is assignable from the listing's generic type
    default <T> void applySafely(String key, T value, BiConsumer<String, V> consumer)
    {
        if (value.getClass().isAssignableFrom(this.genericType()))
            consumer.accept(key, (V) value);
    }
}
