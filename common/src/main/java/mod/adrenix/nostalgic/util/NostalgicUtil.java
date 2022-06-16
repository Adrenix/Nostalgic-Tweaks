package mod.adrenix.nostalgic.util;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

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
        public static final String GOLDEN_DAYS = "https://github.com/PoeticRainbow/golden-days";
    }

    public static class Run
    {
        public static void nothing() {}
    }

    public static class Numbers
    {
        public static boolean tolerance(int a, int b, int tolerance)
        {
            return Math.abs(a - b) < tolerance;
        }

        public static boolean tolerance(int a, int b)
        {
            return tolerance(a, b, 3);
        }
    }

    public static class Text
    {
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

        public static Component combine(Component[] lines)
        {
            StringBuilder builder = new StringBuilder();
            for (Component component : lines) builder.append(component.getString()).append("\n\n");
            return new TextComponent(builder.toString());
        }
    }

    public static class Wrap
    {
        public static List<Component> tooltips(Component translation, int width)
        {
            String translated = translation.getString();
            ArrayList<String> lines = wrap(translated, width);

            List<Component> components = new ArrayList<>();
            lines.forEach((line) -> components.add(new TextComponent(line)));

            return components;
        }

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
