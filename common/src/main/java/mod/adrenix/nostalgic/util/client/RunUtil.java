package mod.adrenix.nostalgic.util.client;

import net.minecraft.client.Minecraft;
import java.util.ArrayList;

/**
 * Config Runnables:
 *
 * Some tweaks require more work for a change to take place.
 *
 * For example, the star buffer needs to be redone if the old stars tweak is toggled.
 * A runnable is defined in the LevelRendererMixin class that provides instructions on what to do when a change is
 * made to the tweak.
 *
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 */

public abstract class RunUtil
{
    /**
     * Used in loops that want to "simulate" work being done.
     * This is used in the progress screen.
     */
    public static void nothing() {}

    /**
     * This is an array list of functions to run after the user updates the config values saved on disk.
     * The standard runnables are defined in the static block below. Other runnables are defined elsewhere.
     */
    public static final ArrayList<Runnable> onSave = new ArrayList<>();

    /**
     * This flag controls whether chunks should be reloaded after the config has been saved.
     * To prevent this from occurring after every save, only tweaks that require chunk reloading will toggle this flag.
     */
    public static boolean reloadChunks = false;

    /**
     * This flag controls whether the game's resources should be reloaded after the config has been saved.
     * To prevent this from occurring after every save, only tweaks that require reloaded resource will toggle this flag.
     */
    public static boolean reloadResources = false;

    /* Standard Reload Runnables */
    static
    {
        onSave.add(() ->
        {
            Minecraft minecraft = Minecraft.getInstance();

            if (reloadResources)
            {
                reloadResources = false;
                reloadChunks = false;
                minecraft.reloadResourcePacks();
            }
            else if (reloadChunks)
            {
                reloadChunks = false;
                minecraft.levelRenderer.allChanged();
            }
        });
    }
}
