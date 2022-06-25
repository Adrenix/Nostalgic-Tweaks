package mod.adrenix.nostalgic.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record NostalgicLogger(String prefix)
{
    private static final Logger LOGGER = LogManager.getLogger();

    private String getPrefix()
    {
        return "[" + this.prefix + "] ";
    }

    public void info(String message)
    {
        LOGGER.info(this.getPrefix() + message);
    }

    public void warn(String message)
    {
        LOGGER.warn(this.getPrefix() + message);
    }
}
