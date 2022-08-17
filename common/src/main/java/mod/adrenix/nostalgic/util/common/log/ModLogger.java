package mod.adrenix.nostalgic.util.common.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record ModLogger(String prefix)
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean isDebugging = true;

    public boolean isDebugMode() { return ModLogger.isDebugging; }
    public void setDebug(boolean state) { ModLogger.isDebugging = state; }

    private String getPrefix() { return String.format("[%s] ", LogColor.apply(LogColor.GREEN, this.prefix)); }
    private String getOutput(String input)
    {
        input = input.replaceAll("true", LogColor.apply(LogColor.GREEN, "true"));
        input = input.replaceAll("false", LogColor.apply(LogColor.RED, "false"));

        for (LogColor color : LogColor.values())
            input = color.convert(input);
        return input;
    }

    public void info(String message)
    {
        String input = String.format(this.getPrefix() + "[%s] " + message, LogColor.apply(LogColor.GREEN, "INFO"));
        LOGGER.info(getOutput(input));
    }

    public void warn(String message)
    {
        String input = String.format(this.getPrefix() + "[%s] " + message, LogColor.apply(LogColor.GOLD, "WARN"));
        LOGGER.warn(getOutput(input));
    }

    public void debug(String message)
    {
        if (ModLogger.isDebugging)
        {
            String input = String.format(this.getPrefix() + "[%s] " + message, LogColor.apply(LogColor.LIGHT_PURPLE, "DEBUG"));
            LOGGER.info(getOutput(input));
        }
    }
}
