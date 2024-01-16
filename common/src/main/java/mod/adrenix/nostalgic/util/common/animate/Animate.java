package mod.adrenix.nostalgic.util.common.animate;

import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.client.animate.Animator;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.util.Mth;

import java.util.concurrent.TimeUnit;

public interface Animate
{
    /* Linear Functions */

    /**
     * Constant movement along a line.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation linear(long duration, TimeUnit timeUnit)
    {
        return new Animator(v -> Mth.clamp(v, 0.0D, 1.0D), duration, timeUnit);
    }

    /**
     * Constant movement along a line that lasts one second.
     *
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation linear()
    {
        return linear(1L, TimeUnit.SECONDS);
    }

    /* Ease Functions */

    /**
     * Starts fast, begins to slow in the middle, and ends very slowly.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeOutExpo(long duration, TimeUnit timeUnit)
    {
        return new Animator(v -> v == 1 ? 1 : 1 - Math.pow(2, -10 * v), duration, timeUnit);
    }

    /**
     * Starts fast, begins to slow in the middle, and ends very slowly.
     *
     * @return A new animator instance that lasts for one second.
     */
    @PublicAPI
    static Animation easeOutExpo()
    {
        return easeOutExpo(1L, TimeUnit.SECONDS);
    }

    /**
     * Starts very slowly, moves quickly through the middle, and ends very slowly.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInOutExpo(long duration, TimeUnit timeUnit)
    {
        return new Animator(v -> v == 0 ? 0 : v == 1 ? 1 : v < 0.5 ? Math.pow(2, 20 * v - 10) / 2 : (2 - Math.pow(2, -20 * v + 10)) / 2, duration, timeUnit);
    }

    /**
     * Starts very slowly, moves quickly through the middle, and ends very slowly.
     *
     * @return A new animator instance that lasts for one second.
     */
    @PublicAPI
    static Animation easeInOutExpo()
    {
        return easeInOutExpo(1L, TimeUnit.SECONDS);
    }

    /**
     * Starts slow, accelerates quickly in the middle, and ends slowly.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInOutQuart(long duration, TimeUnit timeUnit)
    {
        return new Animator(v -> v < 0.5 ? 8 * v * v * v * v : 1 - Math.pow(-2 * v + 2, 4) / 2, duration, timeUnit);
    }

    /**
     * Starts slow, accelerates quickly in the middle, and ends slowly.
     *
     * @return A new animator instance that lasts for one second.
     */
    @PublicAPI
    static Animation easeInOutQuart()
    {
        return easeInOutQuart(1L, TimeUnit.SECONDS);
    }

    /**
     * Similar to ease-in-out-quartic but with smoother start/end transitions and a more aggressive transition in the
     * middle of the animation.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInOutCircular(long duration, TimeUnit timeUnit)
    {
        return new Animator(v -> v < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * v, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * v + 2, 2)) + 1) / 2, duration, timeUnit);
    }

    /**
     * Similar to ease-in-out-quartic but with smoother start/end transitions and a more aggressive transition in the
     * middle of the animation.
     *
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInOutCircular()
    {
        return easeInOutCircular(1L, TimeUnit.SECONDS);
    }
}
