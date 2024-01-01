package mod.adrenix.nostalgic.util.common.data;

import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class NullableResult
{
    /**
     * Get a {@code nullable} result using the given {@code nullable} value and mapper function.
     *
     * @param value The {@code nullable} to apply to the map function if the value is not {@code null}.
     * @param map   The {@link Function} to get a {@code nullable} result from.
     * @param <T>   The class type of the given value.
     * @param <R>   The class type of the map result.
     * @return A {@code nullable} {@link R} value.
     */
    @Nullable
    public static <T, R> R get(@Nullable T value, Function<? super T, ? extends R> map)
    {
        if (value == null)
            return null;

        return map.apply(value);
    }
}
