package mod.adrenix.nostalgic.util.common;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TextUtil
{
    /**
     * Extract strings from another string with the given regex. If nothing was found, then an empty string will be the
     * only entry in the returned array list.
     *
     * @param from The string to extract data from.
     * @param regex The regex pattern to use.
     * @return An array list of matched strings or a list with a single empty string entry if nothing was found.
     */
    public static ArrayList<String> extractAll(String from, String regex)
    {
        ArrayList<String> results = new ArrayList<>();

        Pattern.compile(regex)
            .matcher(from)
            .results()
            .map(match -> match.group(0))
            .forEach(results::add)
        ;

        if (results.isEmpty())
            results.add("");

        return results;
    }

    /**
     * Shortcut for the {@link TextUtil#extractAll(String, String)} method where only the first entry in the array list
     * will be returned. If nothing was found, then an empty string is returned.
     *
     * @param from The string to extract data from.
     * @param regex The regex pattern to use.
     * @return The first string matched by the regex, otherwise an empty string.
     */
    public static String extract(String from, String regex) { return extractAll(from, regex).get(0); }

    /**
     * Apply a Minecraft color section code to the given percent based on the given percent's value. The higher the
     * percentage value is, the more <font color="red">dangerous</font> the color will appear. A reset section code will
     * be applied to the end of the returned string.
     *
     * @param percent The percent value to check (<font color="green">0</font>-<font color="red">100</font>).
     * @return The given percent value with a Minecraft color § code attached and a reset section code appended.
     */
    public static String getPercentColorHigh(int percent)
    {
        if (percent < 20)
            return "§a" + percent + "§r";
        else if (percent < 40)
            return "§2" + percent + "§r";
        else if (percent < 60)
            return "§e" + percent + "§r";
        else if (percent < 80)
            return "§6" + percent + "§r";
        else if (percent < 100)
            return "§c" + percent + "§r";

        return "§4" + percent + "§r";
    }

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

        for (Component component : lines)
            builder.append(component.getString()).append("\n\n");

        return ComponentBackport.literal(builder.toString());
    }

    /**
     * Helper class that includes methods that wrap text.
     * This utility is currently only used for tooltips.
     */

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
            lines.forEach((line) -> components.add(ComponentBackport.literal(line)));

            return components;
        }

        /**
         * Wrap a string based on the given line length.
         * @param string The string to wrap.
         * @param lineLength The length of each line.
         * @return An array list of wrapped strings.
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

        /**
         * Find Minecraft section color code identifiers within a string.
         * @param row The string to check.
         * @return A string with processed color codes otherwise an empty string.
         */
        private static String getCodes(String row)
        {
            Pattern pattern = Pattern.compile(".*(§.)");
            Matcher matcher = pattern.matcher(row);

            if (matcher.find())
                return matcher.group(1);

            return "";
        }

        /**
         * Examine a string to see if it exceeds the given line length.
         * @param line A string to check.
         * @param lineLength The maximum character length of each line.
         * @return A processed line string.
         */
        private static String inspect(String line, int lineLength) {
            if (line.length() == 0)
                return " \n";

            if (line.length() <= lineLength)
                return line + "\n";

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
