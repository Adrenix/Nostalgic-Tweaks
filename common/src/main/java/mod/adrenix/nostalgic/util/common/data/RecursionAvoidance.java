package mod.adrenix.nostalgic.util.common.data;

import java.util.function.Supplier;

public class RecursionAvoidance
{
    /* Static */

    /**
     * Create an avoidance mechanism that prevents recursion when performing {@link #process(Runnable)}.
     *
     * @return A new {@link RecursionAvoidance} instance.
     */
    public static RecursionAvoidance create()
    {
        return new RecursionAvoidance();
    }

    /* Fields */

    private boolean processing = false;

    /* Constructor */

    private RecursionAvoidance()
    {
    }

    /* Methods */

    /**
     * Run the given runnable. The runnable will not be recursively called.
     *
     * @param runnable A {@link Runnable} instance.
     */
    public void process(Runnable runnable)
    {
        if (this.processing)
            return;

        this.processing = true;
        runnable.run();
        this.processing = false;
    }

    /**
     * Get a result from a supplier. The supplier will not be recursively called.
     *
     * @param supplier A {@link Supplier}.
     * @param <T>      The class type of the result.
     * @return A {@code nullable} result from the given supplier.
     */
    public <T> T process(Supplier<T> supplier)
    {
        if (this.processing)
            return null;

        this.processing = true;
        T result = supplier.get();
        this.processing = false;

        return result;
    }

    /**
     * @return Whether the avoidance mechanism is processing instructions.
     */
    public boolean isProcessing()
    {
        return this.processing;
    }
}
