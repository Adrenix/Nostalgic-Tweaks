package mod.adrenix.nostalgic.util;

import com.google.common.base.Suppliers;
import mod.adrenix.nostalgic.util.common.ClassUtil;

import java.util.function.Function;
import java.util.function.Supplier;

public enum ModTracker
{
    APPLE_SKIN("appleskin"),
    SODIUM("sodium"),
    OPTIFINE("optifine"),
    FLYWHEEL("flywheel");

    /* Fields */

    private final String id;
    private boolean installed;

    /* Constructor */

    ModTracker(String id) { this.id = id; }

    /* Methods */

    /**
     * This field tracks whether Optifine is installed. This is a Forge mod, but has a Fabric relative known as Opti-
     * Fabric. A supplier is needed since a hacky class check is required to determine if Optifine is present. This
     * hacky check is needed because Optifine is a closed-source mod.
     */
    private static final Supplier<Boolean> OPTIFINE_SUPPLIER = Suppliers.memoize(ClassUtil::isOptifinePresent);

    /**
     * Checks if the mod is installed using an applicable mod loader function.
     * @param loader A function that accepts a mod id and returns whether the mod id was found in the mod folder.
     */
    public void load(Function<String, Boolean> loader) { this.installed = loader.apply(this.id); }

    /**
     * Checks if the mod is installed. This will not return a truthful value if the mod scanning process has not yet
     * occurred.
     *
     * The Optifine mod needs a special of way of checking if it is installed. This may not always return a correct
     * value if the class that is checked is changed by Optifine in the future.
     *
     * @return Whether the mod is installed.
     */
    public boolean isInstalled()
    {
        if (this.equals(ModTracker.OPTIFINE))
            return OPTIFINE_SUPPLIER.get();

        return this.installed;
    }
}
