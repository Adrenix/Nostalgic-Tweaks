package mod.adrenix.nostalgic.util.common;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ModUtil
{
    public static class Resource
    {
        public static final ResourceLocation BLACK_RESOURCE = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/black.png");
        public static final ResourceLocation GEAR_LOGO = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gear.png");
        public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/widgets.png");
        public static final ResourceLocation COLOR_PICKER = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/overlay_picker.png");
        public static final ResourceLocation CATEGORY_LIST = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/overlay_list.png");
        public static final ResourceLocation OLD_INVENTORY = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/inventory.png");
        public static final ResourceLocation MOJANG_ALPHA = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/mojang_alpha.png");
        public static final ResourceLocation MOJANG_BETA = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/mojang_beta.png");
        public static final ResourceLocation MOJANG_RELEASE_ORANGE = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/mojang_release_orange.png");
        public static final ResourceLocation MOJANG_RELEASE_BLACK = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/mojang_release_black.png");
        public static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
    }

    public static class Link
    {
        public static final String DISCORD = "https://discord.gg/jWdfVh3";
        public static final String KO_FI = "https://ko-fi.com/adrenix";
        public static final String GOLDEN_DAYS = "https://github.com/PoeticRainbow/golden-days/releases";
    }

    public static class Run
    {
        /**
         * Used in loops that want to "simulate" work being done.
         * This is used mostly in progress screens.
         */
        public static void nothing() {}
    }

    public static class Numbers
    {
        /**
         * Determine if two integers are within a given <code>tolerance</code>.
         * @param a First integer.
         * @param b Second integer.
         * @param tolerance The allowed distance between <code>a</code> and <code>b</code>.
         * @return If the two integers are within the given <code>tolerance</code>.
         */
        public static boolean tolerance(int a, int b, int tolerance)
        {
            return Math.abs(a - b) < tolerance;
        }

        /**
         * This is an overload method for {@link Numbers#tolerance(int, int, int)} with a default <code>tolerance</code> of 3.
         * @param a First integer.
         * @param b Second integer.
         * @return If the two integers are within a <code>tolerance</code> of 3.
         */
        public static boolean tolerance(int a, int b)
        {
            return tolerance(a, b, 3);
        }

        /**
         * Checks if the two floats are within a given <code>tolerance</code>.
         * @param a First float.
         * @param b Second float.
         * @param tolerance The allowed distance between <code>a</code> and <code>b</code>.
         * @return If the two floats are within the given <code>tolerance</code>.
         */
        public static boolean tolerance(float a, float b, float tolerance)
        {
            return Math.abs(a - b) < tolerance;
        }

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
         * @param width The width of the box.
         * @param height The height of the box.
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
        public static float sign(float input)
        {
            return input < 0.0F ? -1.0F : 1.0F;
        }

        /**
         * Overload method as <code>double</code> for {@link Numbers#sign(float)}.
         */
        public static double sign(double input)
        {
            return input < 0.0D ? -1.0D : 1.0D;
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
         * Overload method as <code>double</code> for {@link Numbers#moveTowards(float, float, float)}}.
         */
        public static double moveTowards(double current, double target, double delta)
        {
            return Math.abs(target - current) <= delta ? target : current + sign(target - current) * delta;
        }

        /**
         * Move <code>current</code> towards <code>target</code> while being clamped between the <code>min</code> and
         * <code>max</code>. See {@link Numbers#moveTowards(float, float, float)} for more information on movement.
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
         * Overload method as <code>double</code> for {@link Numbers#moveClampTowards(float, float, float, float, float)}.
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
            boolean isTargeted = isR && isG && isB;

            if (isTargeted || tolerance(CURRENT_RGB[0], CURRENT_RGB[1], CURRENT_RGB[2], 0.05F))
                moveTowardsColor(CURRENT_RGB, TARGET_RGB, SPEED);
            else
            {
                final float AVERAGE = (CURRENT_RGB[0] + CURRENT_RGB[1] + CURRENT_RGB[2]) / 3.0F;
                CURRENT_RGB[0] = moveClampTowards(CURRENT_RGB[0], AVERAGE, SPEED, 0.0F, 1.0F);
                CURRENT_RGB[1] = moveClampTowards(CURRENT_RGB[1], AVERAGE, SPEED, 0.0F, 1.0F);
                CURRENT_RGB[2] = moveClampTowards(CURRENT_RGB[2], AVERAGE, SPEED, 0.0F, 1.0F);
            }
        }
    }

    public static class Array
    {
        /**
         * Copies the values <code>from</code> one array <code>to</code> another array.
         * @param from Array to copy values from.
         * @param to Array to paste values to.
         */
        public static void copy(float[] from, float[] to)
        {
            if (from.length != to.length)
                throw new AssertionError("Both arrays must be the same size to copy");

            System.arraycopy(from, 0, to, 0, from.length);
        }

        /**
         * Safely get an item from an array without incurring an out-of-bounds exception.
         * @param array The array to get the item from.
         * @param index The index the item should be at.
         * @param <T> The item from the given index.
         * @return Item at the given index within the array. Will be null if the index is out-of-bounds.
         */
        @Nullable
        public static <T> T get(T[] array, int index)
        {
            int bound = array.length - 1;

            if (index <= bound && index >= 0)
                return array[index];
            return null;
        }
    }

    public static class Text
    {
        /**
         * Converts a string into a title case format. Handles space and underscore delimiters.
         * @param convert The string to convert.
         * @return A string that is in title case format.
         */
        public static String toTitleCase(String convert)
        {
            String delimiters = " _";
            StringBuilder builder = new StringBuilder(convert.toLowerCase());
            boolean next = true;

            for (int i = 0; i < builder.length(); i++)
            {
                char c = builder.charAt(i);

                c = next ? Character.toUpperCase(c) : Character.toLowerCase(c);
                builder.deleteCharAt(i);
                builder.replace(i, i, String.valueOf(c));
                next = delimiters.indexOf(c) >= 0;

                if (next)
                {
                    builder.deleteCharAt(i);
                    builder.replace(i, i, " ");
                }
            }

            return builder.toString();
        }

        /**
         * Checks if the given hexadecimal string is a valid (0-9, A-F) entry.
         * This does not remove any prefixed "#" tags.
         * @param hex The hexadecimal to check.
         * @return Whether the input is a valid hexadecimal.
         */
        public static boolean isHexValid(String hex)
        {
            try { Integer.parseInt(hex, 16); }
            catch (NumberFormatException ignored) { return false; }
            return true;
        }

        /**
         * Converts a hexadecimal string (e.g., #8B8B8BFF) into an array of integers (e.g., [139, 139, 139, 255]).
         * Any invalid input strings will have a default array of [255, 255, 255, 255].
         * @param convert The hexadecimal string to convert.
         * @return An array of base 16 integers.
         */
        public static int[] toHexRGBA(String convert)
        {
            int[] rgba = { 0xFF, 0xFF, 0xFF, 0xFF };
            convert = convert.replaceAll("#", "");

            if (convert.length() != 6 && convert.length() != 8)
                return rgba;

            String[] hex = splitInTwo(convert);

            for (int i = 0; i < hex.length; i++)
            {
                if (isHexValid(hex[i]))
                    rgba[i] = Integer.parseInt(hex[i], 16);
            }

            return rgba;
        }

        /**
         * Converts a hexadecimal string (e.g., #8B8B8BFF) into an ARGB integer (e.g., -7631989).
         * Any invalid input strings will have a malformed color.
         * @param convert The hexadecimal string to convert.
         * @return An ARGB integer.
         */
        public static int toHexInt(String convert)
        {
            int[] hex = toHexRGBA(convert);
            int r = hex[0];
            int g = hex[1];
            int b = hex[2];
            int a = hex[3];

            return a << 24 | r << 16 | g << 8 | b;
        }

        /**
         * Converts an array of RGBA integers [0-255] to a hex string. The # is prefixed before creating the string.
         * Each integer in the RGBA array is range checked before conversion.
         * @param rgba The array of RGBA integers to convert.
         * @return A hex string (e.g., #8B8B8BFF)
         */
        public static String toHexString(int[] rgba)
        {
            StringBuilder hex = new StringBuilder("#");

            for (int color : rgba)
            {
                if (color >= 0 && color <= 255)
                    hex.append(color <= 15 ? "0" : "").append(Integer.toHexString(color).toUpperCase());
            }

            return hex.toString();
        }

        /* Private utilities for hexadecimal string conversion */

        private static String[] splitInTwo(String convert)
        {
            String[] split = { "FF", "FF", "FF", "FF" };
            String regex = "(..)";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(convert);

            int i = -1;
            while (matcher.find())
                split[++i] = matcher.group(0);

            return split;
        }

        /**
         * Removes the last three characters of a string and replaces them with ellipsis points.
         * Any current ellipsis points will be removed before applying another set of ellipsis points.
         * @param in The string to ellipsis.
         * @return A string with ellipsis points.
         */
        public static String ellipsis(String in)
        {
            if (in == null)
                return "";

            in = in.replaceAll("\\.\\.\\.", "");

            int length = in.length();
            if (length < 3)
                return in;

            return in.substring(0, length - 3) + "...";
        }

        /**
         * Combine multiple Minecraft {@link Component Components} into one single component.
         * @param lines An array of {@link Component Components}.
         * @return One single {@link Component}
         */
        public static Component combine(Component[] lines)
        {
            StringBuilder builder = new StringBuilder();
            for (Component component : lines) builder.append(component.getString()).append("\n\n");
            return Component.literal(builder.toString());
        }
    }

    public static class Wrap
    {
        /**
         * Text wrap a Minecraft {@link Component} into an array of multiple {@link Component Components}
         * with the given width.
         * @param translation A {@link Component}.
         * @param width How long each line should be.
         * @return An array of {@link Component Components}.
         */
        public static List<Component> tooltip(Component translation, int width)
        {
            String translated = translation.getString();
            ArrayList<String> lines = wrap(translated, width);

            List<Component> components = new ArrayList<>();
            lines.forEach((line) -> components.add(Component.literal(line)));

            return components;
        }

        /**
         * Text wrapping helper methods.
         */

        private static ArrayList<String> wrap(String string, int lineLength)
        {
            ArrayList<String> processed = new ArrayList<>();
            ArrayList<String> lines = new ArrayList<>();
            String last = "";

            for (String line : string.split(Pattern.quote("\n")))
                processed.add(inspect(line, lineLength));

            for (String row : processed)
            {
                for (String line : row.split(Pattern.quote("\n")))
                {
                    lines.add(getCodes(last) + line.trim());
                    last = lines.get(lines.size() - 1);
                }
            }

            return lines;
        }

        private static String getCodes(String row)
        {
            Pattern pattern = Pattern.compile(".*(§.)");
            Matcher matcher = pattern.matcher(row);
            if (matcher.find())
                return matcher.group(1);
            return "";
        }

        private static String inspect(String line, int lineLength) {
            if (line.length() == 0) return " \n";
            if (line.length() <= lineLength) return line + "\n";

            String[] words = line.split(" ");
            StringBuilder lineBuilder = new StringBuilder();
            StringBuilder trimBuilder = new StringBuilder();
            StringBuilder stripBuilder = new StringBuilder();

            for (String word : words)
            {
                String stripped = word.replaceAll("§.", "");

                if (stripBuilder.length() + 1 + stripped.length() <= lineLength)
                {
                    trimBuilder.append(word).append(" ");
                    stripBuilder.append(stripped).append(" ");
                }
                else
                {
                    lineBuilder.append(trimBuilder).append("\n");
                    trimBuilder = new StringBuilder();
                    trimBuilder.append(word).append(" ");

                    stripBuilder = new StringBuilder();
                    stripBuilder.append(stripped).append(" ");
                }
            }

            if (stripBuilder.length() > 0)
                lineBuilder.append(trimBuilder);

            return lineBuilder.toString();
        }
    }
}
