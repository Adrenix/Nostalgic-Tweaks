package mod.adrenix.nostalgic.util.client.search;

import java.util.Map;

/**
 * Create a {@link GenericDatabase} with a generic value that is linked to a string. An example use case would be
 * finding rows in a row list where the unique class type is a row, and the string linked to that row would be that
 * row's name.
 *
 * @param <T> The class type that is associated with string keys.
 */
public class GenericDatabase<T> extends Database<T>
{
    /**
     * Add an entry to the database map. The key will be automatically made lowercase to improve search results.
     *
     * @param key   A map database key that will be compared against a query request.
     * @param value A generic value that is associated with the key.
     */
    public void put(String key, T value)
    {
        this.map.put(key.toLowerCase(), value);
    }

    /**
     * Clears all entries within the database map.
     */
    public void clear()
    {
        this.map.clear();
    }

    /**
     * @return A value from the database or {@code null} if nothing was found using the given key.
     */
    public T getFromDatabase(String key)
    {
        return this.map.get(key.toLowerCase());
    }

    @Override
    public Map<String, T> getDatabase()
    {
        return this.map;
    }

    /**
     * This method does <b color=red>not</b> reset the database map to prevent accidental clears by automatic cache
     * update systems. Use {@link #clear()} if the database map needs reset.
     */
    @Override
    public void reset()
    {
    }
}
