package mod.adrenix.nostalgic.util.common.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The mod's custom logger wrapper.
 * @param prefix The prefix that is attached to each logging statement.
 */

public record ModLogger(String prefix)
{
    /**
     * The current {@link LogManager} instance.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Whether the mod is in debugging mode.
     */
    private static boolean isDebugging = true;

    /**
     * Checks if the mod is in debug mode.
     * @return The state of this record's <code>isDebugging</code> flag.
     */
    public boolean isDebugMode()
    {
        return ModLogger.isDebugging;
    }

    /**
     * Change this record's <code>isDebugging</code> flag.
     * @param state The new state.
     */
    public void setDebug(boolean state)
    {
        ModLogger.isDebugging = state;
    }

    /**
     * Get the prefix of this record's <code>prefix</code>.
     * @return A colored square bracketed prefix with this record's <code>prefix</code>.
     */
    private String getPrefix()
    {
        return String.format("[%s] ", LogColor.apply(LogColor.BLUE, this.prefix));
    }

    /**
     * Changes known strings to specific colors, such as applying a green color to instances of 'true' within a string.
     * Any logging colors are converted to ANSI in development environments and are removed in production environments.
     * @param input The logging statement.
     * @return The logging statement modified with appropriate color changes based on mod environment.
     */
    private String getOutput(String input)
    {
        input = input.replaceAll("true", LogColor.apply(LogColor.GREEN, "true"));
        input = input.replaceAll("false", LogColor.apply(LogColor.RED, "false"));

        for (LogColor color : LogColor.values())
            input = color.convert(input);
        return input;
    }

    /* Logging Statement Utilities */

    /**
     * Create an informative logging statement.
     * @param message The info to log.
     */
    public void info(String message)
    {
        String input = String.format(this.getPrefix() + "[%s] " + message, LogColor.apply(LogColor.GREEN, "INFO"));
        LOGGER.info(getOutput(input));
    }

    /**
     * Create a warning logging statement.
     * @param message The warning to log.
     */
    public void warn(String message)
    {
        String input = String.format(this.getPrefix() + "[%s] " + message, LogColor.apply(LogColor.GOLD, "WARN"));
        LOGGER.warn(getOutput(input));
    }

    /**
     * Create a debugging logging statement.
     * @param message The debug message to log.
     */
    public void debug(String message)
    {
        if (ModLogger.isDebugging)
        {
            String input = String.format(this.getPrefix() + "[%s] " + message, LogColor.apply(LogColor.LIGHT_PURPLE, "DEBUG"));
            LOGGER.info(getOutput(input));
        }
    }
}
