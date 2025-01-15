package mod.adrenix.nostalgic.tweak;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * A thread-safe memoization implementation for tweak results. At the end of every tick, results are invalidated and
 * will require new computation.
 *
 * @param <T> The class type of the tweak result.
 */
public class TweakResult<T> implements Supplier<T>
{
    /* Fields */

    private final Supplier<? extends T> delegate;
    private final ConcurrentHashMap<Supplier<? extends T>, T> map;

    /* Constructor */

    public TweakResult(Supplier<? extends T> delegate)
    {
        this.delegate = delegate;
        this.map = new ConcurrentHashMap<>();
    }

    /* Methods */

    @Override
    public T get()
    {
        return this.map.computeIfAbsent(this.delegate, Supplier::get);
    }

    /**
     * Invalidate the tweak result so that it requires new computation.
     */
    public void clear()
    {
        this.map.clear();
    }
}
