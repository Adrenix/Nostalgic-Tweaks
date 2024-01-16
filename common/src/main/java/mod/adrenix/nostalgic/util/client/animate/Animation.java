package mod.adrenix.nostalgic.util.client.animate;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.util.Mth;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public interface Animation
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
        return new AnimationImpl(v -> Mth.clamp(v, 0.0D, 1.0D), duration, timeUnit);
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
        return new AnimationImpl(v -> v == 1 ? 1 : 1 - Math.pow(2, -10 * v), duration, timeUnit);
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
        return new AnimationImpl(v -> v == 0 ? 0 : v == 1 ? 1 : v < 0.5 ? Math.pow(2, 20 * v - 10) / 2 : (2 - Math.pow(2, -20 * v + 10)) / 2, duration, timeUnit);
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
        return new AnimationImpl(v -> v < 0.5 ? 8 * v * v * v * v : 1 - Math.pow(-2 * v + 2, 4) / 2, duration, timeUnit);
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
        return new AnimationImpl(v -> v < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * v, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * v + 2, 2)) + 1) / 2, duration, timeUnit);
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

    /**
     * Find an animator using the given identifier.
     *
     * @param identifier An {@link Object} instance that is linked to an animator.
     * @return An {@link Optional} {@link Animation}.
     */
    @PublicAPI
    static Optional<Animation> find(Object identifier)
    {
        return AnimationImpl.INSTANCES.stream()
            .filter(animation -> animation.getIdentifier().equals(identifier))
            .findFirst();
    }

    /* Methods */

    /**
     * Copy this animator and get a fresh new instance separated from this instance.
     *
     * @return A new {@link Animation} instance.
     */
    @PublicAPI
    Animation copy();

    /**
     * Set the minimum value allowed for this animator. The default is {@code Double.MIN_VALUE}.
     *
     * @param minValue A new minimum value.
     */
    @PublicAPI
    void setMinValue(double minValue);

    /**
     * Set the maximum value allowed for this animator. The default is {@code Double.MAX_VALUE}.
     *
     * @param maxValue A new maximum value.
     */
    @PublicAPI
    void setMaxValue(double maxValue);

    /**
     * Use this method to retrieve a value that can be used for animating a specific value. Linear interpolation is
     * applied to this value using the progress between two ticks (also known as the game's "frame time").
     *
     * @return An animated value is usually between 0.0D and 1.0D. This range is <b color=red>not</b> enforced and some
     * animators may yield a value outside of this range depending on the purpose of the animation. It is up to the
     * caller to perform bound enforcement as needed.
     */
    @PublicAPI
    double getValue();

    /**
     * An animator instance will only be ticked if an animation is currently taking place. The animation is considered
     * "finished" if both the last and current values are equal. Additionally, if the tick progress reaches zero while
     * the animator is in "reverse", or if the tick progress reaches the maximum duration in ticks when moving
     * "forward", then the animation will also be considered "finished."
     *
     * @see Animation#play()
     * @see Animation#rewind()
     */
    @PublicAPI
    void tick();

    /**
     * Manually set the animation's tick progress to a certain point.
     *
     * @param progress A normalized float [0.0F, 1.0F] that indicates tick progress.
     */
    @PublicAPI
    void setTickProgress(float progress);

    /**
     * @return The current normalized tick progress of the animation.
     */
    @PublicAPI
    double getTickProgress();

    /**
     * Resets all animator fields back to their original state.
     */
    @PublicAPI
    void reset();

    /**
     * Stop the animation and mark it as "finished". This will unsubscribe the animator from the tick loop. The animator
     * {@link Animation#tick()} method performs automatic cache management; however, if the animation must terminate
     * abruptly, then invoke this method.
     */
    @PublicAPI
    void stop();

    /**
     * Any animation not subscribed to the tick loop is considered finished.
     *
     * @return Whether this animation is considered finished.
     */
    @PublicAPI
    boolean isFinished();

    /**
     * Any animation subscribed to the tick loop is considered not finished.
     *
     * @return Whether this animation is still running.
     */
    @PublicAPI
    default boolean isNotFinished()
    {
        return !this.isFinished();
    }

    /**
     * This will only return {@code true} when some progress has been made. If there is a need to check if the animation
     * has been set to go forward or backward use {@link #isNotFinished()}.
     *
     * @return Whether the animation has made progress.
     */
    @PublicAPI
    boolean isMoving();

    /**
     * This will yield {@code true} if the animator hasn't moved yet.
     *
     * @return Whether the animator was last moving forward.
     */
    @PublicAPI
    boolean wentForward();

    /**
     * This will yield {@code true} if the animator hasn't moved yet.
     *
     * @return Whether the animator was last moving backward.
     */
    @PublicAPI
    boolean wentBackward();

    /**
     * Rewind the animation so that the progress goes back to zero. The animation will only start and subscribe to the
     * tick loop if the current animation value is greater than zero.
     */
    @PublicAPI
    void rewind();

    /**
     * Play the animation. This will automatically subscribe this animator instance to the tick loop and/or undo a
     * previous {@link Animation#rewind()} and move the animator progression forward.
     */
    @PublicAPI
    void play();

    /**
     * If the animation has already played, then rewind. If the animation hasn't moved yet, then this will play the
     * animation. If the animation has not finished yet, then nothing will happen.
     */
    @PublicAPI
    default void playOrRewind()
    {
        if (this.isNotFinished())
            return;

        if (this.wentBackward())
            this.play();
        else if (this.wentForward())
            this.rewind();
    }

    /**
     * If the animation was rewound, then play it again. If the animation hasn't moved yet, then this will play the
     * animation. If the animation has not finished yet, then nothing will happen.
     */
    @PublicAPI
    default void rewindOrPlay()
    {
        if (this.isNotFinished())
            return;

        if (this.wentForward())
            this.rewind();
        else if (this.wentBackward())
            this.play();
    }

    /**
     * Define the animation function for this animator.
     *
     * @param animator A function that accepts a double between 0.0D and 1.0D and returns a value that should be between
     *                 0.0D and 1.0D. This bound is not enforced, and some animators may find it useful to go outside of
     *                 these bounds depending on the goal of the animation.
     */
    @PublicAPI
    void animateWith(Function<Double, Double> animator);

    /**
     * Define the duration of the animation. For example, a duration of <code>.setDuration(1.5L,
     * TimeUnit.SECONDS)</code> implies that the animation will not finish until 30 ticks have been counted
     * <code>1500 ms * (1 tick / 50 ms) = 30 ticks</code>.
     *
     * @param duration The amount of time this animation runs for.
     * @param timeUnit A {@link TimeUnit} enumeration value.
     */
    @PublicAPI
    void setDuration(long duration, TimeUnit timeUnit);

    /**
     * Set an object identifier that can be used to find this animator in the tick loop pool.
     *
     * @param identifier An {@link Object} instance.
     */
    @PublicAPI
    void setIdentifier(Object identifier);

    /**
     * @return The {@link Object} identifier used to find the animation in the tick loop pool.
     */
    @PublicAPI
    Object getIdentifier();
}
