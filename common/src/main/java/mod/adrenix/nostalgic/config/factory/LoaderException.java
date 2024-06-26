package mod.adrenix.nostalgic.config.factory;

public class LoaderException extends Exception
{
    /**
     * Specific exception class for issues that arise during config loading.
     *
     * @param throwable A {@link Throwable} instance.
     */
    public LoaderException(Throwable throwable)
    {
        super(throwable);
    }
}
