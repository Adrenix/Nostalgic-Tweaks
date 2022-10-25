package mod.adrenix.nostalgic.util.common;

/**
 * Do <b>not</b> load <i>any</i> Minecraft code into this class. This utility is used by mixin plugins. If a vanilla
 * Minecraft class is loaded, then that class can't be modified by any mixins.
 */

public abstract class ClassUtil
{
    /**
     * Checks if a class path is loaded.
     * @param path The full-path of the class.
     * @return Whether the class is loaded.
     */
    public static boolean isClassPresent(String path)
    {
        try
        {
            Class.forName(path);
            return true;
        }
        catch (ClassNotFoundException ignored)
        {
            return false;
        }
    }

    /**
     * Do <b>not</b> use this to check for optifine. Use the NostalgicTweaks#OPTIFINE memoized supplier.
     * @return Whether optifine is installed.
     */
    public static boolean isOptifinePresent() { return isClassPresent("net.optifine.Config"); }

    /**
     * @return Whether autoconfig is installed.
     */
    public static boolean isAutoConfigPresent() { return isClassPresent("me.shedaniel.autoconfig.AutoConfig"); }

    /**
     * @return Whether architectury is installed.
     */
    public static boolean isArchitecturyPresent() { return isClassPresent("dev.architectury.networking.NetworkChannel"); }
}
