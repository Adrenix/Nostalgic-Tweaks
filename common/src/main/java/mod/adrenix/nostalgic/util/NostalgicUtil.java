package mod.adrenix.nostalgic.util;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class NostalgicUtil
{
    public static class Resource
    {
        public static final ResourceLocation BLACK_RESOURCE = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/black.png");
        public static final ResourceLocation GEAR_LOGO = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gear.png");
        public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/widgets.png");
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
         * Determine if two integers are within a given tolerance.
         * @param a First integer.
         * @param b Second integer.
         * @param tolerance The allowed distance between a and b.
         * @return If the two integers are within the given tolerance.
         */
        public static boolean tolerance(int a, int b, int tolerance)
        {
            return Math.abs(a - b) < tolerance;
        }

        /**
         * This is an overload method for {@link Numbers#tolerance(int, int, int)} with a default tolerance of 3.
         * @param a First integer.
         * @param b Second integer.
         * @return If the two integers are within a tolerance of 3.
         */
        public static boolean tolerance(int a, int b)
        {
            return tolerance(a, b, 3);
        }
    }

    public static class Array
    {
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
            if (length < 3) {
                return in;
            }

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
