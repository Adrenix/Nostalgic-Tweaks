package mod.adrenix.nostalgic.util.common.function;

/**
 * Represents an operation that accepts three input arguments and returns no result. This is the three-arity
 * specialization of {@link java.util.function.Consumer}. This is a function interface whose functional method
 * is {@link #accept(Object, Object, Object)}.
 *
 * @param <T> The type of the first argument to the operation.
 * @param <U> The type of the second argument to the operation.
 * @param <V> The type of the third argument to the operation.
 */

public interface TriConsumer<T, U, V>
{
    /**
     * Performs this operation on the given arguments.
     * @param t The first input argument.
     * @param u The second input argument.
     * @param v The third input argument.
     */
    void accept(T t, U u, V v);
}
