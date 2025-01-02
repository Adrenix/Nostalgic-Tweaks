package mod.adrenix.nostalgic.util.common.data;

public class IntegerHolder extends Holder<Integer>
{
    /* Static */

    /**
     * Create a new {@link IntegerHolder} instance.
     *
     * @param startAt The integer to start this holder at.
     * @return A new {@link IntegerHolder} instance.
     */
    public static IntegerHolder create(int startAt)
    {
        return new IntegerHolder(startAt);
    }

    /* Constructor */

    private IntegerHolder(int startAt)
    {
        super(startAt);
    }

    /* Methods */

    /**
     * Get the current value and then increment the held value by the given value.
     *
     * @param by The amount to increment by.
     * @return The held value before it is incremented.
     */
    public int getAndIncrement(int by)
    {
        int previousValue = this.value;

        this.set(this.value + by);

        return previousValue;
    }

    /**
     * Get the current value and then increment the held value by one.
     *
     * @return The held value before it is incremented.
     */
    public int getAndIncrement()
    {
        return getAndIncrement(1);
    }

    /**
     * Get the current value and then decrement the held value by the given value.
     *
     * @return The held value before it is decremented.
     */
    public int getAndDecrement(int by)
    {
        int previousValue = this.value;

        this.set(this.value - by);

        return previousValue;
    }

    /**
     * Get the current value and then decrement the held value by one.
     *
     * @return The held value before it is decremented.
     */
    public int getAndDecrement()
    {
        return getAndDecrement(1);
    }
}
