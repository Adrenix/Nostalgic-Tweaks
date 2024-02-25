package mod.adrenix.nostalgic.util.common.array;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.util.Mth;

public class CycleIndex
{
    /* Fields */

    private final int minIndex;
    private final int maxIndex;
    private int lastIndex;
    private int currentIndex;
    private boolean movingForward;
    private boolean resetAtEnd;

    /* Constructor */

    /**
     * Create a new {@link CycleIndex} instance.
     *
     * @param minIndex   The minimum index.
     * @param maxIndex   The maximum index.
     * @param startIndex The starting index (will be clamped).
     * @param resetAtEnd Reset the cycle back to the minimum index when finished.
     */
    public CycleIndex(int minIndex, int maxIndex, int startIndex, boolean resetAtEnd)
    {
        this.minIndex = minIndex;
        this.maxIndex = maxIndex;
        this.currentIndex = Mth.clamp(startIndex, minIndex, maxIndex);
        this.lastIndex = this.currentIndex;
        this.resetAtEnd = resetAtEnd;
        this.movingForward = true;
    }

    /**
     * Create a new {@link CycleIndex} instance using an array where the {@code minIndex} is zero and the
     * {@code maxIndex} is the length of the array minus one.
     *
     * @param array      An array.
     * @param resetAtEnd Reset the cycle back to the minimum index when finished.
     * @param <T>        The class type of the values in the array.
     */
    public <T> CycleIndex(T[] array, boolean resetAtEnd)
    {
        this(0, array.length - 1, 0, resetAtEnd);
    }

    /**
     * Create a new {@link CycleIndex} instance using an array where the {@code minIndex} is zero and the
     * {@code maxIndex} is the length of the array minus one.
     *
     * @param array An array.
     * @param <T>   The class type of the values in the array.
     */
    public <T> CycleIndex(T[] array)
    {
        this(array, false);
    }

    /* Methods */

    /**
     * Move the current index forwards or backwards.
     */
    @PublicAPI
    public void cycle()
    {
        this.lastIndex = this.currentIndex;

        int nextIndex = movingForward ? this.currentIndex + 1 : this.currentIndex - 1;

        if (nextIndex > this.maxIndex)
        {
            this.movingForward = false;

            if (this.resetAtEnd)
                this.currentIndex = 0;
            else
                this.currentIndex--;

            return;
        }

        if (nextIndex < this.minIndex)
        {
            this.movingForward = true;
            this.currentIndex++;

            return;
        }

        this.currentIndex = nextIndex;
    }

    /**
     * Get the current index.
     */
    @PublicAPI
    public int get()
    {
        return this.currentIndex;
    }

    /**
     * Get the last index that was cycled.
     */
    @PublicAPI
    public int getLast()
    {
        return this.lastIndex;
    }

    /**
     * Move the current index forwards or backwards and get the resulting index.
     */
    @PublicAPI
    public int cycleAndGet()
    {
        this.cycle();
        return this.get();
    }

    /**
     * Set whether the cycle restarts when it is finished.
     *
     * @param state The new flag state.
     */
    @PublicAPI
    public void setResetAtEnd(boolean state)
    {
        this.resetAtEnd = state;
    }
}
