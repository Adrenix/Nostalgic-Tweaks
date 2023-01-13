package mod.adrenix.nostalgic.util.common.function;

/**
 * Represents a function that accepts four arguments and produces a result. This is the four-arity specialization of
 * {@link java.util.function.Function}.
 *
 * @param <T> The type of the first argument to the function.
 * @param <U> The type of the second argument to the function.
 * @param <V> The type of the third argument to the function.
 * @param <W> The type of the fourth argument to the function.
 * @param <R> The type of the result of the function.
 */

@FunctionalInterface
public interface QuadFunction<T, U, V, W, R>
{
    /**
     * Applies this function to the given arguments.
     * @param t The first function argument.
     * @param u The second function argument.
     * @param v The third function argument.
     * @param w The fourth function argument.
     * @return The function result.
     */
    R apply(T t, U u, V v, W w);
}
