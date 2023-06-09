package mod.adrenix.nostalgic.util.common;

import org.jetbrains.annotations.CheckReturnValue;

public abstract class ArrayUtil
{
    /**
     * Copies the values <code>from</code> one array <code>to</code> another array.
     * @param from Array to copy values from.
     * @param to Array to paste values to.
     */
    public static void copy(float[] from, float[] to)
    {
        if (from.length != to.length)
            throw new AssertionError("Both arrays must be the same size to copy");

        System.arraycopy(from, 0, to, 0, from.length);
    }

    /**
     * Safely get an item from an array without incurring an out-of-bounds exception.
     * @param array The array to get the item from.
     * @param index The index the item should be at.
     * @param <T> The item from the given index.
     * @return Item at the given index within the array. Will be null if the index is out-of-bounds.
     */
    @CheckReturnValue
    public static <T> T get(T[] array, int index)
    {
        int bound = array.length - 1;

        if (index <= bound && index >= 0)
            return array[index];

        return null;
    }
}
