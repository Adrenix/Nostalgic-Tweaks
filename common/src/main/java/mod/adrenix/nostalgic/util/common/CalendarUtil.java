package mod.adrenix.nostalgic.util.common;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;

import java.time.Month;
import java.time.MonthDay;

public abstract class CalendarUtil
{
    /**
     * Check if today matches the given month and day of the month.
     *
     * @param month      A {@link Month} enumeration.
     * @param dayOfMonth A day of the month, from 1 to 31.
     * @return Whether today matches the given arguments.
     */
    @PublicAPI
    public static boolean isToday(Month month, int dayOfMonth)
    {
        return MonthDay.now().equals(MonthDay.of(month, dayOfMonth));
    }

    /**
     * Check if today matches the given numerical month and day of the month.
     *
     * @param month      The month of the year, from 1 to 12.
     * @param dayOfMonth A day of the month, from 1 to 31.
     * @return Whether today matches the given arguments.
     */
    @PublicAPI
    public static boolean isToday(int month, int dayOfMonth)
    {
        return MonthDay.now().equals(MonthDay.of(month, dayOfMonth));
    }
}
