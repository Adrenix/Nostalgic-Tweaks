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
     * Get the current value and then increment the held value by one.
     *
     * @return The held value before it is incremented.
     */
    public int getAndIncrement()
    {
        int previousValue = this.value;

        this.set(this.value + 1);

        return previousValue;
    }
}
