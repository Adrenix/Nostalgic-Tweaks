package mod.adrenix.nostalgic.util.common;

import java.util.Optional;

/**
 * Do <b>not</b> load <i>any</i> Minecraft code into this class. This utility is used by mixin plugins. If a vanilla
 * Minecraft class is loaded, then that class can't be modified by any mixins.
 */

public abstract class ClassUtil
{
    /**
     * Alternative to using <code>!(a instanceof A.class)</code>. This utility method makes code easier to read.
     * @param variable The variable to check if it is <b>not</b> an instance of the given class type.
     * @param classType The class type to compare.
     * @return Whether the given variable is <b>not</b> an instance of the given class type.
     */
    public static boolean isNotInstanceOf(Object variable, Class<?> classType)
    {
        return !classType.isInstance(variable);
    }

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
     * Attempt a class cast on the given object. If the given object is not an instance of the given class type then
     * a null value is returned.
     *
     * @param object An object to cast.
     * @param classType The class type to get the cast from.
     * @param <T> The class.
     * @return The object with a class type cast or null if the object is not an instance of the class type.
     */
    public static <T> Optional<T> cast(Object object, Class<T> classType)
    {
        return Optional.ofNullable(classType.isInstance(object) ? classType.cast(object) : null);
    }

    /**
     * Do <b>not</b> use this to check for optifine. Use the NostalgicTweaks#OPTIFINE memoized supplier.
     * @return Whether optifine is installed.
     */
    public static boolean isOptifinePresent() { return isClassPresent("net.optifine.Config"); }

    /**
     * @return Whether architectury is installed.
     */
    public static boolean isArchitecturyPresent() { return isClassPresent("dev.architectury.networking.NetworkChannel"); }
}
