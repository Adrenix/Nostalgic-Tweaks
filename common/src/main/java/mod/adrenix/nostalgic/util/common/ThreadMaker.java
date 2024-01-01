package mod.adrenix.nostalgic.util.common;

/**
 * Simple wrapper utility that reduces the overhead of creating a new thread.
 */
public abstract class ThreadMaker
{
    /**
     * Create a new thread.
     *
     * @param name     The name of thread.
     * @param runnable Instructions to run in the thread.
     * @return A new {@link Thread} instance.
     */
    public static Thread create(String name, Runnable runnable)
    {
        return new Thread(name)
        {
            @Override
            public void run()
            {
                runnable.run();
            }
        };
    }

    /**
     * Create a new thread with callback instructions.
     *
     * @param name     The name of thread.
     * @param runnable Instructions to run in the thread.
     * @param callback Instructions to run after the given runnable has finished. This will run on the new thread and is
     *                 guaranteed to execute even if an error occurs in the given runnable.
     * @return A new {@link Thread} instance.
     */
    public static Thread create(String name, Runnable runnable, Runnable callback)
    {
        return new Thread(name)
        {
            @Override
            public void run()
            {
                try
                {
                    runnable.run();
                }
                finally
                {
                    callback.run();
                }
            }
        };
    }
}
