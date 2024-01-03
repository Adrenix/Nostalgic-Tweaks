package mod.adrenix.nostalgic.util.client;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

/**
 * <h4>Config Runnables</h4>
 * Some tweaks require more work for a change to take place. For example, the star buffer needs to be redone if the old
 * stars tweak is toggled. A runnable is defined in the {@code LevelRendererMixin} class that provides instructions on
 * what to do when a change is made to the tweak. This utility class uses the Minecraft client.
 */
public abstract class RunUtil
{
    /**
     * This is an array list of runnables to run after the user updates the config values saved on disk. The standard
     * runnables are defined in the static block below. Other runnables are defined elsewhere.
     */
    public static final ArrayList<Runnable> ON_SAVE_RUNNABLES = new ArrayList<>();

    /**
     * This flag controls whether chunks should be reloaded after the config has been saved. To prevent this from
     * occurring after every save, only tweaks that require chunk reloading will toggle this flag.
     */
    public static boolean reloadChunks = false;

    /**
     * This flag controls whether the game's resources should be reloaded after the config has been saved. To prevent
     * this from occurring after every save, only tweaks that require reloaded resource will toggle this flag.
     */
    public static boolean reloadResources = false;

    /**
     * Run all {@link #ON_SAVE_RUNNABLES}.
     */
    public static void onSave()
    {
        RunUtil.ON_SAVE_RUNNABLES.forEach(Runnable::run);
        NostalgicTweaks.LOGGER.debug("Ran (%s) save functions", ON_SAVE_RUNNABLES.size());
    }

    /**
     * Reloads the client and applies any applicable saving runnables.
     */
    public static void reload()
    {
        RunUtil.reloadChunks = true;
        RunUtil.reloadResources = true;

        onSave();
    }

    /* Standard Reload Runnables */

    static
    {
        ON_SAVE_RUNNABLES.add(() -> {
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
