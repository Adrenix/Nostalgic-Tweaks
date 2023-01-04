package mod.adrenix.nostalgic.util.common;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.Util;

/**
 * Utility class that tracks how much time has elapsed. This watcher tracks time in milliseconds, so a time interval of
 * 1000L is equivalent to 1 second.
 */

public class TimeWatcher
{
    /**
     * Any timer that uses this value as its max repeat limit tells the watcher that this timer does not have max repeat
     * limit. There is an alternative constructor that only accepts an interval and that constructor will use this value
     * as the maximum repeat limit.
     */
    private static final int NO_REPEAT = -1;

    /* Fields */

    private boolean debug;
    private final long timeInterval;
    private final int maxRepeat;
    private long timeSinceLast;
    private int repeated;

    /* Constructor */

    /**
     * Create a new time watcher instance. Time intervals are in milliseconds so 1000L equals 1 second.
     * @param timeInterval The amount of time that needs to elapse before the timer is ready.
     * @param maxRepeat The amount of times to repeat until this timer is manually reset.
     */
    public TimeWatcher(long timeInterval, int maxRepeat)
    {
        this.debug = true;
        this.timeSinceLast = 0L;
        this.timeInterval = timeInterval;
        this.maxRepeat = maxRepeat;
        this.repeated = 0;
    }

    /**
     * Create a new time watcher instance where there is no maximum repeat limit. Time intervals are in milliseconds so
     * 1000L equals 1 second.
     *
     * @param timeInterval The amount of time that needs to elapse before the timer is ready.
     */
    public TimeWatcher(long timeInterval) { this(timeInterval, NO_REPEAT); }

    /* Methods */

    /**
     * Change whether this timer will emit debugger statements when debugging is enabled.
     * It is useful to disable debugging for timers that repeat very frequently.
     *
     * @param state The new state for the debug flag.
     */
    public void setDebug(boolean state) { this.debug = state; }

    /**
     * Reset the time since the timer was last ready.
     */
    public void reset()
    {
        this.timeSinceLast = Util.getMillis();
        this.repeated = 0;
    }

    /**
     * Check if the repeat tracker has reached the maximum repeat count.
     * @return Whether this timer has reached its maximum repeat allowance.
     */
    public boolean isMaxReached() { return this.repeated == this.maxRepeat; }

    /**
     * Checks if enough time has elapsed, and if so, resets the time since the timer was last ready.
     * @return Whether enough time has elapsed for this timer to be considered ready.
     */
    public boolean isReady()
    {
        if (this.timeSinceLast == 0L)
            this.timeSinceLast = Util.getMillis() - this.timeInterval;

        if (this.maxRepeat != NO_REPEAT && this.repeated >= this.maxRepeat)
            return false;

        if (Util.getMillis() - this.timeSinceLast >= this.timeInterval)
        {
            if (this.maxRepeat != NO_REPEAT)
                this.repeated++;

            this.timeSinceLast = Util.getMillis();

            if (this.debug)
            {
                NostalgicTweaks.LOGGER.debug
                (
                    "Timer [timerInterval=%s, maxRepeat=%s, repeated=%s, timeSinceLast=%s] is ready",
                    this.timeInterval,
                    this.maxRepeat,
                    this.repeated,
                    this.timeSinceLast
                );
            }

            return true;
        }

        return false;
    }
}
