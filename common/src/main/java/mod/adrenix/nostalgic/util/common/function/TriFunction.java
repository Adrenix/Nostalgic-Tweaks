package mod.adrenix.nostalgic.util.common.function;

/**
 * Represents a function that accepts three arguments and produces a result. This is the three-arity specialization of
 * {@link java.util.function.Function}.
 *
 * @param <T> The type of the first argument to the function.
 * @param <U> The type of the second argument to the function.
 * @param <V> The type of the third argument to the function.
 * @param <R> The type of the result of the function.
 */

@FunctionalInterface
public interface TriFunction<T, U, V, R>
{
    /**
     * Applies this function to the given arguments.
     * @param t The first function argument.
     * @param u The second function argument.
     * @param v The third function argument.
     * @return The function result.
     */
    R apply(T t, U u, V v) throws IllegalAccessException;
}
