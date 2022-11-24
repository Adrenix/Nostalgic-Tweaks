package mod.adrenix.nostalgic.util.common;

import net.minecraft.util.Mth;

public abstract class MathUtil
{

    /**
     * Turns given <code>bytes</code> into megabytes.
     * @param bytes The bytes to turn into megabytes.
     * @return Bytes turned into megabytes.
     */
    public static long bytesToMegabytes(long bytes) { return bytes / 1024L / 1024L; }

    /**
     * Checks if the given value is greater than or equal the given start or less than or equal to the given end.
     * @param value The integer to check.
     * @param start The starting integer.
     * @param end The ending integer.
     * @return Whether the given value was within range of the given start/end.
     */
    public static boolean isInRange(int value, int start, int end) { return value >= start && value <= end; }

    /**
     * Determine if two integers are within a given <code>tolerance</code>.
     * @param a First integer.
     * @param b Second integer.
     * @param tolerance The allowed distance between <code>a</code> and <code>b</code>.
     * @return If the two integers are within the given <code>tolerance</code>.
     */
    public static boolean tolerance(int a, int b, int tolerance) { return Math.abs(a - b) < tolerance; }

    /**
     * This is an overload method for {@link MathUtil#tolerance(int, int, int)} with a default <code>tolerance</code> of 3.
     * @param a First integer.
     * @param b Second integer.
     * @return If the two integers are within a <code>tolerance</code> of 3.
     */
    public static boolean tolerance(int a, int b) { return tolerance(a, b, 3); }

    /**
     * Checks if the two floats are within a given <code>tolerance</code>.
     * @param a First float.
     * @param b Second float.
     * @param tolerance The allowed distance between <code>a</code> and <code>b</code>.
     * @return If the two floats are within the given <code>tolerance</code>.
     */
    public static boolean tolerance(float a, float b, float tolerance) { return Math.abs(a - b) < tolerance; }

    /**
     * Checks if the two doubles are within a given <code>tolerance</code>.
     * @param a First double.
     * @param b Second double.
     * @param tolerance The allowed distance between <code>a</code> and <code>b</code>.
     * @return If the two doubles are within the given <code>tolerance</code>.
     */
    public static boolean tolerance(double a, double b, double tolerance) { return Math.abs(a - b) < tolerance; }

    /**
     * Checks if <code>a</code>, <code>b</code>, and <code>c</code> are within the given <code>tolerance</code>.
     * @param a The first float to compare.
     * @param b The second float to compare.
     * @param c The third float to compare.
     * @param tolerance The maximum distance each float can be from each other.
     * @return Whether all three floats are within the given <code>tolerance</code>
     */
    public static boolean tolerance(float a, float b, float c, float tolerance)
    {
        return tolerance(a, b, tolerance) && tolerance(b, c, tolerance);
    }

    /**
     * Checks if a point is within a bounding box.
     * @param pointX The x-point to check against.
     * @param pointY The y-point to check against.
     * @param startX The startX of the box.
     * @param startY The startY of the box.
     * @param width The width of the box. Do not include startX addition when providing a width.
     * @param height The height of the box. Do not include startY addition when providing a height.
     * @return Whether the point is within the box's boundaries.
     */
    public static boolean isWithinBox(double pointX, double pointY, double startX, double startY, double width, double height)
    {
        return pointX >= startX && pointX <= startX + width && pointY >= startY && pointY <= startY + height;
    }

    /**
     * Gets the sign of the given input.
     * @param input The input to check for its sign.
     * @return A <code>1</code> when the input is positive or zero, <code>-1</code> when the input is negative.
     */
    public static float sign(float input) { return input < 0.0F ? -1.0F : 1.0F; }

    /**
     * Overload method as <code>double</code> for {@link MathUtil#sign(float)}.
     */
    public static double sign(double input) { return input < 0.0D ? -1.0D : 1.0D; }

    /**
     * Checks if the distance between the <code>current</code> value and the <code>target</code> exceeds the given
     * speed (<code>delta</code>).
     * @param current The current value.
     * @param target The target value.
     * @param delta The change in value, or speed of movement.
     * @return Checks if the distance between the given points is greater than the given speed.
     */
    private static boolean isTargetImmediate(float current, float target, float delta)
    {
        return Math.abs(target - current) <= delta;
    }

    /**
     * Moves <code>current</code> towards <code>target</code>.
     * This is essentially {@link Mth#lerp(float, float, float) Mth.lerp(delta, start, end)} but instead the method
     * ensures that the speed never exceeds the given <code>delta</code>. Negative values of <code>delta</code>
     * pushes the value away from <code>target</code>.
     * @param current The current value.
     * @param target The value to move towards.
     * @param delta The change that should be applied to the value.
     * @return A point from the <code>current</code> value to the <code>target</code> value using the given <code>delta</code>.
     */
    public static float moveTowards(float current, float target, float delta)
    {
        return Math.abs(target - current) <= delta ? target : current + sign(target - current) * delta;
    }

