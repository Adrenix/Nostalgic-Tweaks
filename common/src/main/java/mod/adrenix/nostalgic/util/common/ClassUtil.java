package mod.adrenix.nostalgic.util.common;

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
     * Do not use to check for optifine. Use the NostalgicTweaks#OPTIFINE memoized supplier.
     * @return Whether the optifine config class was found.
     */
    public static boolean isOptifinePresent() { return isClassPresent("net.optifine.Config"); }

    /**
     * @return Whether autoconfig is on the classpath.
     */
    public static boolean isAutoPresent() { return isClassPresent("me.shedaniel.autoconfig.AutoConfig"); }

    /**
     * @return Whether architectury is on the classpath.
     */
    public static boolean isArchitectPresent() { return isClassPresent("dev.architectury.networking.NetworkChannel"); }
}
