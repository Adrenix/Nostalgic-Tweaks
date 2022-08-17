package mod.adrenix.nostalgic.util.common.log;

import mod.adrenix.nostalgic.NostalgicTweaks;

public enum LogColor
{
    BLACK("\u001B[0;30m", "0"),          // Black §0
    DARK_BLUE("\u001B[0;34m", "1"),      // Dark Blue §1
    DARK_GREEN("\u001B[0;32m", "2"),     // Dark Green §2
    DARK_AQUA("\u001B[0;36m", "3"),      // Dark Aqua §3
    DARK_RED("\u001B[0;31m", "4"),       // Dark Red §4
    DARK_PURPLE("\u001B[0;35m", "5"),    // Dark Purple §5
    GOLD("\u001B[0;33m", "6"),           // Gold §6
    GRAY("\u001B[0;37m", "7"),           // Gray §7
    DARK_GRAY("\u001B[0;30;1m", "8"),    // Dark Gray §8
    BLUE("\u001B[0;34;1m", "9"),         // Blue §9
    GREEN("\u001B[0;32;1m", "a"),        // Green §a
    AQUA("\u001B[0;36;1m", "b"),         // Aqua §b
    RED("\u001B[0;31;1m", "c"),          // Red §c
    LIGHT_PURPLE("\u001B[0;35;1m", "d"), // Light Purple §d
    YELLOW("\u001B[0;33;1m", "e"),       // Yellow §e
    WHITE("\u001B[0;37;1m", "f"),        // White §f

    OBFUSCATED("\u001B[5m", "k"),        // Obfuscated §k
    BOLD("\u001B[1m", "l"),              // Bold §l
    STRIKETHROUGH("\u001B[9m", "m"),     // Strikethrough §m
    UNDERLINE("\u001B[4m", "n"),         // Underline §n
    ITALIC("\u001B[3m", "o"),            // Italic §o

    RESET("\u001B[m", "r");              // Reset §r

    LogColor(String ansi, String mc)
    {
        this.ansi = ansi;
        this.mc = mc;
    }

    public static String apply(LogColor color, String to) { return color + to + LogColor.RESET; }
    private final String ansi;
    private final String mc;
    public String convert(String in) { return NostalgicTweaks.isDevelopmentEnvironment() ? in.replaceAll("§" + this.mc, this.ansi) : in; }
    @Override public String toString() { return NostalgicTweaks.isDevelopmentEnvironment() ? this.ansi : ""; }
}
