package mod.adrenix.nostalgic.client;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

/**
 * Some tweaks require more work for a change to take place. For example, the star buffer needs to be redone if the old
 * stars tweak is toggled. A runnable method is defined in the {@code LevelRendererMixin} class that provides
 * instructions on what to do when a change is made to the tweak. Only the client can use this utility.
 */
public abstract class AfterConfigSave
{
    /* Fields */

    /**
     * This is an array list of runnables to run after the user updates the config values saved on disk.
     */
    private static final ArrayList<Runnable> RUNNABLES = new ArrayList<>();

    /**
     * This flag controls whether chunks should be reloaded after the config has been saved.
     */
    private static boolean reloadChunks = false;

    /**
     * This flag controls whether the game's resources should be reloaded after the config has been saved.
     */
    private static boolean reloadResources = false;

    /* Methods */

    /**
     * Add instructions to run after the config is saved on the client.
     *
     * @param runnable A {@link Runnable} to add.
     */
    public static void addInstruction(Runnable runnable)
    {
        RUNNABLES.add(runnable);
    }

    /**
     * Reload the client level chunks after reload instructions have executed.
     */
    public static void setChunksToReload()
    {
        reloadChunks = true;
    }

    /**
     * Reload the client's resources after reload instructions have executed.
     */
    public static void setResourcesToReload()
    {
        reloadResources = true;
    }

    /**
     * @return Whether the client's resources are going to reload after {@link #run()} is executed.
     */
    public static boolean areResourcesGoingToReload()
    {
        return reloadResources;
    }

    /**
     * Performs instructions to run after the config cache has been saved to disk on the client.
     */
    public static void run()
    {
        RUNNABLES.forEach(Runnable::run);
        NostalgicTweaks.LOGGER.debug("Ran (%s) save functions", RUNNABLES.size());

        if (reloadResources)
        {
            reloadResources = false;
            reloadChunks = false;
            Minecraft.getInstance().reloadResourcePacks();
        }
        else if (reloadChunks)
        {
            reloadChunks = false;
            Minecraft.getInstance().levelRenderer.allChanged();
        }
    }

    /**
     * Reloads the client's chunks and resources and runs all after save instructions.
     */
    public static void reloadAndRun()
    {
        reloadChunks = true;
        reloadResources = true;

        run();
    }
}