    /**
     * Overload method as <code>double</code> for {@link MathUtil#moveTowards(float, float, float)}}.
     */
    public static double moveTowards(double current, double target, double delta)
    {
        return Math.abs(target - current) <= delta ? target : current + sign(target - current) * delta;
    }

    /**
     * Move <code>current</code> towards <code>target</code> while being clamped between the <code>min</code> and
     * <code>max</code>. See {@link MathUtil#moveTowards(float, float, float)} for more information on movement.
     * @param current The current value.
     * @param target The value to move towards.
     * @param delta The change that should be applied to the value.
     * @param min The minimum value allowed for this change.
     * @param max The maximum value allowed for this change.
     * @return A point from <code>current</code> value to the <code>target</code> value using the given <code>delta</code>
     * while being clamped between the <code>min</code> and <code>max</code>.
     */
    public static float moveClampTowards(float current, float target, float delta, float min, float max)
    {
        return Mth.clamp(moveTowards(current, target, delta), min, max);
    }

    /**
     * Overload method as <code>double</code> for {@link MathUtil#moveClampTowards(float, float, float, float, float)}.
     */
    public static double moveClampTowards(double current, double target, double delta, double min, double max)
    {
        return Mth.clamp(moveTowards(current, target, delta), min, max);
    }

    /**
     * Moves the <code>CURRENT_RGB</code> array towards the <code>TARGET_RGB</code>.
     * @param CURRENT_RGB The current color to move.
     * @param TARGET_RGB The target color to move the current color towards.
     * @param SPEED How quickly the current color moves towards the target color.
     */
    public static void moveTowardsColor(final float[] CURRENT_RGB, final float[] TARGET_RGB, final float SPEED)
    {
        CURRENT_RGB[0] = moveClampTowards(CURRENT_RGB[0], TARGET_RGB[0], SPEED, 0.0F, 1.0F);
        CURRENT_RGB[1] = moveClampTowards(CURRENT_RGB[1], TARGET_RGB[1], SPEED, 0.0F, 1.0F);
        CURRENT_RGB[2] = moveClampTowards(CURRENT_RGB[2], TARGET_RGB[2], SPEED, 0.0F, 1.0F);
    }

    /**
     * Moves the <code>CURRENT_RGB</code> array towards <b>grayscale</b> before moving the <code>CURRENT_RGB</code>
     * towards the <code>TARGET_RGB</code> array with the given speed.
     * @param CURRENT_RGB The current color to move.
     * @param TARGET_RGB The target color to move the current color towards.
     * @param SPEED How quickly the current color moves towards the target color.
     */
    public static void moveTowardsGrayscale(final float[] CURRENT_RGB, final float[] TARGET_RGB, final float SPEED)
    {
        boolean isR = tolerance(CURRENT_RGB[0], TARGET_RGB[0], 0.1F);
        boolean isG = tolerance(CURRENT_RGB[1], TARGET_RGB[1], 0.1F);
        boolean isB = tolerance(CURRENT_RGB[2], TARGET_RGB[2], 0.1F);
        boolean isImmediate = isTargetImmediate(CURRENT_RGB[0], TARGET_RGB[0], SPEED) || (isR && isG && isB);

        if (isImmediate || tolerance(CURRENT_RGB[0], CURRENT_RGB[1], CURRENT_RGB[2], 0.05F))
            moveTowardsColor(CURRENT_RGB, TARGET_RGB, SPEED);
        else
        {
            final float AVERAGE = (CURRENT_RGB[0] + CURRENT_RGB[1] + CURRENT_RGB[2]) / 3.0F;
            CURRENT_RGB[0] = moveClampTowards(CURRENT_RGB[0], AVERAGE, SPEED, 0.0F, 1.0F);
            CURRENT_RGB[1] = moveClampTowards(CURRENT_RGB[1], AVERAGE, SPEED, 0.0F, 1.0F);
            CURRENT_RGB[2] = moveClampTowards(CURRENT_RGB[2], AVERAGE, SPEED, 0.0F, 1.0F);
        }
    }

    /**
     * Get the largest number within the given varargs.
     * @param numbers The numbers to check.
     * @return The largest number.
     */
    public static int getLargest(int ...numbers)
    {
        int largest = Integer.MIN_VALUE;

        for (int number : numbers)
        {
            if (number > largest)
                largest = number;
        }

        return largest;
    }
}
